package com.thomaskioko.moviemaniac.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thomaskioko.moviemaniac.R;

/**
 * Helper class that uses {@link SharedPreferences} to store data locally
 *
 * @author Thomas Kioko
 */
public final class SharedPreferenceManager {

    private Context mContext;
    public static SharedPreferences mSharedPreferences;
    private static final String LOG_TAG = SharedPreferenceManager.class.getSimpleName();

    public SharedPreferenceManager(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(context.getString(R.string.prefs_name),
                Context.MODE_PRIVATE);
    }

    /**
     * @param key    Shared preference key
     * @param sValue Value
     */
    public void saveToSharedPreferences(String key, String sValue) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(key, sValue);
        mEditor.apply();

        Log.i(LOG_TAG, "@saveToSharedPreferences:: Movie Type - " + sValue);
    }

    /**
     * @param key          Shared preference key
     * @param valueDefault Value
     * @return String
     */
    public static String readSharedPreferences(String key, String valueDefault) {
        return mSharedPreferences.getString(key, valueDefault);
    }

    /**
     * Method to get the saved movie type
     *
     * @return {@link String}
     */
    public String getMovieType() {

        return SharedPreferenceManager.readSharedPreferences(
                mContext.getString(R.string.prefs_key_type),
                ApplicationConstants.PREF_MOVIE_LIST_POPULAR);
    }

}
