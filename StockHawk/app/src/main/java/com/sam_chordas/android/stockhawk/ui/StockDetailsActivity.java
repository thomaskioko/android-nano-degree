package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.interfaces.HistoricalDataCallback;
import com.sam_chordas.android.stockhawk.models.HistoricData;
import com.sam_chordas.android.stockhawk.models.StockMeta;
import com.sam_chordas.android.stockhawk.models.StockSymbol;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity to display stock details
 */
public class StockDetailsActivity extends AppCompatActivity implements HistoricalDataCallback {

    @Bind(R.id.lineChart)
    LineChart mLineChart;
    @Bind(R.id.ll_stock_details)
    LinearLayout mLinearLayout;
    @Bind(R.id.tvStockName)
    TextView mTvStockName;
    @Bind(R.id.tvStockSymbol)
    TextView mTvStockSymbol;
    @Bind(R.id.tvfirstTrade)
    TextView mTvFirstTradeDate;
    @Bind(R.id.tvlastTrade)
    TextView mTvLastTradeDate;
    @Bind(R.id.tvCurrency)
    TextView mTvCurrencyCode;
    @Bind(R.id.tvBidPrice)
    TextView mTvBidPrice;
    @Bind(R.id.tvExchangeName)
    TextView mTvExchangeName;
    @Bind(R.id.tvStockTitleName)
    TextView mTvStockTitleName;
    @Bind(R.id.tvSymbol)
    TextView mTvSymbol;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    private String symbol, bidPrice;
    private HistoricData historicData;
    private static final String TAG = StockDetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }

        //Getting Values from intents
        symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);
        bidPrice = getIntent().getStringExtra(QuoteColumns.BIDPRICE);

        mLineChart.setNoDataText(getString(R.string.loading_stock_data));
        mLineChart.setBorderColor(getResources().getColor(R.color.primary_text_color));

        //Setting values to the text
        mTvStockSymbol.setText(symbol);
        mTvBidPrice.setText(bidPrice);
        historicData = new HistoricData(this, this);

        if (Utils.isNetworkAvailable(this)) {
            historicData.getHistoricData(symbol);
        } else {
            historicData.setHistoricalDataStatus(HistoricData.STATUS_ERROR_NO_NETWORK);
            onFailure();
        }

    }

    @Override
    public void onSuccess(StockMeta stockMeta) {
        ArrayList<StockSymbol> stockSymbolArrayList = stockMeta.stockSymbols;

        mTvStockName.setText(stockMeta.companyName);
        mTvFirstTradeDate.setText(Utils.convertDate(stockMeta.firstTrade));
        mTvLastTradeDate.setText(Utils.convertDate(stockMeta.lastTrade));
        mTvCurrencyCode.setText(stockMeta.currency);
        mTvExchangeName.setText(stockMeta.exchangeName);
        mTvStockTitleName.setText(stockMeta.companyName);
        mTvSymbol.setText(getResources().getString(R.string.symbol_details, symbol, stockMeta.currency, bidPrice));

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();

        for (int i = 0; i < stockSymbolArrayList.size(); i++) {
            StockSymbol stockSymbol = stockSymbolArrayList.get(i);
            double yValue = stockSymbol.close;

            xvalues.add(Utils.convertDate(stockSymbol.date));
            entries.add(new Entry((float) yValue, i));
        }

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setEnabled(true);
        axisLeft.setLabelCount(5, true);

        xAxis.setTextColor(Color.BLACK);
        axisLeft.setTextColor(Color.BLACK);

        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getLegend().setTextSize(12f);

        LineDataSet dataSet = new LineDataSet(entries, symbol);
        LineData lineData = new LineData(xvalues, dataSet);

        mLineChart.setDescriptionColor(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setColor(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        lineData.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));

        lineData.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setCircleColorHole(getResources().getColor(R.color.colorAccent));
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimaryDark));

        mLineChart.setDescription(getString(R.string.last_12month_comparision));
        mLineChart.setData(lineData);
        mLineChart.animateX(3000);
    }

    @Override
    public void onFailure() {
        String errorMessage = "";

        @HistoricData.HistoricalDataStatuses
        int status = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(HistoricData.HISTORICAL_DATA_STATUS, -1);

        switch (status) {
            case HistoricData.STATUS_ERROR_JSON:
                errorMessage += getString(R.string.data_error_json);
                break;
            case HistoricData.STATUS_ERROR_NO_NETWORK:
                errorMessage += getString(R.string.data_no_internet);
                break;
            case HistoricData.STATUS_ERROR_PARSE:
                errorMessage += getString(R.string.data_error_parse);
                break;
            case HistoricData.STATUS_ERROR_UNKNOWN:
                errorMessage += getString(R.string.data_unknown_error);
                break;
            case HistoricData.STATUS_ERROR_SERVER:
                errorMessage += getString(R.string.data_server_down);
                break;
            case HistoricData.STATUS_OK:
                errorMessage += getString(R.string.data_no_error);
                break;
            default:
                break;
        }

        mLineChart.setNoDataText(errorMessage);

        final Snackbar snackbar = Snackbar
                .make(mLinearLayout, getString(R.string.no_data_show) + errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historicData.getHistoricData(symbol);
                    }
                })
                .setActionTextColor(Color.GREEN);

        View subview = snackbar.getView();
        TextView tv = (TextView) subview.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
