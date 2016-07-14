package com.thomaskioko.moviemaniac.api.interfaces;

import com.thomaskioko.moviemaniac.model.Movie;

import java.util.List;

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
     * @param apiKey TMDB API Key
     * @return JSON Result
     */
    @GET("top_rated?{api_key}")
    Call<List<Movie>> getTopRatedMovies(@Path("api_key") String apiKey);

    /**
     * Get popular movies.
     *
     * @param apiKey TMDB API Key
     * @return JSON Result
     */
    @GET("popular?{api_key}")
    Call<List<Movie>> getPopularMovies(@Path("api_key") String apiKey);
}
