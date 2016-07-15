package com.thomaskioko.moviemaniac;

import android.app.Application;

import com.thomaskioko.moviemaniac.api.TmdbApiClient;

/**
 * Application class.
 *
 * @author Thomas Kioko
 */
public class MovieManiacApplication extends Application {

    private static TmdbApiClient tmdbApiClient = new TmdbApiClient();

    @Override
    public void onCreate() {
        super.onCreate();
        tmdbApiClient.setIsDebug(true);
    }

    /**
     * @return {@link TmdbApiClient} instance
     */
    public static TmdbApiClient getTmdbApiClient() {
        return tmdbApiClient;
    }
}
