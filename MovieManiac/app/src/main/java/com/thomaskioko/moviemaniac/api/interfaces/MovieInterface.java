package com.thomaskioko.moviemaniac.api.interfaces;

import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Reviews;
import com.thomaskioko.moviemaniac.model.Videos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface with endpoints
 *
 * @author Thomas Kioko
 */
public interface MovieInterface {

    /**
     * Get top rated movies
     *
     * @return JSON Result
     */
    @GET("top_rated?")
    Call<Movie> getTopRatedMovies();

    /**
     * Get popular movies.
     *
     * @return JSON Result
     */
    @GET("popular?")
    Call<Movie> getPopularMovies();

    /**
     * Get Movie Videos
     *
     * @param movieId Movie ID
     * @return JSON Result
     */
    @GET("{id}/videos")
    Call<Videos> getMovieVideos(@Path("id") int movieId);

    /**
     * Get Movie Reviews
     *
     * @param movieId Movie ID
     * @return JSON Result
     */
    @GET("{id}/reviews")
    Call<Reviews> getMovieReviews(@Path("id") int movieId);
}
