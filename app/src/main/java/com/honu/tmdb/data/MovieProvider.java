package com.honu.tmdb.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.honu.tmdb.data.MovieContract.MovieEntry;
import com.honu.tmdb.data.MovieContract.MovieGenreEntry;

public class MovieProvider extends ContentProvider {

    private MovieDatabaseHelper mDbHelper;

    private static final int MOVIE = 10;
    private static final int MOVIE_ID = 11;
    private static final int GENRE = 20;
    private static final int GENRE_ID = 21;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_GENRE, GENRE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_GENRE + "/#", GENRE_ID);
        return matcher;
    }

    private static final SQLiteQueryBuilder sMoviesWithGenresQueryBuilder = new SQLiteQueryBuilder();

    static {
        sMoviesWithGenresQueryBuilder.setTables(
              MovieEntry.TABLE_NAME + " INNER JOIN " + MovieGenreEntry.TABLE_NAME +
                    " ON " + MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID +
                    " = " + MovieGenreEntry.TABLE_NAME + "." + MovieGenreEntry.COLUMN_MOVIE_ID
        );
    }

    private static final String sSelectByMovieId = MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    public MovieProvider() {
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case GENRE:
                return MovieGenreEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case GENRE_ID:
                return MovieGenreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = -1;

        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                id = db.insert(MovieEntry.TABLE_NAME, null, values);
                break;
            }
            case GENRE: {
                id = db.insert(MovieGenreEntry.TABLE_NAME, null, values);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (id < 0)
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberOfRowsAffected = 0;
        String id = uri.getLastPathSegment();

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numberOfRowsAffected = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_ID:
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.update(MovieEntry.TABLE_NAME,
                          values,
                          MovieEntry._ID + " = ? ",
                          new String[]{id});
                } else {
                    numberOfRowsAffected = db.update(MovieEntry.TABLE_NAME,
                          values,
                          selection + " AND " + MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id));
                }
                break;
            case GENRE:
                numberOfRowsAffected = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GENRE_ID:
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.update(MovieGenreEntry.TABLE_NAME,
                          values,
                          MovieGenreEntry._ID + " = ? ",
                          new String[]{id});
                } else {
                    numberOfRowsAffected = db.update(MovieGenreEntry.TABLE_NAME,
                          values,
                          selection + " AND " + MovieGenreEntry._ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberOfRowsAffected = 0;
        String id = uri.getLastPathSegment();

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numberOfRowsAffected = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.delete(MovieEntry.TABLE_NAME,
                          MovieEntry._ID + " = ? ",
                          new String[]{id});
                } else {
                    numberOfRowsAffected = db.delete(MovieEntry.TABLE_NAME,
                          selection + " AND " +
                          MovieEntry._ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id));
                }
                break;
            case GENRE:
                numberOfRowsAffected = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE_ID:
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.delete(MovieGenreEntry.TABLE_NAME,
                          MovieGenreEntry._ID + " = ? ",
                          new String[]{id});
                } else {
                    numberOfRowsAffected = db.delete(MovieGenreEntry.TABLE_NAME,
                          selection + " AND " + MovieGenreEntry._ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsAffected;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String id = uri.getLastPathSegment();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = getMoviesWithGenres(uri, projection, selection, sortOrder);
                break;
            case GENRE:
                retCursor = db.query(MovieGenreEntry.TABLE_NAME,
                      projection,
                      selection,
                      selectionArgs,
                      "",
                      "",
                      sortOrder);
                break;
            case MOVIE_ID:
                if (TextUtils.isEmpty(selection)) {
                    retCursor = db.query(MovieEntry.TABLE_NAME,
                          projection,
                          MovieEntry._ID + " = ? ",
                          new String[]{id},
                          "",
                          "",
                          sortOrder);
                } else {
                    retCursor = db.query(MovieEntry.TABLE_NAME,
                          projection,
                          selection + " AND " + MovieEntry._ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id),
                          "",
                          "",
                          sortOrder);
                }
                break;
            case GENRE_ID:
                if (TextUtils.isEmpty(selection)) {
                    retCursor = db.query(MovieGenreEntry.TABLE_NAME,
                          projection,
                          MovieGenreEntry._ID + " = ? ",
                          new String[]{id},
                          "",
                          "",
                          sortOrder);
                } else {
                    retCursor = db.query(MovieGenreEntry.TABLE_NAME,
                          projection,
                          selection + " AND " + MovieGenreEntry._ID + " = ? ",
                          appendToSelectionArgs(selectionArgs, id),
                          "",
                          "",
                          sortOrder);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        };

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMoviesWithGenres(Uri uri, String[] projection, String selection, String sortOrder) {
        String movieIdSetting = MovieContract.MovieEntry.getIdFromUri(uri);

        return sMoviesWithGenresQueryBuilder.query(mDbHelper.getReadableDatabase(),
              projection,
              selection,
              new String[]{movieIdSetting},
              null,
              null,
              sortOrder
        );
    }

    private String[] appendToSelectionArgs(String[] selectionArgs, String appendArg) {
        String[] allArgs = new String[selectionArgs.length + 1];
        System.arraycopy(selectionArgs, 0, allArgs, 0, selectionArgs.length + 1);
        allArgs[selectionArgs.length] = appendArg;
        return allArgs;
    }

}
