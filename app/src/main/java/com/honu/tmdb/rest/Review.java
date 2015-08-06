package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Review {

    @SerializedName("id")
    int id;

    @SerializedName("author")
    String author;

    @SerializedName("content")
    String content;

    @SerializedName("url")
    String url;

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
