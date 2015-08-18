package com.honu.tmdb;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Caches ids of favorite movies and persists them to SharedPreferences
 *
 */
public final class MovieFavorites {

    private static int IMG_RESOURCE_IS_FAVORITE = R.drawable.ic_favorite_white_24dp;
    private static int IMG_RESOURCE_NOT_FAVORITE = R.drawable.ic_favorite_border_white_24dp;

    private static final String LOG_TAG = MovieFavorites.class.getSimpleName();

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
        editor.putInt("id-"+ movieId, movieId).commit();
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

    public static void updateFavorite(Context context, boolean isFavorite, int movieId) {
        if (isFavorite) {
            MovieFavorites.addFavoriteMovie(context, movieId);
        } else {
            MovieFavorites.removeFavoriteMovie(context, movieId);
        }
    }

    public static int getImageResourceId(boolean isFavorite) {
        if (isFavorite) {
            return IMG_RESOURCE_IS_FAVORITE;
        }
        return IMG_RESOURCE_NOT_FAVORITE;
    }
}
