package com.thomaskioko.moviemaniac.api;

import com.thomaskioko.moviemaniac.model.Movie;
import com.thomaskioko.moviemaniac.model.Reviews;
import com.thomaskioko.moviemaniac.model.Videos;

import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.assertEquals;

/**
 * @author Thomas Kioko
 */
public class MovieApiTest extends BaseTestCase {

    @Test
    public void getTopRatedMovies() throws IOException {

        Call<Movie> topRatedList = getTmdbApiClient().movieInterface().getTopRatedMovies();
        Response<Movie> movie = topRatedList.execute();

        assertEquals(200, movie.code());
        assertEquals(true, movie.isSuccessful());

    }

    @Test
    public void getPopularMovies() throws IOException {

        Call<Movie> popularMovies = getTmdbApiClient().movieInterface().getPopularMovies();
        Response<Movie> movie = popularMovies.execute();

        assertEquals(200, movie.code());
        assertEquals(true, movie.isSuccessful());
    }

    @Test
    public void getMovieReviews() throws IOException {

        Call<Reviews> popularMovies = getTmdbApiClient().movieInterface().getMovieReviews(188927);
        Response<Reviews> movie = popularMovies.execute();

        assertEquals(200, movie.code());
        assertEquals(true, movie.isSuccessful());
    }

    @Test
    public void getPopularVideos() throws IOException {

        Call<Videos> popularMovies = getTmdbApiClient().movieInterface().getMovieVideos(188927);
        Response<Videos> movie = popularMovies.execute();

        assertEquals(200, movie.code());
        assertEquals(true, movie.isSuccessful());
    }
}
