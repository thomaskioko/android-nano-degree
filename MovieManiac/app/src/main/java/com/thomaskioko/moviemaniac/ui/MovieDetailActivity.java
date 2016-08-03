package com.thomaskioko.moviemaniac.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.ui.fragments.MovieDetailFragment;

import butterknife.ButterKnife;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 *
 * @author Thomas Kioko
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {

            MovieDetailFragment fragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
    }
}
