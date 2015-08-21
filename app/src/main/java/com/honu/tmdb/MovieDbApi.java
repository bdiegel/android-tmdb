package com.honu.tmdb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.honu.tmdb.rest.ApiError;
import com.honu.tmdb.rest.MovieListResponse;
import com.honu.tmdb.rest.MovieResponse;
import com.honu.tmdb.rest.MovieService;
import com.honu.tmdb.rest.ReviewResponse;
import com.honu.tmdb.rest.VideoResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 *
 */
public class MovieDbApi {

    private static MovieDbApi sInstance;

    private static String sApiKey;

    private static MovieService sService;

    // service endpoint
    static final String ENDPOINT = "http://api.themoviedb.org/3";

    static final String TAG = MovieDbApi.class.getSimpleName();


    public MovieDbApi(@NonNull String apiKey) {
        this.sApiKey = apiKey;
    }


    public interface MovieListener {
        public void success(MovieResponse response);
        public void error(ApiError error);
    }

    public interface MovieListListener {
        public void success(MovieListResponse response);
        public void error(ApiError error);
    }


    public interface VideoListener {
        public void success(VideoResponse response);
        public void error(ApiError error);
    }


    public interface ReviewListener {
        public void success(ReviewResponse response);
        public void error(ApiError error);
    }


    public static MovieDbApi getInstance(@NonNull String apiKey) {

        if (sInstance == null) {
            sInstance = new MovieDbApi(apiKey);
        }

        return sInstance;
    }


    public void requestMostPopularMovies(@Nullable final MovieListListener listener) {

        getMovieService().fetchPopularMovies(sApiKey, new Callback<MovieListResponse>() {
            @Override
            public void success(MovieListResponse response, Response httpResponse) {
                Log.d(TAG, "Number of movies found: " + response.getMovies().size());
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api error: " + error.getMessage());
                if (listener != null) {
                    listener.error(new ApiError(error));
                }
            }
        });
    }

    public void requestHighestRatedMovies(@Nullable final MovieListListener listener) {

        getMovieService().fetchHighestRatedMovies(sApiKey, new Callback<MovieListResponse>() {
            @Override
            public void success(MovieListResponse response, Response httpResponse) {
                Log.d(TAG, "Number of movies found: " + response.getMovies().size());
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api error: " + error.getMessage());
                if (listener != null) {
                    listener.error(new ApiError(error));
                }
            }
        });
    }

    public void requestMovieReviews(@NonNull int movieId, @Nullable final ReviewListener listener) {

        getMovieService().fetchReviews(movieId, sApiKey, new Callback<ReviewResponse>() {
            @Override
            public void success(ReviewResponse response, Response httpResponse) {
                Log.d(TAG, "Number of reviews found: " + response.getReviews().size());
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api error: " + error.getMessage());
                if (listener != null) {
                    listener.error(new ApiError(error));
                }
            }
        });
    }

    public void requestMovieVideos(@NonNull int movieId, @Nullable final VideoListener listener) {

        getMovieService().fetchVideos(movieId, sApiKey, new Callback<VideoResponse>() {
            @Override
            public void success(VideoResponse response, Response httpResponse) {
                Log.d(TAG, "Total number of videos found: " + response.getVideos().size());
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api error: " + error.getMessage());
                if (listener != null) {
                    listener.error(new ApiError(error));
                }
            }
        });
    }

    public void requestMovie(@NonNull int movieId, @Nullable final MovieListener listener) {

        getMovieService().fetchMovie(movieId, sApiKey, new Callback<MovieResponse>() {
            @Override
            public void success(MovieResponse response, Response httpResponse) {
                Log.d(TAG, "Movie found: " + response.getMovie().getTitle());
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Api error: " + error.getMessage());
                if (listener != null) {
                    listener.error(new ApiError(error));
                }
            }
        });
    }

    private MovieService getMovieService() {

        if (sService == null ) {
            Gson gson = new GsonBuilder().create();

            RestAdapter.Builder builder = new RestAdapter.Builder()
                  .setEndpoint(ENDPOINT)
                  .setConverter(new GsonConverter(gson))
                  .setClient(new OkClient())
                  .setLogLevel(RestAdapter.LogLevel.BASIC);
                  //.setLogLevel(RestAdapter.LogLevel.FULL);

            sService = builder.build().create(MovieService.class);
        }

        return sService;
    }
}
