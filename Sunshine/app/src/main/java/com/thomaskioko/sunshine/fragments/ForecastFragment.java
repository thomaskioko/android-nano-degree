package com.thomaskioko.sunshine.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.thomaskioko.sunshine.DetailActivity;
import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.SettingsActivity;
import com.thomaskioko.sunshine.data.WeatherContract;
import com.thomaskioko.sunshine.data.tasks.FetchWeatherTask;
import com.thomaskioko.sunshine.data.tasks.ForecastAdapter;

/**
 * @author Thomas Kioko
 */
public class ForecastFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ForecastAdapter mForecastAdapter;
    private SharedPreferences mSharedPreferences;
    private String mLocation;
    private static final int LOADER_ID = 100;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;


    public ForecastFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Enable the fragment to handle menu events
        setHasOptionsMenu(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLocation = mSharedPreferences.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_value_location));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, System.currentTimeMillis());

        Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        mForecastAdapter = new ForecastAdapter(getActivity(), cursor, 0);

        //Initialise the listView
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        //Bind the adapter to the listView
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(this);

        if (cursor != null)
            cursor.close();


        return rootView;
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater Menu inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecast, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchWeatherData();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.action_location:
                Uri geoLocation = Uri.parse("geo:0,0?q=" + mLocation);
                Intent intent = new Intent(Intent.ACTION_VIEW, geoLocation);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Could not get location" + mLocation, Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void fetchWeatherData() {
        String metricUnit = mSharedPreferences.getString(getString(R.string.pref_key_units), getString(R.string.pref_default_value_metric));
        if (!mLocation.equals("")) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mLocation);
            fetchWeatherTask.execute(mLocation, metricUnit);
        } else {
            Toast.makeText(getActivity(), "Could not get location" + mLocation, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchWeatherData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            Intent intent = new Intent(getActivity(), DetailActivity.class)
                    .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                            mLocation, cursor.getLong(COL_WEATHER_DATE)
                    ));
            startActivity(intent);
        }

    }

    // since we read the location when we create the loader, all we need to do is restart things
    public void onLocationChanged() {
        fetchWeatherData();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}