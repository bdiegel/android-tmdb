package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Simple wrapper for the response to a request for a single movie.
 *
 * There are differences between the JSON returned for movies from the /movie/{id} and /discover endpoints.
 * In particular, the format for genre differs:
 *
 *  - the discover endpoint has an array of genre_ids.
 *  - the movie queried by id returns an array of objects with an id and name.
 */
public class MovieResponse extends Movie {

    @SerializedName("genres")
    List<Genre> genres;

    @Override
    public int[] getGenreIds() {
        int[] ids = new int[genres.size()];
        for (int i=0; i<genres.size(); i++) {
            ids[i] = genres.get(i).id;
        }
        return ids;
    }

    /**
     * Extracts the movie from the response (discarding this wrapper type).
     * Sets any fields on the base type (ie, genre_ids).
     */
    public Movie getMovie() {
        this.setGenreIds(getGenreIds());
        return this;
    }

    static class Genre {
        int id;
        String name;
    }
}
