package com.honu.tmdb.rest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;


@Entity(tableName = "movie_genre", primaryKeys = {"genre_id", "movie_id"})
public class Genre {

    @ColumnInfo(name = "genre_id")
    int genreId;

    @ColumnInfo(name = "movie_id")
    int id;

    public Genre(int genreId, int id) {
        this.genreId = genreId;
        this.id = id;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
