package com.thomaskioko.moviemaniac.api;

import com.thomaskioko.moviemaniac.api.interfaces.MovieInterface;
import com.thomaskioko.moviemaniac.util.ApplicationConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API Client helper class used to configure Retrofit object.
 *
 * @author Thomas Kioko
 */

public class TmdbApiClient {

    private Retrofit retrofit;
    private boolean isDebug;
    private HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    /**
     * Set the {@link Retrofit} log level.
     *
     * @param isDebug If true, the log level is set to
     *                {@link HttpLoggingInterceptor.Level#BODY}. Otherwise
     *                {@link HttpLoggingInterceptor.Level#NONE}.
     */
    public TmdbApiClient setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
        if (retrofit != null) {
            httpLoggingInterceptor.
                    setLevel(isDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        }
        return this;
    }

    /**
     * Configure OkHttpClient
     *
     * @return OkHttpClient
     */
    private OkHttpClient okHttpClient() {

        return new OkHttpClient.Builder()
                .connectTimeout(ApplicationConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApplicationConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApplicationConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    /**
     * Create a new {@link Retrofit.Builder}. Override this to e.g. set your own client or executor.
     *
     * @return A {@link Retrofit.Builder} with no modifications.
     */
    private Retrofit.Builder newRestAdapterBuilder() {
        return new Retrofit.Builder();
    }

    /**
     * Return the current {@link Retrofit} instance. If none exists (first call, API key changed),
     * builds a new one.
     * <p/>
     * When building, sets the endpoint and a {@link HttpLoggingInterceptor} which adds the API key as query param.
     */
    private Retrofit getRestAdapter() {

        Retrofit.Builder builder = null;
        if (retrofit == null) {

            //Create a new instance of the Rest Adapter class
            builder = newRestAdapterBuilder();

            builder.baseUrl(ApplicationConstants.END_POINT);
            builder.client(okHttpClient());
            builder.addConverterFactory(GsonConverterFactory.create());
        }

        if (isDebug) {
            if (builder != null) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            }
        }
        if (builder != null) {
            retrofit = builder.build();
        }
        return retrofit;
    }

    /**
     * Create Movie service instance.
     *
     * @return Movie Service.
     */
    public MovieInterface movieInterface() {
        return getRestAdapter().create(MovieInterface.class);
    }

}
