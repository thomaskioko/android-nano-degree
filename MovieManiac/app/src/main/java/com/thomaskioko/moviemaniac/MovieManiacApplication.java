package com.thomaskioko.moviemaniac;

import android.app.Application;

import com.thomaskioko.moviemaniac.api.TmdbApiClient;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

/**
 * Application class.
 *
 * @author Thomas Kioko
 */
public class MovieManiacApplication extends Application {

    private static TmdbApiClient tmdbApiClient = new TmdbApiClient();
    public static Result result;
    public static boolean isTwoPane;

    @Override
    public void onCreate() {
        super.onCreate();
        tmdbApiClient.setIsDebug(ApplicationConstants.DEBUG);
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
