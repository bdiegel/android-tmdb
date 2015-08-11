package com.honu.tmdb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honu.tmdb.rest.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity {

    static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    Movie mMovie;

    boolean mIsFavorite = false;

    @Bind(R.id.movie_detail_poster)
    ImageView mPosterView;

    @Bind(R.id.movie_detail_rating)
    TextView mRatingView;

    @Bind(R.id.movie_detail_release)
    TextView mReleaseView;

    @Bind(R.id.movie_detail_synopsis)
    TextView mSynopsisView;

    @Bind(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

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

        mIsFavorite = MovieFavorites.isFavoriteMovie(this, mMovie.getId());
        updateFabIcon();
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
        int id = item.getItemId();

        // handle back arrow in toolbar:
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_favorite)
    public void onClick(View v) {
        //Log.d(TAG, "Set favorite: " + (!mIsFavorite));
        toggleFavorite();
    }

    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        updateFabIcon();
        updateFavorites();
    }

    private void updateFabIcon() {
        if (mIsFavorite) {
            mFavoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mFavoriteFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    private void updateFavorites() {
        if (mIsFavorite) {
            MovieFavorites.addFavoriteMovie(this, mMovie.getId());
        } else {
            MovieFavorites.removeFavoriteMovie(this, mMovie.getId());
        }
    }

}
