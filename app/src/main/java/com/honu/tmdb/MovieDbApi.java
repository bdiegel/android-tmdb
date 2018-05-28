package com.honu.tmdb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.honu.tmdb.rest.ApiError;
import com.honu.tmdb.rest.MovieListResponse;
import com.honu.tmdb.rest.MovieResponse;
import com.honu.tmdb.rest.MovieService;
import com.honu.tmdb.rest.ReviewResponse;
import com.honu.tmdb.rest.VideoResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class MovieDbApi {

    private static MovieDbApi sInstance;

    private static String sApiKey;

    private static MovieService sService;

    // service endpoint
    static final String BASE_URL = "http://api.themoviedb.org/3/";

    static final String TAG = MovieDbApi.class.getSimpleName();


    public MovieDbApi(@NonNull String apiKey) {
        this.sApiKey = apiKey;
    }


    public interface MovieListener<R> {
        public void success(R response);
        public void error(ApiError error);
    }

    public static MovieDbApi getInstance(@NonNull String apiKey) {

        if (sInstance == null) {
            sInstance = new MovieDbApi(apiKey);
        }

        return sInstance;
    }


    public void requestMostPopularMovies(@Nullable final MovieListener<MovieListResponse> listener) {
        getMovieService().fetchPopularMovies(null).enqueue(new MovieCallback("requestMostPopularMovies", listener));
    }

    public void requestMostPopularMovies(int page, @Nullable final MovieListener<MovieListResponse> listener) {
        getMovieService().fetchPopularMovies(page).enqueue(new MovieCallback("requestMostPopularMovies", listener));
    }

    public void requestHighestRatedMovies(@Nullable final MovieListener<MovieListResponse> listener) {
        getMovieService().fetchHighestRatedMovies(null).enqueue(new MovieCallback("requestHighestRatedMovies", listener));
    }

    public void requestHighestRatedMovies(int page, @Nullable final MovieListener<MovieListResponse> listener) {
        getMovieService().fetchHighestRatedMovies(page).enqueue(new MovieCallback("requestHighestRatedMovies", listener));
    }

    public void requestMovieReviews(@NonNull int movieId, @Nullable final MovieListener<ReviewResponse> listener) {
        getMovieService().fetchReviews(movieId).enqueue(new MovieCallback("requestMovieReviews", listener));
    }

    public void requestMovieVideos(@NonNull int movieId, @Nullable final MovieListener<VideoResponse> listener) {
        getMovieService().fetchVideos(movieId).enqueue(new MovieCallback("requestMovieVideos", listener));
    }

    public void requestMovie(@NonNull int movieId, @Nullable final MovieListener<MovieResponse> listener) {
        getMovieService().fetchMovie(movieId).enqueue(new MovieCallback("requestMovie", listener));
    }

    private MovieService getMovieService() {

        if (sService == null ) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add logging:
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);

            // add api key to every request:
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl url = original.url().newBuilder()
                          .addQueryParameter("api_key", sApiKey)
                          .build();
                    Request.Builder requestBuilder = original.newBuilder().url(url);

                    return chain.proceed(requestBuilder.build());
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .client(httpClient.build())
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();

            sService = retrofit.create(MovieService.class);
        }

        return sService;
    }

    public static class MovieCallback<T, R> implements Callback<T> {

        MovieListener<R> listener;
        String operation;

        public MovieCallback(String operation, MovieListener<R> listener) {
            this.operation = operation;
            this.listener = listener;
        }

        /**
         * Invoked for a received HTTP response.
         * <p>
         * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
         * Call {@link Response#isSuccessful()} to determine if the response indicates success.
         *
         * @param call
         * @param response
         */
        @Override
        public void onResponse(Call<T> call, Response<T> response) {

            if (listener == null) {
                return;
            }

            if (response.isSuccessful()) {
                Log.e(TAG, "Api call successful: " + operation);

                T content = response.body();
                if (content == null || content instanceof Void) {
                    listener.success(null);
                } else {
                    listener.success((R)content);
                }
            } else {
                Log.e(TAG, "Api call failed: " + operation);

                int statusCode = response.code();
                String statusMessage = response.message();
                ResponseBody errorBody = response.errorBody();

                if (errorBody!=null) {
                    listener.error(new ApiError(statusCode, statusMessage, new Exception(operation + " failed: " + parseErrorBody(errorBody))));
                } else {
                    listener.error(new ApiError(statusCode, statusMessage, new Exception(operation + " failed: ")));
                }
            }
        }

        /**
         * Invoked when a network exception occurred talking to the server or when an unexpected
         * exception occurred creating the request or processing the response.
         *
         * @param call
         * @param t
         */
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(TAG, "Api call error: " + t.getMessage());
            if (listener != null) {
                listener.error(new ApiError(t));
            }
        }
    }

    private static String parseErrorBody(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (IOException e) {
            return null;
        }
    }
}
