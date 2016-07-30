package com.thomaskioko.moviemaniac.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Class to test {@link FavoritesContract}
 *
 * @author Thomas Kioko
 */
public class TestFavoritesContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_FAVORITE_MOVIE_ID = "/4";

    /**
     * Test Favorite Contract Function
     */
    public void testBuildFavoriteMovie() {
        Uri locationUri = FavoritesContract.FavoriteMovieEntry.buildFavoriteMovie(TEST_FAVORITE_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in testBuildFavoriteMovie in " +
                "FavoritesContract.", locationUri);
        assertEquals("Error: Favorite Movie not properly appended to the end of the Uri",
                TEST_FAVORITE_MOVIE_ID, locationUri.getLastPathSegment());
        assertEquals("Error: Favorite Movie Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.thomaskioko.moviemaniac/favorite/%2F4");
    }
}
