package com.honu.tmdb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.honu.tmdb.data.MovieContract.MovieEntry;
import com.honu.tmdb.data.MovieContract.MovieGenreEntry;

/**
 *
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movies.db";

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
              MovieEntry._ID + " INTEGER PRIMARY KEY," +
              MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
              MovieEntry.COLUMN_TITLE + " TEXT, " +
              MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
              MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
              MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
              MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
              MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
              MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
              MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
              MovieEntry.COLUMN_POPULARITY + " REAL, " +
              MovieEntry.COLUMN_FAVORITE + " INTEGER, " +
              MovieEntry.COLUMN_LANGUAGE + " TEXT, " +
              MovieEntry.COLUMN_VIDEO + " INTEGER, " +
              MovieEntry.COLUMN_ADULT + " INTEGER, " +
              "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID +") ON CONFLICT IGNORE " +
              ");";

        final String SQL_CREATE_GENRES_TABLE = "CREATE TABLE " + MovieGenreEntry.TABLE_NAME + " (" +
              MovieGenreEntry._ID + " INTEGER PRIMARY KEY," +
              MovieGenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
              MovieGenreEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL " +
              ");";

        // create tables:
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
