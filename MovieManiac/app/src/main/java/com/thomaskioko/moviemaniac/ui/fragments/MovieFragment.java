package com.thomaskioko.moviemaniac.ui.fragments;


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
import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.adapters.MoviesRecyclerViewAdapter;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {


    //Views
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyView)
    TextView emptyView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressView;

    private boolean isFetchOngoing = false;
    private String movieListType;
    private TmdbApiClient mTmdbApiClient;
    private List<Result> mResultList = new ArrayList<>();
    private static final String TAG = MovieFragment.class.getSimpleName();

    /**
     *
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

        int NUMBER_OF_GRID_ITEMS = 4;
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

        switch (MovieManiacApplication.savedMovieListType) {
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

        if (!isFetchOngoing && !item.isChecked() && itemId != R.id.action_sort) {
            switch (itemId) {
                case R.id.action_popular:
                    Log.d(TAG, "Popular");
                    getActivity().setTitle("Popular");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_POPULAR;
                    break;
                case R.id.action_top_rated:
                    Log.d(TAG, "Top Rated");
                    getActivity().setTitle("Top Rated");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED;
                    break;
                case R.id.action_favorites:
                    Log.d(TAG, "Favorites");
                    getActivity().setTitle("Favorites");
                    movieListType = ApplicationConstants.PREF_MOVIE_LIST_FAVORITES;
                    break;
            }
            item.setChecked(true);
            fetchMovies(movieListType);
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies(final String mListType) {
        isFetchOngoing = true;
        toggleProgressBar(true);

        if (!mListType.equals(ApplicationConstants.PREF_MOVIE_LIST_FAVORITES)) {
            switch (mListType) {
                case ApplicationConstants.PREF_MOVIE_LIST_POPULAR:
                    getPopularMovies();
                    break;
                case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                    getTopRatedMovies();
                    break;
                case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                    break;
            }
        }
    }

    /**
     * @param visible True/False
     */
    private void toggleProgressBar(boolean visible) {
        if (visible) mProgressView.setVisibility(View.VISIBLE);
        else mProgressView.setVisibility(View.GONE);
    }

    private void getPopularMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getPopularMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                fetchEnded();
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
                fetchEnded();
                Log.e(TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }

    private void getTopRatedMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getTopRatedMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                fetchEnded();
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
                fetchEnded();
                Log.e(TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }

    private void fetchEnded() {
        isFetchOngoing = false;
        toggleProgressBar(false);
    }
}
