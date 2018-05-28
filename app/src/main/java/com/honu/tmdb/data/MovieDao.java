package com.honu.tmdb.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.honu.tmdb.rest.Genre;
import com.honu.tmdb.rest.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 *
 */
@Dao
public interface MovieDao {

    // Retrieve a single Movie by its id
    @Query("SELECT * FROM movie WHERE movie_id = :movie_id")
//    LiveData<Movie> getMovieById(int movie_id);
    Movie getMovieById(int movie_id);

    @Insert(onConflict = REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    // Retrieve all User objects
    @Query("SELECT * FROM movie")
//    LiveData<List<Movie>> getAllMovies();
    List<Movie> getAllMovies();

    @Query("SELECT genre_id FROM movie_genre WHERE movie_id = :movie_id")
//    LiveData<List<Integer>> getGenres(int movie_id);
    List<Integer> getGenreIds(int movie_id);

    @Query("SELECT * FROM movie_genre WHERE movie_id = :movie_id")
//    LiveData<List<Integer>> getGenres(int movie_id);
    List<Genre> getGenres(int movie_id);

    @Insert(onConflict = REPLACE)
    void insertAllGenres(List<Genre> genresList);

    @Query("DELETE FROM movie_genre WHERE movie_id = :movie_id")
    void deleteGenres(int movie_id);

}
