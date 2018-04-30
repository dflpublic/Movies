package com.willows5.movies.utilities;

import android.net.Uri;
import android.util.Log;

import com.willows5.movies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String MOVIE_LIST    = "/discover/movie";
    private static final String MOVIE         = "/movie";
    private static final String SORT_BY       = "sort_by";
    public static final  String POPULARITY    = "popularity.desc";
    public static final  String RATING        = "vote_average.desc";
    private static final String API_KEY       = "api_key";
    private static final String MY_KEY        = "6d3afdbb178e40f7936312362dd90a2d";
    public static final  String IMAGE_PATH    = "https://image.tmdb.org/t/p/w185/";

    public static URL buildUrlMovieList(String sSortBy) {
        Uri uri = Uri.parse(TMDB_BASE_URL + MOVIE_LIST).buildUpon()
                .appendQueryParameter(API_KEY, MY_KEY)
                .appendQueryParameter(SORT_BY, sSortBy)
                .build();

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

    public static URL buildUrlMovie(String sId) {
        Uri uri = Uri.parse(TMDB_BASE_URL + MOVIE + "/" + sId).buildUpon()
                .appendQueryParameter(API_KEY, MY_KEY)
                .build();

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

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
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
}
