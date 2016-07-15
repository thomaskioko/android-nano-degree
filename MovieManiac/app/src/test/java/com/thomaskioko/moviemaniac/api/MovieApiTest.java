package com.thomaskioko.moviemaniac.api;

import com.thomaskioko.moviemaniac.model.Movie;

import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Kioko
 */
public class MovieApiTest extends BaseTestCase {

    @Test
    public void getTopRatedMovies() {

        Call<List<Movie>> topRatedList = getTmdbApiClient().movieInterface().getTopRatedMovies();
        topRatedList.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

                assertEquals(200, response.code());
                System.out.print(response.code());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

            }
        });
    }

    @Test
    public void getPopularMovies() {

        Call<List<Movie>> topRatedList = getTmdbApiClient().movieInterface().getPopularMovies();
        topRatedList.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

                assertEquals(200, response.code());
                System.out.print(response.code());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

            }
        });
    }
}
