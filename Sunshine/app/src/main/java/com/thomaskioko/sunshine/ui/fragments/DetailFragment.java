package com.thomaskioko.sunshine.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.sunshine.DetailActivity;
import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.SettingsActivity;
import com.thomaskioko.sunshine.data.WeatherContract;
import com.thomaskioko.sunshine.data.WeatherContract.WeatherEntry;
import com.thomaskioko.sunshine.util.AppConstants;
import com.thomaskioko.sunshine.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mForecast;
    private Uri mUri;
    public static final String DETAIL_URI = "URI";
    private static final int LOADER_ID = 100;
    public static final String DETAIL_TRANSITION_ANIMATION = "DTA";
    private boolean mTransitionAnimation;
    @Bind(R.id.detail_icon)
    ImageView mIconView;
    @Bind(R.id.detail_date_textview)
    TextView mDateView;
    @Bind(R.id.detail_forecast_textview)
    TextView mDescriptionView;
    @Bind(R.id.detail_high_textview)
    TextView mHighTempView;
    @Bind(R.id.detail_low_textview)
    TextView mLowTempView;
    @Bind(R.id.detail_humidity_textview)
    TextView mHumidityView;
    @Bind(R.id.detail_humidity_label_textview)
    TextView mHumidityLabelView;
    @Bind(R.id.detail_wind_textview)
    TextView mWindView;
    @Bind(R.id.detail_wind_label_textview)
    TextView mWindLabelView;
    @Bind(R.id.detail_pressure_textview)
    TextView mPressureView;
    @Bind(R.id.detail_pressure_label_textview)
    TextView mPressureLabelView;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mTransitionAnimation = arguments.getBoolean(DetailFragment.DETAIL_TRANSITION_ANIMATION, false);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() instanceof DetailActivity) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_detail_settings, menu);
            finishCreatingMenu(menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    AppConstants.DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        ViewParent vp = getView().getParent();
        if (vp instanceof CardView) {
            ((View) vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            ViewParent vp = getView().getParent();
            if (vp instanceof CardView) {
                ((View) vp).setVisibility(View.VISIBLE);
            }

            // Read weather condition ID from cursor
            int weatherId = data.getInt(AppConstants.COL_DETAIL_WEATHER_CONDITION_ID);

            if (StringUtils.usingLocalGraphics(getActivity())) {
                mIconView.setImageResource(StringUtils.getArtResourceForWeatherCondition(weatherId));
            } else {
                // Use weather art image
                Glide.with(this)
                        .load(StringUtils.getArtUrlForWeatherCondition(getActivity(), weatherId))
                        .error(StringUtils.getArtResourceForWeatherCondition(weatherId))
                        .crossFade()
                        .into(mIconView);
            }

            // Read date from cursor and update views for day of week and date
            long date = data.getLong(AppConstants.COL_WEATHER_DATE);
            String dateText = StringUtils.formatDate(getActivity(), date);
            mDateView.setText(dateText);

            // Get description from weather condition ID
            String description = StringUtils.getStringForWeatherCondition(getActivity(), weatherId);
            mDescriptionView.setText(description);
            mDescriptionView.setContentDescription(getString(R.string.a11y_forecast, description));

            // For accessibility, add a content description to the icon field. Because the ImageView
            // is independently focusable, it's better to have a description of the image. Using
            // null is appropriate when the image is purely decorative or when the image already
            // has text describing it in the same UI component.
            mIconView.setContentDescription(getString(R.string.a11y_forecast_icon, description));

            // Read high temperature from cursor and update view
            boolean isMetric = StringUtils.isMetric(getActivity());

            double high = data.getDouble(AppConstants.COL_WEATHER_MAX_TEMP);
            String highString = StringUtils.formatTemperature(getActivity(), high);
            mHighTempView.setText(highString);
            mHighTempView.setContentDescription(getString(R.string.a11y_high_temp, highString));

            // Read low temperature from cursor and update view
            double low = data.getDouble(AppConstants.COL_WEATHER_MIN_TEMP);
            String lowString = StringUtils.formatTemperature(getActivity(), low);
            mLowTempView.setText(lowString);
            mLowTempView.setContentDescription(getString(R.string.a11y_low_temp, lowString));

            // Read humidity from cursor and update view
            float humidity = data.getFloat(AppConstants.COL_WEATHER_HUMIDITY);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
            mHumidityView.setContentDescription(getString(R.string.a11y_humidity, mHumidityView.getText()));
            mHumidityLabelView.setContentDescription(mHumidityView.getContentDescription());

            // Read wind speed and direction from cursor and update view
            float windSpeedStr = data.getFloat(AppConstants.COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(AppConstants.COL_WEATHER_DEGREES);
            mWindView.setText(StringUtils.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
            mWindView.setContentDescription(getString(R.string.a11y_wind, mWindView.getText()));
            mWindLabelView.setContentDescription(mWindView.getContentDescription());

            // Read pressure from cursor and update view
            float pressure = data.getFloat(AppConstants.COL_WEATHER_PRESSURE);
            mPressureView.setText(getString(R.string.format_pressure, pressure));
            mPressureView.setContentDescription(getString(R.string.a11y_pressure, mPressureView.getText()));
            mPressureLabelView.setContentDescription(mPressureView.getContentDescription());

            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

        // We need to start the enter transition after the data has loaded
        if (mTransitionAnimation) {
            activity.supportStartPostponedEnterTransition();

            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView);

                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if (null != toolbarView) {
                Menu menu = toolbarView.getMenu();
                if (null != menu) menu.clear();
                toolbarView.inflateMenu(R.menu.menu_detail_settings);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // since we read the location when we create the loader, all we need to do is restart things
    public void onLocationChanged(String location) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherEntry.buildWeatherLocationWithDate(location, date);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + "#SunshineApp");
        return shareIntent;
    }

}
