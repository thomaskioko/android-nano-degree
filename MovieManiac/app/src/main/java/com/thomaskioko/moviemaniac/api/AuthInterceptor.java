package com.thomaskioko.moviemaniac.api;

import com.thomaskioko.moviemaniac.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class add information (API Key) to {@link okhttp3.OkHttpClient} which is passed in
 * {@link TmdbApiClient#getRestAdapter()} which is required when making a request.
 *
 * @author Thomas Kioko
 */
public class AuthInterceptor implements Interceptor {

    /**
     * Default constructor.
     */
    public AuthInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url().newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
