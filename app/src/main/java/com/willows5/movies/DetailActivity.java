package com.willows5.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.willows5.movies.data.Movie;
import com.willows5.movies.data.Review;
import com.willows5.movies.data.Video;
import com.willows5.movies.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    public static final String MY_INTENT = "myIntent";

    @BindView(R.id.tv_title)
    TextView  tvTitle;
    @BindView(R.id.tv_date)
    TextView  tvDate;
    @BindView(R.id.tv_rating)
    TextView  tvRating;
    @BindView(R.id.tv_desc)
    TextView  tvDesc;
    @BindView(R.id.iv_poster)
    ImageView ivPoster;

    @BindView(R.id.ll_reviews)
    LinearLayout llReviews;
    @BindView(R.id.ll_videos)
    LinearLayout llVideos;
    @BindView(R.id.add_favorite)
    ToggleButton btnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent.hasExtra(MY_INTENT)) {
//            String sText = intent.getStringExtra(Intent.EXTRA_TEXT);
//            Movie  movie = Movie.movieFromString(sText);

            final Movie movie = intent.getParcelableExtra(MY_INTENT);

            tvTitle.setText(movie.getTitle());
            tvDate.setText(movie.getDate());
            tvRating.setText(movie.getRating());
            tvDesc.setText(movie.getDesc());

            btnFavorite.setChecked(movie.favoriteExists(getBaseContext()));

            btnFavorite.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    movie.toggleFavorite(getBaseContext());
                }
            });

            doAsyncLoad(movie);

            Picasso.with(this)
                    .load(Movie.IMAGE_DETAIL_PATH + movie.getPoster())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(ivPoster);
        }
    }

    private void doAsyncLoad(Movie movie) {
        new AsyncTask<Movie, Void, Movie>() {

            @Override
            protected Movie doInBackground(Movie... movie1) {
                movie1[0].setMovieReviews(movie1[0].getReviews());
                movie1[0].setMovieVideos(movie1[0].getVideos());
                return movie1[0];
            }

            @Override
            protected void onPostExecute(Movie data) {
                loadReviewsAndVideos(data);
            }
        }.execute(movie);
    }

    private void loadReviewsAndVideos(Movie movie) {
        Review[] reviews = movie.getMovieReviews();
        if (reviews != null) {
            for (int i = 0; i < reviews.length; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.review, null);

                TextView tvAuthor  = view.findViewById(R.id.author);
                TextView tvContent = view.findViewById(R.id.content);

                tvAuthor.setText(reviews[i].getAuthor());
                tvContent.setText(reviews[i].getContent());

                llReviews.addView(view);
            }
        }
        LinearLayout  llVideos = findViewById(R.id.ll_videos);
        final Video[] videos   = movie.getMovieVideos();
        if (videos != null) {
            for (int i = 0; i < videos.length; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.video, null);

                TextView tvType = view.findViewById(R.id.type);
                TextView tvName = view.findViewById(R.id.name);

                final Video video = videos[i];
                tvType.setText(video.getType());
                tvName.setText(video.getName());

                tvName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkUtils.watchYoutubeVideo(getBaseContext(), video.getKey());
                    }
                });
                llVideos.addView(view);
            }
        }
    }
}
