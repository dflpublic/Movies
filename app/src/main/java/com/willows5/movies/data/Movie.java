package com.willows5.movies.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.willows5.movies.BuildConfig;
import com.willows5.movies.data.MovieContract.MovieEntry;
import com.willows5.movies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Movie implements Parcelable {
    public static final String TMDB_BASE_URL     = "https://api.themoviedb.org/3/movie/";
    public static final String API_KEY           = "api_key";
    public static final String MY_KEY            = BuildConfig.MY_TMDB_KEY;
    public static final String IMAGE_DETAIL_PATH = "https://image.tmdb.org/t/p/w185/";
    public static final String IMAGE_MAIN_PATH   = "https://image.tmdb.org/t/p/w92/";
    static final        String TAG               = Movie.class.getSimpleName();
    public static final String REVIEWS           = "/reviews";
    public static final String VIDEOS            = "/videos";

    int    _nId;
    String _sTitle;
    String _sDate;
    String _sRating;
    String _sDesc;
    String _sPoster;

    Review[] _reviews;
    Video[]  _videos;

    int idFavorite;

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(int nId, String sTitle, String sDate, String sVote, String sDesc, String sPoster) {
        _nId = nId;
        _sTitle = sTitle;
        _sDate = sDate;
        _sRating = sVote;
        _sDesc = sDesc;
        _sPoster = sPoster;
    }

    public int getId() {
        return _nId;
    }

    public String getTitle() {
        return _sTitle;
    }

    public String getDate() {
        return _sDate;
    }

    protected Movie(Parcel in) {
        this._nId = in.readInt();
        this._sTitle = in.readString();
        this._sDate = in.readString();
        this._sRating = in.readString();
        this._sDesc = in.readString();
        this._sPoster = in.readString();
    }

    public String getDesc() {
        return _sDesc;
    }

    @Nullable
    private static URL getUrl(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + uri);

        return url;
    }

    public String getPoster() {
        return _sPoster;
    }

    //currently only gets first page (20 movies) returned
    public static Movie[] getMovieData(String s) {
        Movie[] movies = null;
        try {
            JSONObject root    = new JSONObject(s);
            JSONArray  results = root.optJSONArray("results");
            int        num     = Math.min(20, results.length());// root.optInt("total_results");
            movies = new Movie[num];

            for (int i = 0; i < num; i++) {
                JSONObject first        = results.getJSONObject(i);
                int        nId          = first.optInt("id");
                String     sTitle       = first.optString("title");
                String     sDate        = first.optString("release_date");
                String     sVote        = first.optString("vote_average");
                String     sDescription = first.optString("overview");
                String     sImage       = first.optString("poster_path");

                movies[i] = new Movie(nId, sTitle, sDate, sVote, sDescription, sImage);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static Movie[] getFavorites(Context context) {
        Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI, null, null,
                null, null);
        Movie[] movies = new Movie[cursor.getCount()];
        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext()) {
                int    nId     = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                String sTitle  = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                String sDate   = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_DATE));
                String sRating = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RATING));
                String sDesc = cursor.getString(cursor.getColumnIndex(MovieEntry
                        .COLUMN_DESCRIPTION));
                String sPoster = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                movies[count++] = new Movie(nId, sTitle, sDate, sRating, sDesc, sPoster);
            }
            cursor.close();
        }
        return movies;
    }

    public static URL buildUrlMovie(String sId) {
        Uri uri = Uri.parse(TMDB_BASE_URL + sId).buildUpon()
                .appendQueryParameter(API_KEY, MY_KEY)
                .build();

        return getUrl(uri);
    }

    public static Movie movieFromString(String s) {
        Gson  gson  = new Gson();
        Movie movie = gson.fromJson(s, Movie.class);
        return movie;
    }

    public String getRating() {
        return _sRating;
    }

    public String makeString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Review[] getMovieReviews() {
        return _reviews;
    }

    public void setMovieReviews(Review[] reviews) {
        this._reviews = reviews;
    }

    public Video[] getMovieVideos() {
        return _videos;
    }

    public void setMovieVideos(Video[] videos) {
        this._videos = videos;
    }

    public Review[] getReviews() {
        Review[] reviews = null;
        URL      url     = buildUrlMovie(_nId + REVIEWS);
        try {
            String sJson = NetworkUtils.getResponseFromHttpUrl(url);

            JSONObject root    = new JSONObject(sJson);
            JSONArray  results = root.optJSONArray("results");

            reviews = new Review[results.length()];

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj      = results.getJSONObject(i);
                String     sAuthor  = obj.optString("author");
                String     sContent = obj.optString("content");
                String     sId      = obj.optString("id");
                String     sUrl     = obj.optString("url");
                reviews[i] = new Review(sAuthor, sContent, sId, sUrl);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public Video[] getVideos() {
        Video[] videos = null;
        URL     url    = buildUrlMovie(_nId + VIDEOS);
        try {
            String sJson = NetworkUtils.getResponseFromHttpUrl(url);

            JSONObject root    = new JSONObject(sJson);
            JSONArray  results = root.optJSONArray("results");

            videos = new Video[results.length()];

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj   = results.getJSONObject(i);
                String     sId   = obj.optString("id");
                String     sKey  = obj.optString("key");
                String     sName = obj.optString("name");
                String     sType = obj.optString("type");
                videos[i] = new Video(sId, sKey, sName, sType);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return videos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._nId);
        dest.writeString(this._sTitle);
        dest.writeString(this._sDate);
        dest.writeString(this._sRating);
        dest.writeString(this._sDesc);
        dest.writeString(this._sPoster);

//        dest.writeTypedArray(this._reviews, flags);
//        dest.writeTypedArray(this._videos, flags);
    }

    public void toggleFavorite(Context context) {
        if (!favoriteExists(context)) {
            ContentValues cv = new ContentValues();
            cv.put(MovieEntry.COLUMN_MOVIE_ID, _nId);
            cv.put(MovieEntry.COLUMN_TITLE, _sTitle);
            cv.put(MovieEntry.COLUMN_DATE, _sDate);
            cv.put(MovieEntry.COLUMN_RATING, _sRating);
            cv.put(MovieEntry.COLUMN_DESCRIPTION, _sDesc);
            cv.put(MovieEntry.COLUMN_POSTER, _sPoster);

            Uri uri = context.getContentResolver().insert(MovieEntry.CONTENT_URI, cv);
            if (uri == null) {
                throw new SQLException("Unable to insert favorite movie " + _sTitle + " " + _nId
                        + ")");
            }
            Log.v(TAG, "Movie " + _sTitle + " (" + _nId + ") successfully added to favorites");
        } else {

            int nRemoved = context.getContentResolver().delete(ContentUris.withAppendedId
                    (MovieEntry.CONTENT_URI, idFavorite), null, null);

            if (nRemoved > 0) {
                Log.v(TAG, "Movie " + _sTitle + "(" + _nId + ") successfully removed from " +
                        "favorites");
            } else {
                throw new SQLException("Unable to remove favorite movie " + _sTitle + " (" + _nId
                        + ")");
            }
        }
    }

    public boolean favoriteExists(Context context) {
        Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI, null,
                MovieEntry
                        .COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(_nId)}, null);
        boolean bRet = (cursor != null) && (cursor.getCount() > 0);
        if (bRet) {
            cursor.moveToFirst();
            idFavorite = cursor.getInt(cursor.getColumnIndex(MovieEntry._ID));
        }
        cursor.close();
        return bRet;
    }
}
