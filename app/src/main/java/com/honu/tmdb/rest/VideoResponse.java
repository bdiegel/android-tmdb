package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
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
        if (videos == null) {
            return Collections.EMPTY_LIST;
        }
        return videos;
    }

    public List<Video> getYoutubeTrailers() {
        List<Video> youtubeTrailers = new ArrayList<>();
        for (Video video : videos) {
            if (video.isYoutubeTrailer()) {
                youtubeTrailers.add(video);
            }
        }
        return youtubeTrailers;
    }
}
