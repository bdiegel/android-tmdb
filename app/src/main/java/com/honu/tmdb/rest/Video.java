package com.honu.tmdb.rest;

import com.google.gson.annotations.SerializedName;

/**
 *
 */
public class Video {


    @SerializedName("id")
    String id;

    // ie, "en"
    @SerializedName("iso_639_1")
    String languageCode;

    @SerializedName("key")
    String key;

    @SerializedName("name")
    String name;

    // ie, YouTube
    @SerializedName("site")
    String site;

    @SerializedName("size")
    int size;

    // ie, Trailer
    @SerializedName("type")
    String type;

    public String getId() {
        return id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public boolean isYoutubeTrailer() {
        return (site.toLowerCase().equals("youtube") && type.toLowerCase().equals("trailer"));
    }
}
