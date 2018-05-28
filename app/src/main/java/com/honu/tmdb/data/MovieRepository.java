package com.honu.tmdb.data;

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

    public List<Movie> getMovies() {
        return mMovieDatabase.movieDao().getAllMovies();
    }

    public List<Integer> getGenreIds(int movie_id) {
        return mMovieDatabase.movieDao().getGenreIds(movie_id);
    }

    public List<Genre> getGenres(int movie_id) {
        return mMovieDatabase.movieDao().getGenres(movie_id);
    }
}
