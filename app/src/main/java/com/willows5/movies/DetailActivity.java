package com.willows5.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willows5.movies.data.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String sText = intent.getStringExtra(Intent.EXTRA_TEXT);
            Movie  movie = Movie.movieFromString(sText);

            tvTitle.setText(movie.getTitle());
            tvDate.setText(movie.getDate());
            tvRating.setText(movie.getRating());
            tvDesc.setText(movie.getDesc());

            Picasso.with(this)
                    .load(Movie.IMAGE_DETAIL_PATH + movie.getPoster())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(ivPoster);
        }
    }

}
