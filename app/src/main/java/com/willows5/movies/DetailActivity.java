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
            String   sText   = intent.getStringExtra(Intent.EXTRA_TEXT);
            String[] sa      = sText.split("\n");
            String   sId     = sa[0];
            String   sTitle  = sa[1];
            String   sDate   = sa[2];
            String   sRating = sa[3];
            String   sDesc   = sa[4];
            String   sPoster = sa[5];

            tvTitle.setText(sTitle);
            tvDate.setText(sDate);
            tvRating.setText(sRating);
            tvDesc.setText(sDesc);

            Picasso.with(this)
                    .load(Movie.IMAGE_DETAIL_PATH + sPoster)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(ivPoster);
        }
    }

}
