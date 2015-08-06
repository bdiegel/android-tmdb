package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Movie {

    @SerializedName("id")
    int id;

    @SerializedName("adult")
    boolean isAdult;

    @SerializedName("backdrop_path")
    String backdropPath;

    @SerializedName("genre_ids")
    int[] genreIds;

    @SerializedName("original_language")
    String originalLanguage;

    @SerializedName("original_title")
    String originalTitle;

    @SerializedName("overview")
    String overview;

    @SerializedName("release_date")
    String releaseDate;

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("popularity")
    float popularity;

    @SerializedName("title")
    String title;

    @SerializedName("video")
    boolean isVideo;

    @SerializedName("vote_average")
    float voteAverage;

    @SerializedName("vote_count")
    int voteCount;

    static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";

    // Image sizes
    static final String SIZE_W92 = "w92";
    static final String SIZE_W154 = "w154";
    static final String SIZE_W185 = "w185";
    static final String SIZE_W342 = "w342";
    static final String SIZE_W500 = "w500";
    static final String W780 = "w780";
    static final String SIZE_ORIGINAL = "original";

    // recommended for most phones:
    static final String SIZE_DEFAULT = SIZE_W185;

    public int getId() {
        return id;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPosterUrl() {
        return BASE_IMG_URL + SIZE_W185 + posterPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }
}
