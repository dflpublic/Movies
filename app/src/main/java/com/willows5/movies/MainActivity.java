package com.willows5.movies;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler,
                                                               LoaderManager
                                                                       .LoaderCallbacks<Movie[]> {
    @BindView(R.id.rvMain)
    RecyclerView rvMain;
    static final int    LOADER_ID = 42;
    static final String TAG       = MainActivity.class.getSimpleName();
    MoviesAdapter _adapter;

    static final String SORT      = "sort";
    static final String POPULAR   = "popular";
    static final String TOP_RATED = "top_rated";
    static final String FAVORITE  = "favorite";

    @BindView(R.id.tv_error_message_display)
    TextView    tvError;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

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
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        int               nCols         = getNumColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, nCols);
        rvMain.setLayoutManager(layoutManager);
        rvMain.setHasFixedSize(true);

        _adapter = new MoviesAdapter(this);
        rvMain.setAdapter(_adapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        loadMovies();
    }

    void loadMovies() {
        /*
        grab saved preference to determine which way to sort
         */
        showMovies();
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        sSort = pref.getString(SORT, POPULAR);

        Bundle bundle = new Bundle();
        bundle.putString(SORT, sSort);

        LoaderManager   loaderManager = getSupportLoaderManager();
        Loader<Movie[]> loader        = loaderManager.getLoader(LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, bundle, this);
        }
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

        switch (sSort) {
            case POPULAR:
                menu.findItem(R.id.menu_popular).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.menu_top_rated).setChecked(true);
                break;
            case FAVORITE:
                menu.findItem(R.id.menu_favorite).setChecked(true);
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

            case R.id.menu_favorite:
                setSort(FAVORITE);
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

        int imageWidth = getResources().getDimensionPixelSize(R.dimen.thumbnail_image_width);
        return metrics.widthPixels / imageWidth;
    }

    @Override
    public void onClick(int n) {
        Intent intent = new Intent(this, DetailActivity.class);
        Movie  movie  = getMovie(n);
        intent.putExtra("myIntent", movie);
//        String s      = movie.makeString();
//        intent.putExtra(Intent.EXTRA_TEXT, s);
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

    @NonNull
    @Override
    public Loader<Movie[]> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<Movie[]>(this) {
            Movie[] movies;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (movies != null) {
                    deliverResult(movies);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Movie[] loadInBackground() {
                String sWhat = args.getString(SORT);
                if (sWhat == null || TextUtils.isEmpty(sWhat)) {
                    return null;
                }
                if (!sWhat.equals(FAVORITE)) {
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
                } else {
                    return Movie.getFavorites(getContext());
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable Movie[] data) {
                movies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie[]> loader, Movie[] data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovies();
            _adapter.setMovies(data);
        } else {
            showError();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie[]> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMovies();
    }

}
