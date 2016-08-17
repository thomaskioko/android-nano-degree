package com.thomaskioko.sunshine.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.data.WeatherContract;
import com.thomaskioko.sunshine.util.AppConstants;
import com.thomaskioko.sunshine.util.RecyclerItemChoiceManager;
import com.thomaskioko.sunshine.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ForecastAdapter subclass of CursorAdapter
 *
 * @author Thomas Kioko
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    private Cursor mCursor;
    final private Context mContext;
    final private ForecastAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private RecyclerItemChoiceManager mRecyclerItemChoiceManager;

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.list_item_icon)
        public ImageView mIconView;
        @Bind(R.id.list_item_date_textview)
        TextView mDateView;
        @Bind(R.id.list_item_forecast_textview)
        TextView mDescriptionView;
        @Bind(R.id.list_item_high_textview)
        TextView mHighTempView;
        @Bind(R.id.list_item_low_textview)
        TextView mLowTempView;

        /**
         * Constructor
         *
         * @param view View
         */
        public ForecastAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
            mRecyclerItemChoiceManager.onClick(this);
        }
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ForecastAdapterViewHolder vh);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler dh, View emptyView, int choiceMode) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mRecyclerItemChoiceManager = new RecyclerItemChoiceManager(this);
        mRecyclerItemChoiceManager.setChoiceMode(choiceMode);
    }

    /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.

        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the RecyclerItemChoiceManager.
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        int weatherId = mCursor.getInt(AppConstants.COL_WEATHER_CONDITION_ID);
        int defaultImage;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                defaultImage = StringUtils.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = StringUtils.getIconResourceForWeatherCondition(weatherId);
        }

        if (StringUtils.usingLocalGraphics(mContext)) {
            forecastAdapterViewHolder.mIconView.setImageResource(defaultImage);
        } else {
            Glide.with(mContext)
                    .load(StringUtils.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(forecastAdapterViewHolder.mIconView);
        }

        // this enables better animations. even if we lose state due to a device rotation,
        // the animator can use this to re-find the original view
        ViewCompat.setTransitionName(forecastAdapterViewHolder.mIconView, "iconView" + position);

        // Read date from cursor
        long dateInMillis = mCursor.getLong(AppConstants.COL_WEATHER_DATE);

        // Find TextView and set formatted date on it
        forecastAdapterViewHolder.mDateView.setText(StringUtils.getFriendlyDayString(mContext, dateInMillis));

        // Read weather forecast from cursor
        String description = StringUtils.getStringForWeatherCondition(mContext, weatherId);

        // Find TextView and set weather forecast on it
        forecastAdapterViewHolder.mDescriptionView.setText(description);
        forecastAdapterViewHolder.mDescriptionView.setContentDescription(mContext.getString(R.string.a11y_forecast, description));

        // For accessibility, we don't want a content description for the icon field
        // because the information is repeated in the description view and the icon
        // is not individually selectable

        // Read high temperature from cursor
        double high = mCursor.getDouble(AppConstants.COL_WEATHER_MAX_TEMP);
        String highString = StringUtils.formatTemperature(mContext, high);
        forecastAdapterViewHolder.mHighTempView.setText(highString);
        forecastAdapterViewHolder.mHighTempView.setContentDescription(mContext.getString(R.string.a11y_high_temp, highString));

        // Read low temperature from cursor
        double low = mCursor.getDouble(AppConstants.COL_WEATHER_MIN_TEMP);
        String lowString = StringUtils.formatTemperature(mContext, low);
        forecastAdapterViewHolder.mLowTempView.setText(lowString);
        forecastAdapterViewHolder.mLowTempView.setContentDescription(mContext.getString(R.string.a11y_low_temp, lowString));

        mRecyclerItemChoiceManager.onBindViewHolder(forecastAdapterViewHolder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Method to swap cursor with a new one
     *
     * @param cursor Cursor
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Method to get the instance of the cursor.
     *
     * @return Cursor
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * @param viewHolder
     */
    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ForecastAdapterViewHolder) {
            ForecastAdapterViewHolder forecastAdapterViewHolder = (ForecastAdapterViewHolder) viewHolder;
            forecastAdapterViewHolder.onClick(forecastAdapterViewHolder.itemView);
        }
    }

    /**
     * Method to restore saved data.
     *
     * @param savedInstanceState {@link Bundle}
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecyclerItemChoiceManager.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Method to save data in a bundle. This is called is specific cases. When the screen orientation
     * changes. and other cases
     *
     * @param outState {@link Bundle}
     */
    public void onSaveInstanceState(Bundle outState) {
        mRecyclerItemChoiceManager.onSaveInstanceState(outState);
    }

    /**
     * @param useTodayLayout
     */
    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    /**
     * Method to get the selected item position in the recyclerView
     *
     * @return Position
     */
    public int getSelectedItemPosition() {
        return mRecyclerItemChoiceManager.getSelectedItemPosition();
    }
}
