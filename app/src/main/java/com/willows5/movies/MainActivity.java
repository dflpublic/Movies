package com.willows5.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.willows5.movies.MoviesAdapter.MoviesAdapterOnClickHandler;
import com.willows5.movies.data.Movie;
import com.willows5.movies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

//done: fix layout for DetailActivity
//done: create layout for RecyclerView content
//done: use RecyclerView to show data
//done: add menu or spinner for sortby type
//todo: add page buttons to navigate
//done: create horizontal layout for MainActivity
//done: create horizontal layout for DetailActivity

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler {
    @BindView(R.id.rvMain)
    RecyclerView rvMain;
    @BindView(R.id.tv_error_message_display)
    TextView tvError;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;
    MoviesAdapter _adapter;

    static final String SORT      = "sort";
    static final String POPULAR   = "popular";
    static final String TOP_RATED = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int               nCols         = getNumColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, nCols);
        rvMain.setLayoutManager(layoutManager);
        rvMain.setHasFixedSize(true);

        _adapter = new MoviesAdapter(this);
        rvMain.setAdapter(_adapter);

        loadMovies();
    }

    void loadMovies() {
        showMovies();
        SharedPreferences pref  = getPreferences(Context.MODE_PRIVATE);
        String            sWhat = pref.getString(SORT, POPULAR);

        new FetchMoviesTask().execute(sWhat);
    }

    private void setSort(String sWhat) {
        SharedPreferences        pref   = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SORT, sWhat);
        editor.commit();
    }

    public Movie getMovie(int n) {
        return _adapter.getMovie(n);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_popular:
                setSort(POPULAR);
                break;

            case R.id.menu_top_rated:
                setSort(TOP_RATED);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        loadMovies();
        return true;
    }

    int getNumColumns(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        //float          dpWidth    = metrics.widthPixels / metrics.density;
        int imageWidth = getResources().getDimensionPixelSize(R.dimen.thumbnail_image_width);
        int numColumns = metrics.widthPixels / imageWidth;
        return numColumns;
    }

    @Override
    public void onClick(int n) {
        Intent intent = new Intent(this, DetailActivity.class);
        Movie  movie  = getMovie(n);
        String s = String.format("%d\n%s\n%s\n%s\n%s\n%s", movie.getId(), movie.getTitle(), movie
                .getDate(), movie.getRating(), movie.getDesc(), movie.getPoster());
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(intent);
    }

    private void showMovies() {
        tvError.setVisibility(View.INVISIBLE);
        rvMain.setVisibility(View.VISIBLE);
    }

    private void showError() {
        tvError.setVisibility(View.VISIBLE);
        rvMain.setVisibility(View.INVISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sWhat = params[0];

            URL requestUrl = Movie.buildUrlMovie(sWhat);
            try {
                String s = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                if (s != null) {
                    Movie[] movies = Movie.getMovieData(s);
                    return movies;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            progressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovies();
                _adapter.setMovies(movies);
            } else {
                showError();
            }
        }
    }
}
