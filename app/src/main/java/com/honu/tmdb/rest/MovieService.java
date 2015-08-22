package com.honu.tmdb.rest;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Defines selected services from TMDb API
 * <p>
 * Example:  GET /discover/movie?sort_by=popularity.desc?api_key=XXXXXX
 */
public interface MovieService {

    // Top 20 movies sorted by specified criteria
    @GET("/discover/movie")
    void discoverMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey, Callback<MovieListResponse> cb);

    // Top 20 sorted by popularity
    //@GET("/discover/movie?sort_by=popularity.desc")
    @GET("/movie/popular")
    void fetchPopularMovies(@Query("api_key") String apiKey, Callback<MovieListResponse> cb);

    // Next page of 20 sorted by popularity
    //@GET("/discover/movie?sort_by=popularity.desc")
    @GET("/movie/popular")
    void fetchPopularMovies(@Query("api_key") String apiKey, @Query("page") int page, Callback<MovieListResponse> cb);

    // Top 20 sorted by rating
    // @GET("/discover/movie?sort_by=vote_average.desc")
    @GET("/movie/top_rated")
    void fetchHighestRatedMovies(@Query("api_key") String apiKey, Callback<MovieListResponse> cb);

    // Next page of 20 sorted by rating
    //@GET("/discover/movie?sort_by=vote_average.desc")
    @GET("/movie/top_rated")
    void fetchHighestRatedMovies(@Query("api_key") String apiKey, @Query("page") int page, Callback<MovieListResponse> cb);

    // Get movie details by id
    @GET("/movie/{id}")
    void fetchMovie(@Path("id") int movieId, @Query("api_key") String apiKey, Callback<MovieResponse> cb);

    // Video trailers, clips, etc
    @GET("/movie/{id}/videos")
    void fetchVideos(@Path("id") int movieId, @Query("api_key") String apiKey, Callback<VideoResponse> cb);

    // Movie reviews
    @GET("/movie/{id}/reviews")
    void fetchReviews(@Path("id") int movieId, @Query("api_key") String apiKey, Callback<ReviewResponse> cb);

    // http://api.themoviedb.org/3/movie/{movie_id}?api_key=your_key&append_to_response=trailers,reviews
}
