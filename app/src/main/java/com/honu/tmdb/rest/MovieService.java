package com.honu.tmdb.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Defines selected services from TMDb API
 * <p>
 * Example:  GET /discover/movie?sort_by=popularity.desc?api_key=XXXXXX
 */
public interface MovieService {

    // Top 20 movies sorted by specified criteria
    @GET("discover/movie")
    Call<MovieListResponse> discoverMovies(@Query("sort_by") String sortBy);

    // Next page of 20 sorted by popularity
    //@GET("/discover/movie?sort_by=popularity.desc")
    @GET("movie/popular")
    Call<MovieListResponse> fetchPopularMovies(@Query("page") Integer page);

    // Next page of 20 sorted by rating
    //@GET("/discover/movie?sort_by=vote_average.desc")
    @GET("movie/top_rated")
    Call<MovieListResponse> fetchHighestRatedMovies(@Query("page") Integer page);

    // Get movie details by id
    @GET("movie/{id}")
    Call<MovieResponse> fetchMovie(@Path("id") int movieId);

    // Video trailers, clips, etc
    @GET("movie/{id}/videos")
    Call<VideoResponse> fetchVideos(@Path("id") int movieId);

    // Movie reviews
    @GET("movie/{id}/reviews")
    Call<ReviewResponse> fetchReviews(@Path("id") int movieId);

    // http://api.themoviedb.org/3/movie/{movie_id}?api_key=your_key&append_to_response=trailers,reviews
}
