package com.thomaskioko.moviemaniac.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.interfaces.MovieDetailCallback;
import com.thomaskioko.moviemaniac.interfaces.MovieCallback;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.fragments.MovieDetailFragment;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;
import com.thomaskioko.moviemaniac.util.SharedPreferenceManager;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements MovieCallback, MovieDetailCallback {

    public static boolean isTwoPane = false;
    public static MovieCallback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        isTwoPane = findViewById(R.id.movie_details_container) != null;
        MovieManiacApplication.isTwoPane = isTwoPane;

        setUpToolBarTitle();

    }

    /**
     * Method to set Toolbar titles
     */
    private void setUpToolBarTitle() {

        //Set title from preference
        SharedPreferenceManager sharedPreferences = new SharedPreferenceManager(getApplicationContext());
        switch (sharedPreferences.getMovieType()) {
            case ApplicationConstants.PREF_MOVIE_LIST_POPULAR:
                setTitle(getString(R.string.menu_action_popular));
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                setTitle(getString(R.string.menu_action_top_rated));
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                setTitle(getString(R.string.menu_action_favorites));
                break;
        }
    }

    /**
     * Helper method to instantiate {@link #mCallback}
     * @param callback {@link MovieCallback}
     */
    public static void setCallback(MovieCallback callback) {
        mCallback = callback;
    }

    @Override
    public void CallbackRequest(String requestType, String bundleData) {
        if (requestType.equals(ApplicationConstants.CALLBACK_REFRESH_FAVORITES)) {
            mCallback.CallbackRequest(ApplicationConstants.CALLBACK_REFRESH_FAVORITES, "");
        }
    }

    @Override
    public void CallbackRequest(String requestType, ArrayList<Result> resultArrayList) {
    }

    @Override
    public void CallbackRequest(String request, Bundle bundle) {

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_container, fragment)
                .commit();
    }
}
