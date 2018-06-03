package com.honu.tmdb.data;

import android.arch.lifecycle.LiveData;

import com.honu.tmdb.rest.Genre;
import com.honu.tmdb.rest.Movie;

import java.util.List;

/**
 *
 */
public class MovieRepository {

    MovieDatabase mMovieDatabase;

    public MovieRepository(MovieDatabase movieDatabase) {
        mMovieDatabase = movieDatabase;
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovieDatabase.movieDao().getAllMovies();
    }

    public LiveData<List<Integer>> getGenreIds(int movie_id) {
        return mMovieDatabase.movieDao().getGenreIds(movie_id);
    }

    public LiveData<List<Genre>> getGenres(int movie_id) {
        return mMovieDatabase.movieDao().getGenres(movie_id);
    }
}
