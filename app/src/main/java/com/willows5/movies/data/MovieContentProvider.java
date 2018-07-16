package com.willows5.movies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.willows5.movies.data.MovieContract.MovieEntry;

public class MovieContentProvider extends ContentProvider {

    public static final int FAVORITES        = 100;
    public static final int FAVORITE_WITH_ID = 101;

    static final         String     VND_ANDROID_CURSOR = "vnd.android.cursor";
    static final         String     VND_DIR            = ".dir";
    static final         String     VND_ITEM           = ".item";
    private static final UriMatcher sUriMatcher        = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                return VND_ANDROID_CURSOR + VND_DIR + "/" + MovieContract.AUTHORITY + "/" +
                        MovieContract.PATH_FAVORITES;
            case FAVORITE_WITH_ID:
                return VND_ANDROID_CURSOR + VND_ITEM + "/" + MovieContract.AUTHORITY + "/" +
                        MovieContract.PATH_FAVORITES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri                  returnUri;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                long id = db.insertOrThrow(MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int                  nDeleted;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_WITH_ID:
                long id = ContentUris.parseId(uri);
                nDeleted = db.delete(MovieEntry.TABLE_NAME, "_ID=?", new String[]{String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (nDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int nUpdated;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_WITH_ID:
                long id = ContentUris.parseId(uri);
                nUpdated = db.update(MovieEntry.TABLE_NAME, values, "_ID=?", new String[]
                        {String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (nUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nUpdated;
    }
}
