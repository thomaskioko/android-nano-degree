package com.thomaskioko.moviemaniac.data;

import android.content.ContentValues;

import com.thomaskioko.moviemaniac.model.Result;

/**
 * Helper class to handle CRUD DB functions
 *
 * @author Thomas Kioko
 */
public class DbUtils {

    private static final String LOG_TAG = DbUtils.class.getSimpleName();

    /**
     * Default Constructor
     */
    public DbUtils() {
    }

    /**
     * This method saves {@link Result} into {@link ContentValues}
     *
     * @param result Movie Object
     * @return {@link ContentValues}
     */
    public static ContentValues getContentValues(Result result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, result.getPosterPath());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW, result.getOverview());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, result.getReleaseDate());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, result.getId());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE, result.getTitle());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH, result.getBackdropPath());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY, result.getPopularity());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT, result.getVoteCount());
        contentValues.put(FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, result.getVoteAverage());

        return contentValues;
    }
}
