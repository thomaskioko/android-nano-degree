package com.thomaskioko.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.thomaskioko.sunshine.data.WeatherContract;
import com.thomaskioko.sunshine.data.sync.SunshineSyncAdapter;
import com.thomaskioko.sunshine.util.SharedPrefsManager;

import java.util.Locale;

/**
 * A {@link PreferenceActivity} that presents a set of application menu_detail_settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 * @author Thomas Kioko
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private ImageView mAttribution;
    public final static int PLACE_PICKER_REQUEST = 9090;
    public final static String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_location)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_units)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_art_pack_key)));

        // If we are using a PlacePicker location, we need to show attributions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAttribution = new ImageView(this);
            mAttribution.setImageResource(R.drawable.powered_by_google_light);

            if (!SharedPrefsManager.isLocationLatLonAvailable(this)) {
                mAttribution.setVisibility(View.GONE);
            }

            setListFooter(mAttribution);
        }
    }

    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Set the preference summaries
        setPreferenceSummary(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        setPreferenceSummary(preference, value);
        return true;
    }

    // This gets called after the preference is changed, which is important because we
    // start our synchronization here
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_location))) {
            // we've changed the location
            // first clear locationStatus
            SharedPrefsManager.resetLocationStatus(this);
            SunshineSyncAdapter.syncImmediately(this);
        } else if (key.equals(getString(R.string.pref_key_units))) {
            // units have changed. update lists of weather entries accordingly
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        } else if (key.equals(getString(R.string.pref_location_status_key))) {
            // our location status has changed.  Update the summary accordingly
            Preference locationPreference = findPreference(getString(R.string.pref_key_location));
            bindPreferenceSummaryToValue(locationPreference);
        } else if (key.equals(getString(R.string.pref_art_pack_key))) {
            // art pack have changed. update lists of weather entries accordingly
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * Method to set the summary of the shared preference
     *
     * @param preference Preference object
     * @param value      Value stored.
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (key.equals(getString(R.string.pref_key_location))) {
            @SharedPrefsManager.LocationStatus int status = SharedPrefsManager.getLocationStatus(this);
            switch (status) {
                case SharedPrefsManager.LOCATION_STATUS_OK:
                    preference.setSummary(stringValue);
                    break;
                case SharedPrefsManager.LOCATION_STATUS_UNKNOWN:
                    preference.setSummary(getString(R.string.pref_location_unknown_description, value.toString()));
                    break;
                case SharedPrefsManager.LOCATION_STATUS_INVALID:
                    preference.setSummary(getString(R.string.pref_location_error_description, value.toString()));
                    break;
                default:
                    // Note --- if the server is down we still assume the value
                    // is valid
                    preference.setSummary(stringValue);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check to see if the result is from our Place Picker intent
        if (requestCode == PLACE_PICKER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = place.getAddress().toString();
                LatLng latLong = place.getLatLng();

                Log.i(LOG_TAG, "@onActivityResult:: " + address);
                Log.i(LOG_TAG, "@onActivityResult:: " + latLong.latitude);
                Log.i(LOG_TAG, "@onActivityResult:: " + latLong.longitude);

                // If the provided place doesn't have an address, we'll form a display-friendly
                // string from the latlng values.
                if (TextUtils.isEmpty(address)) {
                    address = String.format(Locale.getDefault(), "(%.2f, %.2f)", latLong.latitude, latLong.longitude);
                }

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_location), address);

                // Also store the latitude and longitude so that we can use these to get a precise
                // result from our weather service. We cannot expect the weather service to
                // understand addresses that Google formats.
                editor.putFloat(getString(R.string.pref_location_latitude),  (float) latLong.latitude);
                editor.putFloat(getString(R.string.pref_location_longitude), (float) latLong.longitude);
                editor.commit();

                // Tell the SyncAdapter that we've changed the location, so that we can update
                // our UI with new values. We need to do this manually because we are responding
                // to the PlacePicker widget result here instead of allowing the
                // LocationEditTextPreference to handle these changes and invoke our callbacks.
                Preference locationPreference = findPreference(getString(R.string.pref_key_location));
                setPreferenceSummary(locationPreference, address);

                // Add attributions for our new PlacePicker location.
                if (mAttribution != null) {
                    mAttribution.setVisibility(View.VISIBLE);
                } else {
                    // For pre-Honeycomb devices, we cannot add a footer, so we will use a snackbar
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, getString(R.string.attribution_text),
                            Snackbar.LENGTH_LONG).show();
                }

                SharedPrefsManager.resetLocationStatus(this);
                SunshineSyncAdapter.syncImmediately(this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
