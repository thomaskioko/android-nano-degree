package com.thomaskioko.moviemaniac.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.ArrayList;

/**
 * Helper class to handle CRUD DB functions
 *
 * @author Thomas Kioko
 */
public class DbUtils {

    private Context mContext;
    private static final String LOG_TAG = DbUtils.class.getSimpleName();

    /**
     * @param context Application Context
     */
    public DbUtils(Context context) {
        mContext = context;
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
    public long addFavoriteMovie(Integer movieId, String movieTitle, String movieOverView, String moviePosterPath,
                                 String backdropPath, Double moviePopularity, Double movieVoteAverage, Integer movieVoteCount,
                                 String movieReleaseDate) {
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long locationId;


        //Check if the movie with the movieID
        Cursor cursor = mContext.getContentResolver().query(
                FavoritesContract.FavoriteMovieEntry.CONTENT_URI,
                new String[]{FavoritesContract.FavoriteMovieEntry._ID},
                FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{String.valueOf(movieId)},
                null
        );

        assert cursor != null;
        if (cursor.moveToFirst()) {

            int locationIndex = cursor.getColumnIndex(FavoritesContract.FavoriteMovieEntry._ID);
            locationId = cursor.getLong(locationIndex);
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

            Log.i(LOG_TAG, "@addFavoriteMovie Inserted Record Id " + locationId);
        }
        cursor.close();
        return locationId;
    }

    /**
     * Method to fetch all favorite movies from DB
     *
     * @return {@link ArrayList} List of Movies
     */
    public ArrayList<Result> getFavoriteMovies() {

        ArrayList<Result> resultArrayList = new ArrayList<>();
        Uri favoriteMovies = FavoritesContract.FavoriteMovieEntry.CONTENT_URI;

        try {

            Cursor cursor = mContext.getContentResolver().query(favoriteMovies,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Log.i(LOG_TAG, "@onCreateView:: Cursor has data");
                do {
                    //Create an instance on of {@link Result} object
                    Result movieResult = new Result();
                    movieResult.setId(Integer.valueOf(cursor.getString(ApplicationConstants.COL_MOVIE_ID)));
                    movieResult.setTitle(cursor.getString(ApplicationConstants.COL_MOVIE_TITLE));
                    movieResult.setPosterPath(cursor.getString(ApplicationConstants.COL_MOVIE_POSTER_PATH));
                    movieResult.setBackdropPath(cursor.getString(ApplicationConstants.COL_MOVIE_BACKDROP_PATH));
                    movieResult.setOverview(cursor.getString(ApplicationConstants.COL_MOVIE_OVERVIEW));
                    movieResult.setPopularity(Double.valueOf(cursor.getString(ApplicationConstants.COL_MOVIE_POPULARITY)));
                    movieResult.setVoteAverage(Double.valueOf(cursor.getString(ApplicationConstants.COL_MOVIE_VOTE_AVERAGE)));
                    movieResult.setVoteCount(Integer.valueOf(cursor.getString(ApplicationConstants.COL_MOVIE_VOTE_COUNT)));
                    movieResult.setReleaseDate(cursor.getString(ApplicationConstants.COL_MOVIE_RELEASE_DATE));

                    resultArrayList.add(movieResult);

                } while (cursor.moveToNext());
            }
        } catch (Exception exception) {
            Log.e(LOG_TAG, "@getFavoriteMovies:: Error message: " + exception.getMessage());
        }

        return resultArrayList;
    }
}
