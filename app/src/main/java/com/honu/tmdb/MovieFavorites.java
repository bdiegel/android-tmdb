package com.honu.tmdb;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.honu.tmdb.data.MovieContract;
import com.honu.tmdb.rest.Movie;

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

    public static void updateFavorite(Context context, boolean isFavorite, Movie movie) {
        if (isFavorite) {
            addFavoriteMovie(context, movie.getId());
            addFavorite(context, movie);
        } else {
            removeFavoriteMovie(context, movie.getId());
            removeFavorite(context, movie);
        }
    }

    public static void addFavorite(Context context, Movie movie) {
        Log.d(TAG, "Inserting favorite: " + movie.getTitle());

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        values.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, movie.getOriginalLanguage());
        values.put(MovieContract.MovieEntry.COLUMN_VIDEO, movie.isVideo());
        values.put(MovieContract.MovieEntry.COLUMN_ADULT, movie.isAdult());

        final AsyncQueryHandler handler = new AsyncCrudHandler(context.getContentResolver());
        handler.startInsert(2, null, MovieContract.MovieEntry.CONTENT_URI, values);
    }

    public static void removeFavorite(Context context, Movie movie) {
        Log.d(TAG, "Removing favorite: " + movie.getTitle());
        final AsyncQueryHandler handler = new AsyncCrudHandler(context.getContentResolver());
        handler.startDelete(2, null,
              MovieContract.MovieEntry.CONTENT_URI,
              MovieContract.MovieEntry.WHERE_MOVIE_ID,
              new String[]{"" + movie.getId()});
    }

    public static int getImageResourceId(boolean isFavorite) {
        if (isFavorite) {
            return IMG_RESOURCE_IS_FAVORITE;
        }
        return IMG_RESOURCE_NOT_FAVORITE;
    }

    static class AsyncCrudHandler extends AsyncQueryHandler {
        public AsyncCrudHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            Log.d(TAG, "Insert completed for uri: " + uri);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            Log.d(TAG, "Delete completed with result: " + result);
        }
    }
}
