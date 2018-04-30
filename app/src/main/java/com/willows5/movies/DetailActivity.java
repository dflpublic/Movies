package com.willows5.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willows5.movies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity {
    TextView  tvDetail;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        ivPoster = (ImageView) findViewById(R.id.iv_poster);

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String sText   = intent.getStringExtra(Intent.EXTRA_TEXT);
            int    nLast   = sText.lastIndexOf("\n");
            String sPoster = sText.substring(nLast + 1);
            sText = sText.substring(0, nLast);
            tvDetail.setText(sText);

            Picasso.with(this)
                    .load(NetworkUtils.IMAGE_PATH + sPoster)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(ivPoster);
        }
    }

}
