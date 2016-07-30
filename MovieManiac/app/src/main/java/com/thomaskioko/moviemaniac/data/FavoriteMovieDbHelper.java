package com.thomaskioko.moviemaniac.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thomaskioko.moviemaniac.data.FavoritesContract.FavoriteMovieEntry;

/**
 * Database class.
 *
 * @author Thomas Kioko
 */
public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "moviemaniac.db";

    /**
     * Constructor
     *
     * @param context {@link Context}
     */
    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this favorites data
                FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT + " REAL NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " REAL NOT NULL, " +

                /**
                 * To assure the application has a unique favored movie,  a UNIQUE constraint with
                 * REPLACE strategy is created.
                 */
                " UNIQUE (" + FavoriteMovieEntry.COLUMN_MOVIE_ID
                + " ) ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
