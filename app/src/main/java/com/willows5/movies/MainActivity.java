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

    String sSort;

    /*
    find out the number of columns supported by current screen
    create GridLayoutManager and set it to RecyclerView
    create adapter and set it to RecyclerView

    then load the data into the adapter
     */
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
        /*
        grab saved preference to determine which way to sort
         */
        showMovies();
        SharedPreferences pref  = getPreferences(Context.MODE_PRIVATE);
        sSort = pref.getString(SORT, POPULAR);

        new FetchMoviesTask().execute(sSort);
    }

    private void setSort(String sWhat) {
        SharedPreferences        pref   = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SORT, sWhat);
        editor.apply();
    }

    public Movie getMovie(int n) {
        return _adapter.getMovie(n);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        for some reason if preferences are gotten here, it doesn't seem to remember the saved
        setting
         */
        getMenuInflater().inflate(R.menu.main, menu);

        if (sSort.equals(POPULAR)) {
            menu.findItem(R.id.menu_popular).setChecked(true);
        } else {
            menu.findItem(R.id.menu_top_rated).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_popular:
                setSort(POPULAR);
                item.setChecked(true);
                break;

            case R.id.menu_top_rated:
                setSort(TOP_RATED);
                item.setChecked(true);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        loadMovies();
        return true;
    }

    int getNumColumns(Context context) {
        /*
        get the screen width and divide by the image width to determine the number of columns
        supported
         */
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        //float          dpWidth    = metrics.widthPixels / metrics.density;
        int imageWidth = getResources().getDimensionPixelSize(R.dimen.thumbnail_image_width);
        return metrics.widthPixels / imageWidth;
    }

    @Override
    public void onClick(int n) {
        Intent intent = new Intent(this, DetailActivity.class);
        Movie  movie  = getMovie(n);
        String s      = movie.makeString();
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
                    return Movie.getMovieData(s);
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
