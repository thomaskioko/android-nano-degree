package com.thomaskioko.moviemaniac.data.provider;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.thomaskioko.moviemaniac.data.FavoritesContract;
import com.thomaskioko.moviemaniac.data.FavoritesContract.FavoriteMovieEntry;

/**
 * Class to test wherether {@link FavoriteMovieProvider} has been configured correctly.
 *
 * @author Thomas Kioko
 */
public class TestFavoriteProvider extends AndroidTestCase {


    /**
     * Since we want each test to start with a clean slate, run deleteAllRecords
     * in setUp (called by the test runner before each test).
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    /**
     * Method to test if provider has correctly been set up
     */
    public void testProviderRegistry() {
        PackageManager packageManager = mContext.getPackageManager();

        /**
         * We define the component name based on the package name from the context and
         * {@link FavoriteMovieProvider}
         */
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                FavoriteMovieProvider.class.getName());
        try {
            /**
             *   Fetch the provider info using the component name from the PackageManager.
             *   This throws an exception if the provider isn't registered.
             */
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: FavoriteMovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + FavoritesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FavoritesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: FavoriteMovieProvider not registered at " + mContext.getPackageName(), false);
        }
    }


    /**
     * This test doesn't touch the database.  It verifies that the ContentProvider returns
     * the correct type for each type of URI that it can handle.
     */
    public void testGetType() {
        // content://com.thomaskioko.moviemaniac/favorite/
        String type = mContext.getContentResolver().getType(FavoriteMovieEntry.CONTENT_URI);
        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
                FavoriteMovieEntry.CONTENT_TYPE, type);

        String movieId = "4";
        // content://com.thomaskioko.moviemaniac/favorite/94074
        type = mContext.getContentResolver().getType(FavoriteMovieEntry.buildFavoriteMovie(movieId));
        assertEquals("Error: the FavoriteMovieEntry CONTENT_URI with id should return FavoriteMovieEntry.CONTENT_ITEM_TYPE",
                FavoriteMovieEntry.CONTENT_ITEM_TYPE, type);

    }


    /**
     * This helper function deletes all records from both database tables using the ContentProvider.
     * It also queries the ContentProvider to make sure that the database has been successfully
     * deleted, so it cannot be used until the Query and Delete functions have been written
     * in the ContentProvider.
     * <p/>
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                FavoriteMovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                FavoriteMovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            assertEquals("Error: Records not deleted from Favorite table during delete", 0, cursor.getCount());

            cursor = mContext.getContentResolver().query(
                    FavoriteMovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                assertEquals("Error: Records not deleted from Favorite table during delete", 0, cursor.getCount());
            }
            //Close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }

    }
}
