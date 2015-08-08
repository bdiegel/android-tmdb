package com.honu.tmdb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.honu.tmdb.rest.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    Movie mMovie;

    @Bind(R.id.movie_detail_poster)
    ImageView mPosterView;

    @Bind(R.id.movie_detail_rating)
    TextView mRatingView;

    @Bind(R.id.movie_detail_release)
    TextView mReleaseView;

    @Bind(R.id.movie_detail_synopsis)
    TextView mSynopsisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(KEY_MOVIE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        Bundle bundle = new Bundle();
//        bundle.putParcelable(KEY_MOVIE, movie);

//        if (savedInstanceState == null ) {
//            Fragment fragment = new MovieDetailFragment();
//            fragment.setArguments(bundle);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.container, fragment).commit();
//        }

        setStatusBarTransparent();
        ButterKnife.bind(this);

        Log.d(TAG, "Backdrop url: " + mMovie.getBackdropUrl());
        Picasso.with(this).load(mMovie.getBackdropUrl()).into(mPosterView);
        mRatingView.setText(""+mMovie.getVoteAverage());
        mReleaseView.setText(mMovie.getReleaseDate());
        mSynopsisView.setText(mMovie.getOverview());

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) this.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovie.getTitle());
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarTransparent() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
