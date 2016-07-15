package com.thomaskioko.moviemaniac.api;

import org.junit.BeforeClass;
import org.mockito.Mock;

/**
 * @author Thomas Kioko
 */
public abstract class BaseTestCase {

    private static final boolean DEBUG = true;
    @Mock
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
