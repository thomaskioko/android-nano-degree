package com.thomaskioko.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.sunshine.data.sync.SunshineSyncAdapter;
import com.thomaskioko.sunshine.fragments.DetailFragment;
import com.thomaskioko.sunshine.fragments.ForecastFragment;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    private String mLocation;
    private boolean mTwoPane = false;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        //Initialize the sync adapter
        SunshineSyncAdapter.initializeSyncAdapter(this);

        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLocation = mSharedPreferences.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_value_location));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the location in our second pane using the fragment manager
        if (mLocation != null && !mLocation.equals(mLocation)) {
            String FORECASTFRAGMENT_TAG = "FFTAG";
            String DETAILFRAGMENT_TAG = "detailFragmentTag";
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (null != forecastFragment) {
                forecastFragment.onLocationChanged();
            }

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != detailFragment) {
                detailFragment.onLocationChanged(mLocation);
            }
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
