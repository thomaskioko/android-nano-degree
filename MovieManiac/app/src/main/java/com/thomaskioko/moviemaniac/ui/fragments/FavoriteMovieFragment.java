package com.thomaskioko.moviemaniac.ui.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.data.FavoriteMovieDbHelper;
import com.thomaskioko.moviemaniac.data.FavoritesContract;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.MovieDetailActivity;
import com.thomaskioko.moviemaniac.ui.adapters.FavoritesAdapter;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass that implements {@link LoaderManager} to fetch data from
 * {@link FavoriteMovieDbHelper}
 *
 * @author Thomas Kioko
 */
public class FavoriteMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @Bind(R.id.tvFavorites)
    TextView mTextView;
    @Bind(R.id.ivFavorite)
    ImageView mImageView;
    @Bind(R.id.grid_view)
    GridView mGridView;
    private FavoritesAdapter mForecastAdapter;
    public static final int LOADER_ID = 100;
    private static final String LOG_TAG = FavoriteMovieFragment.class.getSimpleName();

    /**
     * Constructor required empty public constructor
     */
    public FavoriteMovieFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_movies, container, false);
        ButterKnife.bind(this, rootView);

        Uri weatherForLocationUri = FavoritesContract.FavoriteMovieEntry.CONTENT_URI;

        Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, null);

        assert cursor != null;
        if (cursor.moveToFirst()) {
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);

            mForecastAdapter = new FavoritesAdapter(getActivity(), cursor, 0, getFragmentManager());
            mGridView.setAdapter(mForecastAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    fetchMovieDetails(position);
                }
            });
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri favoritesUri = FavoritesContract.FavoriteMovieEntry.buildFavoritesUri();

        return new CursorLoader(getActivity(),
                favoritesUri,
                ApplicationConstants.FAVORITE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    /**
     * This method queries the DB and fetches a movie based on the selected item.
     * //TODO:: Optimize this method
     *
     * @param position Position on the gridView
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fetchMovieDetails(int position) {
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(getActivity());

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            Cursor cursor = db.query(
                    FavoritesContract.FavoriteMovieEntry.TABLE_NAME,  // Table to Query
                    ApplicationConstants.FAVORITE_COLUMNS, // all columns
                    FavoritesContract.FavoriteMovieEntry._ID + " = ? ",
                    new String[]{String.valueOf(position + 1)}, //Row ID
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );

            if (cursor != null) {
                Log.i(LOG_TAG, "@onCreateView:: Cursor has data");
                if (cursor.moveToFirst()) {
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

                        MovieManiacApplication.result = movieResult;

                        if (MovieManiacApplication.isTwoPane) {
                            MovieDetailFragment fragment = new MovieDetailFragment();
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.movie_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                            startActivity(intent);
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception exception) {
            Log.e(LOG_TAG, "@fetchMovieDetails:: Error message: " + exception.getMessage());
        }

    }

}
