package com.thomaskioko.moviemaniac.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.ui.adapters.ViewPagerAdapter;
import com.thomaskioko.moviemaniac.ui.fragments.FavoriteMovieFragment;
import com.thomaskioko.moviemaniac.ui.fragments.MovieDetailFragment;
import com.thomaskioko.moviemaniac.ui.fragments.PopularMoviesFragment;
import com.thomaskioko.moviemaniac.ui.fragments.TopRatedMoviesFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularMoviesFragment(), "Popular");
        adapter.addFragment(new TopRatedMoviesFragment(), "Top Rated");
        adapter.addFragment(new FavoriteMovieFragment(), "Favorite");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            MovieManiacApplication.isTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new MovieDetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
            MovieManiacApplication.isTwoPane = false;
        }
    }


}
