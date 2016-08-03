package com.thomaskioko.moviemaniac.ui.fragments;


import android.os.Bundle;
import android.os.Parcelable;
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
import com.thomaskioko.moviemaniac.interfaces.MovieCallback;
import com.thomaskioko.moviemaniac.interfaces.MovieDetailCallback;
import com.thomaskioko.moviemaniac.data.tasks.DatabaseAsyncTask;
import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.MovieListActivity;
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
public class MovieFragment extends Fragment implements MovieCallback, MovieDetailCallback {


    //Views
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyView)
    TextView mEmptyView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressView;

    private boolean mIsFetching = false;
    private String mMovieListType;
    private TmdbApiClient mTmdbApiClient;
    private MoviesRecyclerViewAdapter mRecyclerViewAdapter;
    private MovieDetailCallback mMovieDetailCallback;
    private SharedPreferenceManager sharedPreferenceManager;
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
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);

        int NUMBER_OF_GRID_ITEMS;
        if (MovieListActivity.isTwoPane) {
            NUMBER_OF_GRID_ITEMS = 4;
        } else {
            NUMBER_OF_GRID_ITEMS = 3;
        }

        mRecyclerViewAdapter = new MoviesRecyclerViewAdapter(getActivity(), mResultList);
        mRecyclerViewAdapter.setMovieDetailCallback(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUMBER_OF_GRID_ITEMS);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MovieListActivity.setCallback(this);
        mMovieDetailCallback = (MovieDetailCallback) getActivity();
        mMovieListType = sharedPreferenceManager.getMovieType();

        if (savedInstanceState == null) {
            fetchMovies(mMovieListType);
        } else {
            ArrayList<Result> resultArrayList = savedInstanceState.getParcelableArrayList(ApplicationConstants.KEY_MOVIE_OBJECTS);
            mMovieListType = savedInstanceState.getString(ApplicationConstants.KEY_MOVIE_LIST_TYPE);
            mRecyclerView.smoothScrollToPosition(savedInstanceState.getInt(ApplicationConstants.KEY_LIST_POSITION));
            populateRecyclerView(mMovieListType, resultArrayList);
        }
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
                    getActivity().setTitle(getString(R.string.menu_action_popular));
                    mMovieListType = ApplicationConstants.PREF_MOVIE_LIST_POPULAR;
                    break;
                case R.id.action_top_rated:
                    getActivity().setTitle(getString(R.string.menu_action_top_rated));
                    mMovieListType = ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED;
                    break;
                case R.id.action_favorites:
                    getActivity().setTitle(getString(R.string.menu_action_favorites));
                    mMovieListType = ApplicationConstants.PREF_MOVIE_LIST_FAVORITES;
                    break;
            }
            item.setChecked(true);
            fetchMovies(mMovieListType);
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
                saveMovieType(ApplicationConstants.PREF_MOVIE_LIST_POPULAR);
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                getTopRatedMovies();
                saveMovieType(ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED);
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                new DatabaseAsyncTask(this, getActivity(), null).execute(ApplicationConstants.TASK_QUERY_FAVORITE_LIST, null);
                saveMovieType(ApplicationConstants.PREF_MOVIE_LIST_FAVORITES);
                break;
            default:
                saveMovieType(ApplicationConstants.PREF_MOVIE_LIST_POPULAR);
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
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getPopularMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.GONE);
                populateRecyclerView(mMovieListType, new ArrayList<>(response.body().getResults()));
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
        toggleProgressBar(true);

        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getTopRatedMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                toggleProgressBar(false);
                mIsFetching = false;
                mEmptyView.setVisibility(View.GONE);
                populateRecyclerView(mMovieListType, new ArrayList<>(response.body().getResults()));
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ApplicationConstants.KEY_MOVIE_OBJECTS, (ArrayList<? extends Parcelable>) mResultList);
        outState.putInt(ApplicationConstants.KEY_LIST_POSITION, mRecyclerViewAdapter.getPosition());
        outState.putString(ApplicationConstants.KEY_MOVIE_LIST_TYPE, mMovieListType);
    }

    /**
     * Helper method that saves the selected movie type in shared preference.
     *
     * @param movieType Movie Type
     */
    private void saveMovieType(String movieType) {
        sharedPreferenceManager.saveToSharedPreferences(
                getString(R.string.prefs_key_type), movieType);
    }

    @Override
    public void CallbackRequest(String request, Bundle bundle) {
        mMovieDetailCallback.CallbackRequest(ApplicationConstants.CALLBACK_MOVIE_BUNDLE, bundle);
    }

    @Override
    public void CallbackRequest(String requestType, String bundleData) {
        if (requestType.equals(ApplicationConstants.CALLBACK_REFRESH_FAVORITES) && !mIsFetching) {
            fetchMovies(mMovieListType);
        }
    }

    @Override
    public void CallbackRequest(String requestType, ArrayList<Result> resultArrayList) {
        populateRecyclerView(mMovieListType, resultArrayList);
        saveMovieType(mMovieListType);
        mIsFetching = false;
        toggleProgressBar(false);
    }

    /**
     * Helper method to load items to the recyclerView
     *
     * @param movieListType   Type of movie to display
     * @param resultArrayList {@link Result} Movie objects
     */
    private void populateRecyclerView(String movieListType, ArrayList<Result> resultArrayList) {
        if (resultArrayList == null || resultArrayList.size() == 0) {
            if (ApplicationConstants.PREF_MOVIE_LIST_FAVORITES.equals(movieListType)) {
                mRecyclerViewAdapter.reloadRecyclerView(false, new ArrayList<Result>());
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(getString(R.string.no_favorite_movie_text));
            } else {
                if (movieListType.equals(sharedPreferenceManager.getMovieType()))
                    mRecyclerViewAdapter.reloadRecyclerView(true, new ArrayList<Result>());
                else {
                    //ToDo check network
                    mRecyclerViewAdapter.reloadRecyclerView(false, new ArrayList<Result>());
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setText(getString(R.string.no_network_text));
                }
            }
        } else {
            if (ApplicationConstants.PREF_MOVIE_LIST_FAVORITES.equals(movieListType)) {
                mRecyclerViewAdapter.reloadRecyclerView(false, resultArrayList);
                mRecyclerView.scrollToPosition(0);
                toggleProgressBar(false);
            } else {
                mRecyclerViewAdapter.reloadRecyclerView(true, resultArrayList);
                toggleProgressBar(false);
            }
        }
    }
}
