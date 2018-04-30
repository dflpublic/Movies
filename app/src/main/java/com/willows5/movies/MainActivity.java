package com.willows5.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.willows5.movies.data.Movie;
import com.willows5.movies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHello = (TextView) findViewById(R.id.tv_hello);

        new FetchMovieTask().execute(NetworkUtils.POPULARITY);
    }

    public void helloClicked(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, tvHello.getText().toString());
        startActivity(intent);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String sSortBy    = params[0];
            URL    requestUrl = NetworkUtils.buildUrlMovieList(sSortBy);
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return jsonResponse;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Movie[] movies = NetworkUtils.getMovieData(s);
                int     n      = (int) (Math.random() * movies.length);
                Movie   movie  = movies[n];
                String sText = String.format("%d\n%s\n%s\n%s\n%s\n%s", movie.getId(), movie
                                .getTitle(), movie.getDate(), movie.getVote(), movie.getDesc(),
                        movie.getPoster());

                tvHello.setText(sText);
            }
        }
    }


}
