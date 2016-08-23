package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.sam_chordas.android.stockhawk.ui.adapter.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.widget.StockQuoteWidget;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 *  Activity called when the application first starts.
 */
public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.tvErrorMessage)
    TextView mTvErrorMessage;
    @Bind(R.id.ivErrorImage)
    ImageView mIvErrorImage;

    private CharSequence mTitle;
    private Intent mServiceIntent;
    private QuoteCursorAdapter mQuoteCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    private static final int CURSOR_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_my_stocks);
        ButterKnife.bind(this);

        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "init");
            if (Utils.isNetworkAvailable(mContext)) {
                startService(mServiceIntent);
            } else {
                networkSnackBar();
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        mQuoteCursorAdapter = new QuoteCursorAdapter(this, null);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (mCursor.moveToPosition(position)) {
                            Intent intent = new Intent(mContext, StockDetailsActivity.class);
                            intent.putExtra(QuoteColumns.SYMBOL, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
                            intent.putExtra(QuoteColumns.BIDPRICE, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                            startActivity(intent);
                        }
                    }
                }));
        mRecyclerView.setAdapter(mQuoteCursorAdapter);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isNetworkAvailable(mContext)) {
                        new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                                .content(R.string.content_test)
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {

                                        //make the stock symbols capital letter
                                        String newSymbol = input.toString().toUpperCase();

                                        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                                new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                                new String[]{newSymbol}, null);

                                        if (cursor != null) {
                                            if (cursor.getCount() != 0) {
                                                Snackbar.make(
                                                        coordinatorLayout,
                                                        getString(R.string.stock_already_saved),
                                                        Snackbar.LENGTH_SHORT
                                                ).show();
                                            } else {
                                                // Add the stock to DB
                                                mServiceIntent.putExtra("tag", "add");
                                                mServiceIntent.putExtra("symbol", newSymbol);
                                                startService(mServiceIntent);
                                            }
                                        }
                                    }
                                })
                                .show();
                    } else {
                        networkSnackBar();
                    }

                }
            });
        }

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mQuoteCursorAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mTitle = getTitle();
        if (Utils.isNetworkAvailable(mContext)) {
            long period = 3600L;
            long flex = 10L;
            String periodicTag = "periodic";

            // create a periodic task to pull stocks once every hour after the app has been opened. This
            // is so Widget data stays up to date.
            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(StockTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    public void networkSnackBar() {
        Snackbar.make(coordinatorLayout, getString(R.string.network_toast), Snackbar.LENGTH_SHORT).show();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_change_units) {
            // this is for changing stock changes from percent value to dollar value
            Utils.showPercent = !Utils.showPercent;
            this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuoteCursorAdapter.swapCursor(data);
        mCursor = data;
        //Update view Status
        updateEmptyView();
        //update Widget
        updateStocksWidget();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuoteCursorAdapter.swapCursor(null);
    }


    /**
     * method to update textview and icon based on the response from there server
     */
    public void updateEmptyView() {
        if (mQuoteCursorAdapter.getItemCount() == 0) {
            if (!Utils.isNetworkAvailable(mContext)) {
                mTvErrorMessage.setText(getResources().getString(R.string.no_internet_connection));
                mIvErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloud_white_124dp));

            } else {
                mTvErrorMessage.setText(getResources().getString(R.string.no_internet_connection));
                mIvErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_attach_money_white_24dp));

            }
        } else {
            mTvErrorMessage.setVisibility(View.GONE);
            mIvErrorImage.setVisibility(View.GONE);
        }
    }

    /**
     * Method to update widget
     */
    private void updateStocksWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext.getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, StockQuoteWidget.class));
        if (ids.length > 0) {
            /**
             * notifyAppWidgetViewDataChanged() method will call the onDataSetChanged method of the
             * #{@link com.sam_chordas.android.stockhawk.widget.StockQuoteWidgetService.StockRVFactory} class.
             */
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        }
    }

}
