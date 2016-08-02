package com.thomaskioko.moviemaniac.util;

import com.thomaskioko.moviemaniac.data.FavoritesContract;

/**
 * @author Thomas Kioko
 */


public class ApplicationConstants {

    /**
     * Set to true to Enable Debugging in the API false to disable. This should be false when
     * releasing the app.
     */
    public static final boolean DEBUG = false;
    /**
     * API Endpoint
     */
    public static final String END_POINT = "http://api.themoviedb.org/3/movie/";
    /**
     * Image base Url
     */
    public static final String TMDB_IMAGE_URL = "http://image.tmdb.org/t/p/";
    /**
     * 185 Image size
     */
    public static final String IMAGE_SIZE_185 = "w185";
    /**
     * 500 Image size
     */
    public static final String IMAGE_SIZE_500 = "w500";
    /**
     * 780 Image size
     */
    public static final String IMAGE_SIZE_780 = "w780";
    /**
     * Original Image size
     */
    public static final String IMAGE_SIZE_ORIGINAL = "original";
    /**
     * Connection timeout duration
     */
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**
     * Connection Read timeout duration
     */
    public static final int READ_TIMEOUT = 60 * 1000;
    /**
     * Connection write timeout duration
     */
    public static final int WRITE_TIMEOUT = 60 * 1000;
    /**
     * This array contains data we'll fetch from the DB.
     */
    public static final String[] FAVORITE_COLUMNS = {
            FavoritesContract.FavoriteMovieEntry.TABLE_NAME + "." + FavoritesContract.FavoriteMovieEntry._ID,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_POPULARITY,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT,
            FavoritesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };

    /**
     * These indices are tied to FAVORITE_COLUMNS. If FAVORITE_COLUMNS changes, these must change.
     */
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_OVERVIEW = 3;
    public static final int COL_MOVIE_POSTER_PATH = 4;
    public static final int COL_MOVIE_BACKDROP_PATH = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_VOTE_AVERAGE = 7;
    public static final int COL_MOVIE_VOTE_COUNT = 8;
    public static final int COL_MOVIE_RELEASE_DATE = 9;


    // Prefs
    public static final String prefName = "MOVIZ_PREF";

    public static final String PREF_MOVIE_LIST_TYPE = "PREF_MOVIE_LIST_TYPE";
    public static final String PREF_MOVIE_LIST_POPULAR = "popular";
    public static final String PREF_MOVIE_LIST_TOP_RATED = "top_rated";
    public static final String PREF_MOVIE_LIST_NOW_PLAYING = "now_playing";
    public static final String PREF_MOVIE_LIST_UPCOMING = "upcoming";
    public static final String PREF_MOVIE_LIST_FAVORITES = "favorites";
    public static final String PREF_MOVIE_LIST_SEARCH = "search";

    //Callbacks
    public static final String CALLBACK_FETCH_MOVIES_WITH_PAGE = "CALLBACK_FETCH_MOVIES_WITH_PAGE";

    public static final String CALLBACK_IS_FAVORITE = "CALLBACK_IS_FAVORITE";
    public static final String CALLBACK_DELETE_FAVORITE = "CALLBACK_DELETE_FAVORITE";
    public static final String CALLBACK_ADD_FAVORITE = "CALLBACK_ADD_FAVORITE";
    public static final String CALLBACK_QUERY_FAVORITE_LIST = "CALLBACK_QUERY_FAVORITE_LIST";

    public static final String CALLBACK_MOVIE_BUNDLE = "CALLBACK_MOVIE_BUNDLE";
    public static final String CALLBACK_REFRESH_FAVORITES = "CALLBACK_REFRESH_FAVORITES";
    public static final String CALLBACK_VIEW_SEARCH_RESULTS = "CALLBACK_VIEW_SEARCH_RESULTS";

    public static final String CALLBACK_FETCH_POPULAR = "CALLBACK_FETCH_POPULAR";

    //MoviesListFragment
    public static final String KEY_MOVIE_OBJECTS = "KEY_MOVIE_OBJECTS";
    public static final String KEY_MOVIE_LIST_TYPE = "KEY_MOVIE_LIST_TYPE";
    public static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    public static final String KEY_CURRENT_QUERY_STRING = "KEY_CURRENT_QUERY_STRING";
    public static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
    public static final String KEY_FIRST_FETCH = "KEY_FIRST_FETCH";

    //DetailsFragment
    public static final String KEY_MOVIE_OBJECT = "KEY_MOVIE_OBJECT";
    public static final String KEY_VIDEO_OBJECTS = "KEY_VIDEO_OBJECTS";
    public static final String KEY_MOVIE_TITLE = "KEY_MOVIE_TITLE";
    public static final String KEY_MOVIE_ID = "KEY_MOVIE_ID";
    public static final String KEY_TMDB_RAW_REVIEW_OBJECT = "KEY_TMDB_RAW_REVIEW_OBJECT";
    public static final String KEY_REVIEW_OBJECTS = "KEY_REVIEW_OBJECTS";
    public static final String KEY_REVIEW_PAGING = "KEY_REVIEW_PAGING";

    //Database Related
    public static final String TASK_IS_FAVORITE = "TASK_IS_FAVORITE";
    public static final String TASK_DELETE_FAVORITE = "TASK_DELETE_FAVORITE";
    public static final String TASK_ADD_FAVORITE = "TASK_ADD_FAVORITE";
    public static final String TASK_QUERY_FAVORITE_LIST = "TASK_QUERY_FAVORITE_LIST";

    //RecyclerViewWithEmptyView States
    public static final String STATE_NO_NETWORK = "STATE_NO_NETWORK";
    public static final String STATE_NO_FAV = "STATE_NO_FAV";
}
