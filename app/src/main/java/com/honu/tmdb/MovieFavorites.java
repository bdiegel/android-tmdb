package com.honu.tmdb;

import android.content.Context;
import android.content.SharedPreferences;

import com.honu.tmdb.data.MovieDatabase;
import com.honu.tmdb.rest.Genre;
import com.honu.tmdb.rest.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Caches ids of favorite movies and persists them to SharedPreferences
 *
 * Movie details are persisted to SQLite by MovieProvider.
 */
public final class MovieFavorites {

    private static int IMG_RESOURCE_IS_FAVORITE = R.drawable.ic_favorite_white_24dp;
    private static int IMG_RESOURCE_NOT_FAVORITE = R.drawable.ic_favorite_border_white_24dp;

    private static final String TAG = MovieFavorites.class.getSimpleName();

    static final String PREF_FILE_NAME = "FavoriteMovies";

    static List<Integer> sFavoriteMovies;

    private static void loadFavoriteMovies(Context context) {
        if (sFavoriteMovies == null) {
            sFavoriteMovies = new ArrayList<Integer>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            synchronized (sharedPreferences) {
                Set<String> keys = sharedPreferences.getAll().keySet();
                for (String key : keys) {
                    sFavoriteMovies.add(sharedPreferences.getInt(key, -1));
                }
            }
        }
    }

    private static void saveFavoriteMoviePreference(Context context, int movieId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id-" + movieId, movieId).commit();
    }

    private static void removeFavoriteMoviePreference(Context context, int movieId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("id-" + movieId).commit();
    }

    public static List<Integer> getFavoriteMovies(Context context) {
        loadFavoriteMovies(context);
        return sFavoriteMovies;
    }

    public static boolean isFavoriteMovie(Context context, int movieId) {
        loadFavoriteMovies(context);
        return sFavoriteMovies.contains(movieId);
    }

    public static void addFavoriteMovie(Context context, int movieId) {
        loadFavoriteMovies(context);
        if (!sFavoriteMovies.contains(movieId)) {
            sFavoriteMovies.add(movieId);
            saveFavoriteMoviePreference(context, movieId);
        }
    }

    public static void removeFavoriteMovie(Context context, int movieId) {
        loadFavoriteMovies(context);
        if (sFavoriteMovies.contains(movieId)) {
            sFavoriteMovies.remove(sFavoriteMovies.indexOf(movieId));
            removeFavoriteMoviePreference(context, movieId);
        }
    }

    public static void updateFavorite(final Context context, boolean isFavorite, final Movie movie) {
        if (isFavorite) {
            addFavoriteMovie(context, movie.getId());

            // @TODO: addFavorite(context, movie);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    MovieDatabase.getInstance(context).movieDao().insertMovie(movie);
                    // add genres
                    List<Genre> genres = new ArrayList<>();
                    for (int id : movie.getGenreIds()) {
                        Genre genre = new Genre(id, movie.getId());
                        genres.add(genre);
                    }
                    MovieDatabase.getInstance(context).movieDao().insertAllGenres(genres);
                }
            });
        } else {
            removeFavoriteMovie(context, movie.getId());

            // @TODO: removeFavorite(context, movie);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    MovieDatabase.getInstance(context).movieDao().deleteMovie(movie);
                    MovieDatabase.getInstance(context).movieDao().deleteGenres(movie.getId());
                }
            });
        }
    }

    public static int getImageResourceId(boolean isFavorite) {
        if (isFavorite) {
            return IMG_RESOURCE_IS_FAVORITE;
        }
        return IMG_RESOURCE_NOT_FAVORITE;
    }
}
