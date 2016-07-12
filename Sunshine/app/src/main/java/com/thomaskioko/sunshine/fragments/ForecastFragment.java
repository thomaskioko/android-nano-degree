package com.thomaskioko.sunshine.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.thomaskioko.sunshine.DetailActivity;
import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.SettingsActivity;
import com.thomaskioko.sunshine.net.HttpHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Thomas Kioko
 */
public class ForecastFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> arrayAdapter;
    private SharedPreferences mSharedPreferences;
    private String mLocation;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create an array of to hold fake data.
        String[] arrayItems = {};

        List<String> forecastData = new ArrayList<>(Arrays.asList(arrayItems));

        arrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                forecastData);

        //Initialise the listView
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        //Bind the adapter to the listView
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);


        return rootView;
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
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
        String metricUnit = mSharedPreferences.getString(getString(R.string.pref_key_temp), getString(R.string.pref_default_value_metric));
        if (!mLocation.equals("")) {
            new FetchWeatherTask().execute(mLocation, metricUnit);
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

        String data = adapterView.getItemAtPosition(position).toString();

        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(detailIntent);

    }

    /**
     *
     */
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            HttpHelper httpHelper = new HttpHelper();
            return httpHelper.getWeatherForecast(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            arrayAdapter.clear();
            for (String forecastString : strings) {
                arrayAdapter.add(forecastString);
            }
        }


    }
}