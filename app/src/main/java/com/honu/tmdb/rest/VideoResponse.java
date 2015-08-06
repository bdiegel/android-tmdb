package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 */
public class VideoResponse {

    @SerializedName("id")
    int id;

    @SerializedName("results")
    List<Video> videos;

    public int getId() {
        return id;
    }

    public List<Video> getVideos() {
        return videos;
    }
}
