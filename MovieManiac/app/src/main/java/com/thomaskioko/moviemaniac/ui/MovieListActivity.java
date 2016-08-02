package com.thomaskioko.moviemaniac.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;
import com.thomaskioko.moviemaniac.util.SharedPreferenceManager;

import butterknife.ButterKnife;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity  {

    public static boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        twoPane = findViewById(R.id.movie_details_container) != null;
        MovieManiacApplication.isTwoPane = twoPane;

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
                setTitle("Popular");
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_TOP_RATED:
                setTitle("Top Rated");
                break;
            case ApplicationConstants.PREF_MOVIE_LIST_FAVORITES:
                setTitle("Favorites");
                break;
        }
    }

}
