package com.thomaskioko.moviemaniac;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.thomaskioko.moviemaniac.api.TmdbApiClient;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;
import com.thomaskioko.moviemaniac.util.SharedPreferenceManager;

/**
 * Application class.
 *
 * @author Thomas Kioko
 */
public class MovieManiacApplication extends Application {

    private static TmdbApiClient tmdbApiClient = new TmdbApiClient();
    public static Result result;
    public static SharedPreferences mSharedPreferences;
    public static String savedMovieListType;
    public static boolean isTwoPane;

    @Override
    public void onCreate() {
        super.onCreate();
        tmdbApiClient.setIsDebug(ApplicationConstants.DEBUG);

        mSharedPreferences = getSharedPreferences(ApplicationConstants.prefName, Context.MODE_PRIVATE);
        savedMovieListType = SharedPreferenceManager.readSharedPreferences(ApplicationConstants.PREF_MOVIE_LIST_TYPE, ApplicationConstants.PREF_MOVIE_LIST_POPULAR);

        //Switch to popular if in search state
        if (ApplicationConstants.PREF_MOVIE_LIST_SEARCH.equals(savedMovieListType))
            savedMovieListType = ApplicationConstants.PREF_MOVIE_LIST_POPULAR;
    }

    /**
     * @return {@link TmdbApiClient} instance
     */
    public static TmdbApiClient getTmdbApiClient() {
        return tmdbApiClient;
    }

    /**
     * @return
     */
    public static Result getResult() {
        return result;
    }

}
