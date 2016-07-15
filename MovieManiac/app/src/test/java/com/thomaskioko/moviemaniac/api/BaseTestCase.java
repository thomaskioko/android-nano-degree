package com.thomaskioko.moviemaniac.api;

import org.junit.BeforeClass;

/**
 * @author Thomas Kioko
 */
public abstract class BaseTestCase {

    private static final boolean DEBUG = true;
    private final static TmdbApiClient tmdbApiClient = new TmdbApiClient();

    @BeforeClass
    public static void setUpOnce() {
        tmdbApiClient.setIsDebug(DEBUG);
    }

    /**
     * @return {@link TmdbApiClient} instance.
     */
    protected final TmdbApiClient getTmdbApiClient() {

        return tmdbApiClient;
    }
}
