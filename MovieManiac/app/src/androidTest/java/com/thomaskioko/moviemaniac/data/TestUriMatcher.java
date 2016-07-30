package com.thomaskioko.moviemaniac.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.thomaskioko.moviemaniac.data.provider.FavoriteMovieProvider;

/**
 * @author Thomas Kioko
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String TEST_MOVIE_ID = "4";
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_LOCATION = FavoritesContract.FavoriteMovieEntry.CONTENT_URI;
    private static final Uri TEST_LOCATION_DIR = FavoritesContract.FavoriteMovieEntry.buildFavoritesUri();
    private static final Uri TEST_FAVORITE_WITH_ID = FavoritesContract.FavoriteMovieEntry.buildFavoriteMovie(TEST_MOVIE_ID);

    /**
     * This function tests that your UriMatcher returns the correct integer value
     * for each of the Uri types that our ContentProvider can handle.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = FavoriteMovieProvider.buildUriMatcher();

        assertEquals("Error: The FAVORITE URI was matched incorrectly.", testMatcher.match(TEST_LOCATION), FavoriteMovieProvider.FAVORITE);
        assertEquals("Error: The FAVORITE URI DIR was matched incorrectly.", testMatcher.match(TEST_LOCATION_DIR), FavoriteMovieProvider.FAVORITE);
        assertEquals("Error: The FAVORITE WITH ID AND DATE URI was matched incorrectly.",
                testMatcher.match(TEST_FAVORITE_WITH_ID), FavoriteMovieProvider.FAVORITE_WITH_ID);
    }
}