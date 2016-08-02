package com.thomaskioko.moviemaniac.ui.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.api.TmdbApiClient;
import com.thomaskioko.moviemaniac.data.DbUtils;
import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.adapters.MoviesRecyclerViewAdapter;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;
import com.thomaskioko.moviemaniac.util.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is a simple {@link Fragment} subclass that is used to fetch movies and display movies.
 *
 * @author Thomas Kioko
 */
public class MovieFragment extends Fragment {


    //Views
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyView)
    TextView mEmptyView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressView;

    private boolean mIsFetching = false;
    private String movieListType;
    private TmdbApiClient mTmdbApiClient;
    private List<Result> mResultList = new ArrayList<>();
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();

    /**
     * Default constructor
     */
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTmdbApiClient = MovieManiacApplication.getTmdbApiClient();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);

        int NUMBER_OF_GRID_ITEMS;
        if (MovieManiacApplication.isTwoPane) {
            NUMBER_OF_GRID_ITEMS = 4;
        } else {
            NUMBER_OF_GRID_ITEMS = 3;
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUMBER_OF_GRID_ITEMS);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Load popular movies as the default
        getPopularMovies();

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_movies, menu);
        SharedPreferenceManager sharedPreferences = new SharedPreferenceManager(getActivity());

        switch (sharedPreferences.getMovieType()) {
            case ApplicationConstants.PREF_MOVIE_LIST_POPULAR:
                menu.findItem(R.id.action_popular).setChecked(true);
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                menu.findItem(R.id.action_top_rated).setChecked(true);
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                menu.findItem(R.id.action_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (!mIsFetching && !item.isChecked() && itemId != R.id.action_sort) {
            switch (itemId) {
                case R.id.action_popular:
                    Log.d(LOG_TAG, "Popular");
                    getActivity().setTitle("Popular");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_POPULAR;
                    break;
                case R.id.action_top_rated:
                    Log.d(LOG_TAG, "Top Rated");
                    getActivity().setTitle("Top Rated");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED;
                    break;
                case R.id.action_favorites:
                    Log.d(LOG_TAG, "Favorites");
                    getActivity().setTitle("Favorites");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_FAVORITES;
                    break;
            }
            item.setChecked(true);
            fetchMovies(movieListType);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method that executes query based on the selected movie type.
     *
     * @param selectedMovieType Movie Type
     */
    private void fetchMovies(final String selectedMovieType) {
        mIsFetching = true;
        toggleProgressBar(true);

        switch (selectedMovieType) {
            case ApplicationConstants.PREF_MOVIE_LIST_POPULAR:
                getPopularMovies();
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                getTopRatedMovies();
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                getFavoriteMovies();
                break;
            default:
                break;
        }
    }

    /**
     * Helper method to hide and display the progressbar
     *
     * @param visible {@link boolean}True/False
     */
    private void toggleProgressBar(boolean visible) {
        if (visible) mProgressView.setVisibility(View.VISIBLE);
        else mProgressView.setVisibility(View.GONE);
    }

    /**
     * Method to get popular movies
     */
    private void getPopularMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getPopularMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.GONE);

                for (Result result : response.body().getResults()) {
                    mResultList.add(result);
                    mRecyclerView.setAdapter(new MoviesRecyclerViewAdapter(
                            getActivity(),
                            getFragmentManager(),
                            MovieManiacApplication.isTwoPane,
                            mResultList)
                    );
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.VISIBLE);
                Log.e(LOG_TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Method to get top rated movies
     */
    private void getTopRatedMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getTopRatedMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.GONE);
                for (Result result : response.body().getResults()) {
                    mResultList.add(result);
                    mRecyclerView.setAdapter(new MoviesRecyclerViewAdapter(
                            getActivity(),
                            getFragmentManager(),
                            MovieManiacApplication.isTwoPane,
                            mResultList)
                    );
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.VISIBLE);
                Log.e(LOG_TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }


    /**
     * Method to get favorite movies from {@link com.thomaskioko.moviemaniac.data.FavoriteMovieDbHelper}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getFavoriteMovies() {

        mResultList.clear();
        mRecyclerView.setAdapter(null);

        DbUtils dbUtils = new DbUtils(getActivity());
        mResultList = dbUtils.getFavoriteMovies();

        if (mResultList.size() > -1) {
            mRecyclerView.setAdapter(new MoviesRecyclerViewAdapter(
                    getActivity(),
                    getFragmentManager(),
                    MovieManiacApplication.isTwoPane,
                    mResultList)
            );

            toggleProgressBar(false);
            mIsFetching = false;
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

}
