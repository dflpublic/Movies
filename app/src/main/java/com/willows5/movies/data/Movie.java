package com.willows5.movies.data;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Movie {
    public static final String TMDB_BASE_URL     = "https://api.themoviedb.org/3/movie/";
    public static final String API_KEY           = "api_key";
    public static final String MY_KEY            = "6d3afdbb178e40f7936312362dd90a2d";
    public static final String IMAGE_DETAIL_PATH = "https://image.tmdb.org/t/p/w185/";
    public static final String IMAGE_MAIN_PATH   = "https://image.tmdb.org/t/p/w92/";
    static final        String TAG               = Movie.class.getSimpleName();

    int    _nId;
    String _sTitle;
    String _sDate;
    String _sVote;
    String _sDesc;
    String _sPoster;

    public Movie(int nId, String sTitle, String sDate, String sVote, String sDesc, String sPoster) {
        _nId = nId;
        _sTitle = sTitle;
        _sDate = sDate;
        _sVote = sVote;
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

    public String getDesc() {
        return _sDesc;
    }

    public String getPoster() {
        return _sPoster;
    }

    public static URL buildUrlMovie(String sId) {
        Uri uri = Uri.parse(TMDB_BASE_URL + sId).buildUpon()
                .appendQueryParameter(API_KEY, MY_KEY)
                .build();

        return getUrl(uri);
    }

    public static Movie[] getMovieData(String s) {
        Movie[] movies = null;
        try {
            JSONObject root    = new JSONObject(s);
            JSONArray  results = root.optJSONArray("results");
            int        num     = 20;// root.optInt("total_results");
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

    public String getRating() {
        return _sVote;
    }
}
