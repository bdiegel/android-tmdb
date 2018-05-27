package com.honu.tmdb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.honu.tmdb.rest.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity  {

    static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String KEY_MOVIE = "movie";

    Movie mMovie;

    boolean mIsFavorite = false;

    @BindView(R.id.movie_detail_backdrop)
    ImageView mBackdropView;

    @BindView(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(KEY_MOVIE);

        setStatusBarTransparent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_MOVIE, mMovie);

        if (savedInstanceState == null ) {
            Fragment fragment = MovieDetailFragment.newInstance(mMovie);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detail_recycler_container, fragment).commit();
        }

        // bind data to the app bar
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        Picasso.get().load(mMovie.getBackdropUrl(screenWidth)).into(mBackdropView);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) this.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovie.getTitle());
        mIsFavorite = MovieFavorites.isFavoriteMovie(this, mMovie.getId());

        // set image if favorite
        mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));

        setupTransition();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition() {
        getWindow().setEnterTransition(new Explode());
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
        toggleFavorite();
    }

    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
        MovieFavorites.updateFavorite(this, mIsFavorite, mMovie);
    }
}
