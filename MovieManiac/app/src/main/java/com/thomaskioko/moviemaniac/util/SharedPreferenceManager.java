package com.thomaskioko.moviemaniac.util;

import android.content.SharedPreferences;

import com.thomaskioko.moviemaniac.MovieManiacApplication;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public final class SharedPreferenceManager {

    private SharedPreferenceManager() {
    }

    /**
     * @param key    Shared preference key
     * @param sValue Value
     */
    public static void saveToSharedPreferences(String key, String sValue) {
        SharedPreferences.Editor mEditor = MovieManiacApplication.mSharedPreferences.edit();
        mEditor.putString(key, sValue);
        mEditor.apply();

        //Update local reference
        MovieManiacApplication.savedMovieListType = sValue;
    }

    /**
     * @param key          Shared preference key
     * @param valueDefault Value
     * @return String
     */
    public static String readSharedPreferences(String key, String valueDefault) {
        return MovieManiacApplication.mSharedPreferences.getString(key, valueDefault);
    }

}
