package com.thomaskioko.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.sunshine.fragments.ForecastFragment;

public class MainActivity extends AppCompatActivity {

    private String mLocation;
    private final String FORECASTFRAGMENT_TAG = "FFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLocation = mSharedPreferences.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_value_location));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the location in our second pane using the fragment manager
        if (mLocation != null && !mLocation.equals(mLocation)) {
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if ( null != forecastFragment ) {
                forecastFragment.onLocationChanged();
            }
        }
    }
}
