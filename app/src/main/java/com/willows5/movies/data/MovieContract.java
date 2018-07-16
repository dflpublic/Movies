package com.willows5.movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY        = "com.willows5.movies";
    public static final Uri    BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES   = "favorites";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath
                (PATH_FAVORITES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID    = "movie_id";
        public static final String COLUMN_TITLE       = "title";
        public static final String COLUMN_DATE        = "date";
        public static final String COLUMN_RATING      = "rating";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_POSTER      = "poster";
    }
}
