package com.thomaskioko.moviemaniac.data.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.thomaskioko.moviemaniac.interfaces.MovieCallback;
import com.thomaskioko.moviemaniac.data.DbUtils;
import com.thomaskioko.moviemaniac.data.FavoritesContract;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.ArrayList;

/**
 * This class extends {@link AsyncTask} to fetch and save favorite movies in SQLite
 *
 * @author Thomas Kioko
 */
public class DatabaseAsyncTask extends AsyncTask<String, Void, String[]> {

    private final String TAG = DatabaseAsyncTask.class.getSimpleName();

    MovieCallback mCallback;
    Context mContext;
    Result mResult;
    ArrayList<Result> mResultArrayList = null;

    /**
     * Constructor
     *
     * @param callback {@link MovieCallback} interface
     * @param context  Application context
     * @param result   Movie object
     */
    public DatabaseAsyncTask(MovieCallback callback, Context context, Result result) {
        mCallback = callback;
        mContext = context;
        mResult = result;
    }

    @Override
    protected String[] doInBackground(String... params) {

        //Get the params
        String taskType = params[0];
        String movieId = params[1];
        String[] strResult;

        switch (taskType) {
            case ApplicationConstants.TASK_IS_FAVORITE: {
                Uri uri = FavoritesContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    strResult = new String[]{ApplicationConstants.CALLBACK_IS_FAVORITE, "true"};
                } else {
                    strResult = new String[]{ApplicationConstants.CALLBACK_IS_FAVORITE, "false"};
                }
                if (cursor != null) cursor.close();
                return strResult;
            }
            case ApplicationConstants.TASK_ADD_FAVORITE: {
                Uri success = mContext.getContentResolver().insert(FavoritesContract.FavoriteMovieEntry.CONTENT_URI, DbUtils.getContentValues(mResult));
                if (success != null)
                    return new String[]{ApplicationConstants.CALLBACK_ADD_FAVORITE, "true"};
                else
                    return new String[]{ApplicationConstants.CALLBACK_ADD_FAVORITE, "false"};
            }
            case ApplicationConstants.TASK_QUERY_FAVORITE_LIST: {
                Uri uri = FavoritesContract.FavoriteMovieEntry.CONTENT_URI;
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

                if (cursor != null) {
                    mResultArrayList = new ArrayList<>();

                    while (cursor.moveToNext()) {
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

                        mResultArrayList.add(movieResult);
                    }
                    cursor.close();
                    return new String[]{ApplicationConstants.CALLBACK_QUERY_FAVORITE_LIST};
                } else {
                    Log.e(TAG, "@doInBackground Query Failed");
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        String callBackType = strings[0];

        if (!callBackType.equals(ApplicationConstants.CALLBACK_QUERY_FAVORITE_LIST)) {
            mCallback.CallbackRequest(callBackType, strings[1]);
        } else mCallback.CallbackRequest(callBackType, mResultArrayList);
    }
}
