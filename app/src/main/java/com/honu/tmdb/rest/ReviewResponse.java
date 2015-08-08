package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 */
public class ReviewResponse {

    @SerializedName("id")
    int id;

    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Review> reviews;

    @SerializedName("total_pages")
    int totalPages;

    @SerializedName("total_results")
    int totalResults;

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
