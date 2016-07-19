package com.thomaskioko.moviemaniac.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.thomaskioko.moviemaniac.MovieManiacApplication;
import com.thomaskioko.moviemaniac.R;
import com.thomaskioko.moviemaniac.model.Result;
import com.thomaskioko.moviemaniac.ui.MovieDetailActivity;
import com.thomaskioko.moviemaniac.ui.MovieListActivity;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    @Bind(R.id.layout_movie_title)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.movie_detail_year)
    TextView mMovieYear;
    @Bind(R.id.movie_detail_plot)
    TextView mMoviePlot;
    @Bind(R.id.movie_detail_thumbnail)
    ImageView mThumbnail;
    @Bind(R.id.movie_detail_rating)
    TextView mMovieRating;
    @Bind(R.id.movie_detail_popularity)
    TextView mMoviePopularity;
    @Bind(R.id.movie_detail_votes)
    TextView mMovieVote;
    @Bind(R.id.circularProgressBar)
    CircularProgressBar mCircularProgressBar;
    private Result mMovieResult;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieResult = MovieManiacApplication.getResult();

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            if (mMovieResult != null) {
                appBarLayout.setTitle(mMovieResult.getTitle());
                final ImageView imageView = (ImageView) activity.findViewById(R.id.ivBigImage);

                //Image URL
                String imagePath = ApplicationConstants.TMDB_IMAGE_URL
                        + ApplicationConstants.IMAGE_SIZE_780
                        + mMovieResult.getBackdropPath();


                Glide.with(imageView.getContext())
                        .load(imagePath)
                        .asBitmap()
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, final GlideAnimation glideAnimation) {
                                super.onResourceReady(bitmap, glideAnimation);
                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {

                                        if (palette.getDarkVibrantSwatch() != null) {
                                            mRelativeLayout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                            mCircularProgressBar.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());

                                        } else if (palette.getMutedSwatch() != null) {
                                            mRelativeLayout.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                            mCircularProgressBar.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                        }
                                        if (palette.getLightVibrantSwatch() != null) {
                                            mCircularProgressBar.setColor(palette.getLightVibrantSwatch().getRgb());
                                        } else if (palette.getLightMutedSwatch() != null) {
                                            mCircularProgressBar.setColor(palette.getLightMutedSwatch().getRgb());
                                        }
                                    }
                                });
                            }
                        });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        //Image path
        String imagePath = ApplicationConstants.TMDB_IMAGE_URL
                + ApplicationConstants.IMAGE_SIZE_185
                + mMovieResult.getPosterPath();

        Glide.with(getActivity())
                .load(imagePath)
                .asBitmap()
                .centerCrop()
                .into(mThumbnail);

        float rating = mMovieResult.getVoteAverage().floatValue() * 10;
        float popularity = mMovieResult.getPopularity().intValue();

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
                .withLocale(Locale.getDefault());

        //Get the year from the release date.
        LocalDate date = formatter.parseLocalDate(mMovieResult.getReleaseDate());

        mMoviePlot.setText(mMovieResult.getOverview());
        mMovieYear.setText(String.valueOf(date.getYear()));
        mMovieRating.setText(String.valueOf(mMovieResult.getVoteAverage()));
        mMoviePopularity.setText(String.valueOf(popularity));
        mMovieVote.setText(String.valueOf(mMovieResult.getVoteCount()));
        mCircularProgressBar.setProgressWithAnimation(rating);

        return rootView;
    }
}
