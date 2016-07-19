package com.thomaskioko.moviemaniac.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.api.TmdbApiClient;
import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.adapters.MoviesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    @Bind(R.id.movie_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private TmdbApiClient mTmdbApiClient;
    private List<Result> mResultList = new ArrayList<>();
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mTmdbApiClient = MovieManiacApplication.getTmdbApiClient();

        int NUMBER_OF_GRID_ITEMS = 3;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), NUMBER_OF_GRID_ITEMS);
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mResultList.size() == 0) {
            getPopularMovies();
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    /**
     * Method to get Top Rated movies
     */
    private void getTopRatedMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        mProgressBar.setVisibility(View.VISIBLE);
        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getTopRatedMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                mProgressBar.setVisibility(View.GONE);
                for (Result result : response.body().getResults()) {
                    mResultList.add(result);
                    mRecyclerView.setAdapter(new MoviesRecyclerViewAdapter(getApplicationContext(),
                            getSupportFragmentManager(), mTwoPane, mResultList));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(LOG_TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Method to get Popular movies
     */
    private void getPopularMovies() {
        mResultList.clear();
        mRecyclerView.setAdapter(null);
        mProgressBar.setVisibility(View.VISIBLE);
        Call<Movie> topRatedList = mTmdbApiClient.movieInterface().getPopularMovies();
        topRatedList.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                mProgressBar.setVisibility(View.GONE);
                for (Result result : response.body().getResults()) {
                    mResultList.add(result);
                    mRecyclerView.setAdapter(new MoviesRecyclerViewAdapter(getApplicationContext(),
                            getSupportFragmentManager(), mTwoPane, mResultList));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(LOG_TAG, "@getTopRatedMovies Error Message:: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_movies, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular:
                getPopularMovies();
                return true;
            case R.id.action_top_rated:
                getTopRatedMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
