package com.thomaskioko.moviemaniac.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.thomaskioko.moviemaniac.utils.TestUtilities;

import java.util.HashSet;

/**
 * Class to test DB function
 *
 * @author Thomas Kioko
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(FavoriteMovieDbHelper.DATABASE_NAME);
    }

    /**
     * This function gets called before each test is executed to delete the database.  This makes
     * sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * Function to test database creation.
     *
     * @throws Throwable
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(FavoritesContract.FavoriteMovieEntry.TABLE_NAME);

        mContext.deleteDatabase(FavoriteMovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoriteMovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //Check if we have created the tables.
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the favorites entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + FavoritesContract.FavoriteMovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry._ID);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT);
        locationColumnHashSet.add(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favorite entry columns",
                locationColumnHashSet.isEmpty());
        cursor.close();
        db.close();
    }

    /**
     * Method to test record insertion
     */
    public void testInsertRecord() {
        // Create the location
        long recordId = insertFavoriteData();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", recordId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues weatherValues = TestUtilities.createFavoriteMovieValues();

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                FavoritesContract.FavoriteMovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from location query", weatherCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate", weatherCursor, weatherValues);

        // Move the cursor to demonstrate that there is only one record in the database
//        assertFalse("Error: More than one record returned from weather query", weatherCursor.moveToNext());

        // Sixth Step: Close cursor and database
        weatherCursor.close();
        dbHelper.close();
    }

    /**
     * Helper method to insert record to the DB
     *
     * @return {@link long} DB row record Id
     */
    public long insertFavoriteData() {
        /**
         *  First step: Get reference to writable database. If there's an error in those massive
         *  SQL table creation Strings, errors will be thrown here when you try to get a writable database.
         */
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createFavoriteMovieValues();

        //Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(FavoritesContract.FavoriteMovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);


        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                FavoritesContract.FavoriteMovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from favorites query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Favorites Query Validation Failed",
                cursor, testValues);

        //Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from favorites query", cursor.moveToNext());

        //Close Cursor and Database
        cursor.close();
        db.close();
        return locationRowId;
    }

}
