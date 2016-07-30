package com.thomaskioko.moviemaniac.data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.thomaskioko.moviemaniac.utils.TestUtilities;

/**
 * @author Thomas Kioko
 */
public class TestFetchFavorites extends AndroidTestCase {
    static final String ADD_MOVIE_ID = "209112";
    static final String ADD_MOVIE_TITLE = "Batman v Superman: Dawn of Justice";
    static final String ADD_MOVIE_OVERVIEW = TestUtilities.MOVIE_OVERVIEW;
    static final String ADD_MOVIE_POSTER_PATH = "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg";
    static final String ADD_MOVIE_BACKDROP_PATH = "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg";
    static final String ADD_MOVIE_POPULARITY = "34.499162";
    static final String ADD_MOVIE_VOTE_AVERAGE = "5.55";
    static final String ADD_MOVIE_VOTE_COUNT = "2981";
    static final String ADD_MOVIE_RELEASE_DATE = "2016-06-29";

    /**
     * testAddFavoriteMovie after you have written the AddLocation function.
     * This test will only run on API level 11 and higher because of a requirement in the
     * content provider.
     */
    @TargetApi(11)
    public void testAddFavoriteMovie() {
        // start from a clean state
        getContext().getContentResolver().delete(FavoritesContract.FavoriteMovieEntry.CONTENT_URI,
                FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ADD_MOVIE_ID});

        long rowId = addFavoriteMovie(
                ADD_MOVIE_ID,
                ADD_MOVIE_TITLE,
                ADD_MOVIE_OVERVIEW,
                ADD_MOVIE_POSTER_PATH,
                ADD_MOVIE_BACKDROP_PATH,
                ADD_MOVIE_POPULARITY,
                ADD_MOVIE_VOTE_AVERAGE,
                ADD_MOVIE_VOTE_COUNT,
                ADD_MOVIE_RELEASE_DATE
        );

        // does addFavoriteMovie return a valid record ID?
        assertFalse("Error: addFavoriteMovie returned an invalid ID on insert", rowId == -1);

        // test all this twice
        for (int i = 0; i < 2; i++) {

            // does the ID point to our movie?
            Cursor locationCursor = getContext().getContentResolver().query(
                    FavoritesContract.FavoriteMovieEntry.CONTENT_URI,
                    new String[]{
                            FavoritesContract.FavoriteMovieEntry._ID,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT,
                            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE,

                    },
                    FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{ADD_MOVIE_ID},
                    null);

            assertNotNull(locationCursor);
            // these match the indices of the projection
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of Id does not match the returned value" +
                        "from addFavoriteMovie", locationCursor.getLong(0), rowId);
                assertEquals("Error: the queried value of movie id is incorrect",
                        locationCursor.getString(1), ADD_MOVIE_ID);
                assertEquals("Error: the queried value of movie title is incorrect",
                        locationCursor.getString(2), ADD_MOVIE_TITLE);
                assertEquals("Error: the queried value of movie overview is incorrect",
                        locationCursor.getString(3), ADD_MOVIE_OVERVIEW);
                assertEquals("Error: the queried value of poster path is incorrect",
                        locationCursor.getString(4), ADD_MOVIE_POSTER_PATH);
                assertEquals("Error: the queried value of backdrop path is incorrect",
                        locationCursor.getString(5), ADD_MOVIE_BACKDROP_PATH);
                assertEquals("Error: the queried value of popularity is incorrect",
                        locationCursor.getString(6), ADD_MOVIE_POPULARITY);
                assertEquals("Error: the queried value of average vote is incorrect",
                        locationCursor.getString(7), ADD_MOVIE_VOTE_AVERAGE);
                assertEquals("Error: the queried value of vote count is incorrect",
                        locationCursor.getString(8), ADD_MOVIE_VOTE_COUNT);
                assertEquals("Error: the queried value of release date is incorrect",
                        locationCursor.getString(9), ADD_MOVIE_RELEASE_DATE);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a favorite query",
                    locationCursor.moveToNext());

            // add the location again
            long newLocationId = addFavoriteMovie(
                    ADD_MOVIE_ID,
                    ADD_MOVIE_TITLE,
                    ADD_MOVIE_OVERVIEW,
                    ADD_MOVIE_POSTER_PATH,
                    ADD_MOVIE_BACKDROP_PATH,
                    ADD_MOVIE_POPULARITY,
                    ADD_MOVIE_VOTE_AVERAGE,
                    ADD_MOVIE_VOTE_COUNT,
                    ADD_MOVIE_RELEASE_DATE
            );


            assertEquals("Error: inserting a favorite again should return the same ID", rowId, newLocationId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(FavoritesContract.FavoriteMovieEntry.CONTENT_URI,
                FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ADD_MOVIE_ID});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(FavoritesContract.FavoriteMovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param movieId          Movie ID
     * @param movieTitle       Movie Title
     * @param movieOverView    Movie Overview
     * @param moviePosterPath  Movie Poster path
     * @param backdropPath     Movie backdrop path
     * @param moviePopularity  Popularity count
     * @param movieVoteAverage Average rating
     * @param movieVoteCount   Number of votes
     * @param movieReleaseDate Release date
     * @return Record Id
     */
    public long addFavoriteMovie(String movieId, String movieTitle, String movieOverView, String moviePosterPath,
                                 String backdropPath, String moviePopularity, String movieVoteAverage, String movieVoteCount,
                                 String movieReleaseDate) {
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long locationId;

        //Check if the movie with the city ID
        Cursor cursor = mContext.getContentResolver().query(
                FavoritesContract.FavoriteMovieEntry.CONTENT_URI,
                new String[]{FavoritesContract.FavoriteMovieEntry._ID},
                FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movieId},
                null
        );

        assert cursor != null;
        if (cursor.moveToFirst()) {

            int recordIndex = cursor.getColumnIndex(FavoritesContract.FavoriteMovieEntry._ID);
            locationId = cursor.getLong(recordIndex);
        } else {

            ContentValues contentValues = new ContentValues();

            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW, movieOverView);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, moviePosterPath);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY, moviePopularity);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieVoteAverage);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT, movieVoteCount);
            contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);


            locationId = db.insert(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);

        }
        cursor.close();
        return locationId;
    }
}
