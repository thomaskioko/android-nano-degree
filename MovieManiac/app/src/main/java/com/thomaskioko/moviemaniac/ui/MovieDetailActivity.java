package com.thomaskioko.moviemaniac.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.data.DbUtils;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.fragments.MovieDetailFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 *
 * @author Thomas Kioko
 */
public class MovieDetailActivity extends AppCompatActivity {

    @Bind(R.id.fab)
    FloatingActionButton mFloationActionButton;
    @Bind(R.id.coordinated_layout)
    CoordinatorLayout mCoordinatedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {

            MovieDetailFragment fragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fab})
    void onClickViews(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Result result = MovieManiacApplication.getResult();

                DbUtils dbUtils = new DbUtils(getApplicationContext());
                long recordId = dbUtils.addFavoriteMovie(
                        result.getId(), result.getTitle(), result.getOverview(), result.getPosterPath(),
                        result.getBackdropPath(), result.getPopularity(), result.getVoteAverage(),
                        result.getVoteCount(), result.getReleaseDate()
                );

                //Check if the record was added successfully and display a notification
                if (recordId > 0) {

                    Snackbar snackbar = Snackbar
                            .make(mCoordinatedLayout, result.getTitle() + " has been added to Favorites", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    snackbar.show();

                } else {
                    Snackbar snackbar = Snackbar
                            .make(mCoordinatedLayout, "Something went wrong", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    snackbar.show();
                }
                break;
            default:
                break;
        }
    }
}
