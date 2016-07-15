package com.thomaskioko.moviemaniac.api.interfaces;

import com.thomaskioko.moviemaniac.model.Movie;

import retrofit2.Call;
import retrofit2.http.GET;

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
}
