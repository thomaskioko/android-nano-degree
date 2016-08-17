package com.thomaskioko.sunshine.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.thomaskioko.sunshine.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * {@link SharedPreferences} helper class. Manages all shared preference functions.
 *
 * @author Thomas Kioko
 */
public class SharedPrefsManager {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCATION_STATUS_OK, LOCATION_STATUS_SERVER_DOWN, LOCATION_STATUS_SERVER_INVALID,
            LOCATION_STATUS_UNKNOWN, LOCATION_STATUS_INVALID})
    public @interface LocationStatus {
    }

    public static final int LOCATION_STATUS_OK = 0;
    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
    public static final int LOCATION_STATUS_UNKNOWN = 3;
    public static final int LOCATION_STATUS_INVALID = 4;

    /**
     * Sets the location status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     *
     * @param context        Context to get the PreferenceManager from.
     * @param locationStatus The IntDef value to set
     */
    static public void setLocationStatus(Context context, @LocationStatus int locationStatus) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(context.getString(R.string.pref_location_status_key), locationStatus);
        edit.apply(); //We use apply because it's on a background thread
    }

    /**
     * This function gets the saved location in SharedPreference.
     *
     * @param context Context used to get the SharedPreferences
     * @return the location status integer type
     */
    @SuppressWarnings("ResourceType")
    static public
    @LocationStatus
    int getLocationStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getString(R.string.pref_location_status_key), LOCATION_STATUS_UNKNOWN);
    }

    /**
     * Resets the location status.  (Sets it to SharedPrefsManager.LOCATION_STATUS_UNKNOWN)
     *
     * @param cursor Context used to get the SharedPreferences
     */
    static public void resetLocationStatus(Context cursor) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cursor);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(cursor.getString(R.string.pref_location_status_key), SharedPrefsManager.LOCATION_STATUS_UNKNOWN);
        spe.apply();
    }

    /**
     * Get the preferred/ set location
     *
     * @param context Context in which the method is called
     * @return User location
     */
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_location),
                context.getString(R.string.pref_default_value_location));
    }
}
