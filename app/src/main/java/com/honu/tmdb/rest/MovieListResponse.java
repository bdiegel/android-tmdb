package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 */
public class MovieListResponse {

    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Movie> movies;

    @SerializedName("total_pages")
    int totalPages;

    @SerializedName("total_results")
    int totalResults;

    public int getPage() {
        return page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
