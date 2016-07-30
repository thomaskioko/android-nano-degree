package com.thomaskioko.moviemaniac.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.thomaskioko.moviemaniac.data.FavoritesContract;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Thomas Kioko
 */
public class TestUtilities {

    public static final String MOVIE_OVERVIEW = "Fearing the actions of a god-like Super Hero left unchecked, Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, modern-day savior, while the world wrestles with what sort of hero it really needs. And with Batman and Superman at war with one another, a new threat quickly arises, putting mankind in greater danger than it’s ever known before.";

    /**
     * @param error          Error Message
     * @param valueCursor    {@link Cursor}
     * @param expectedValues {@link ContentValues}
     */
    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * @param error          Error Message
     * @param valueCursor    {@link Cursor}
     * @param expectedValues {@link ContentValues}
     */
    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /**
     * @return {@link ContentValues}
     */
    public static ContentValues createFavoriteMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, "209112");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE, "Batman v Superman: Dawn of Justice");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW, MOVIE_OVERVIEW);
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, "/cGOPbv9wA5gEejkUN892JrveARt.jpg");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY, "34.499162");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, "5.55");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT, "2981");
        testValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2016-06-29");

        return testValues;
    }

}
