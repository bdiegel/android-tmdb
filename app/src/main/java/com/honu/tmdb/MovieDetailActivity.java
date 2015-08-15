package com.honu.tmdb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.honu.tmdb.rest.ApiError;
import com.honu.tmdb.rest.Movie;
import com.honu.tmdb.rest.Review;
import com.honu.tmdb.rest.ReviewResponse;
import com.honu.tmdb.rest.Video;
import com.honu.tmdb.rest.VideoResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity implements MovieDbApi.ReviewListener, MovieDbApi.VideoListener {

    static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    Movie mMovie;

    boolean mIsFavorite = false;

    MovieDetailsAdapter mAdapter;

    MovieDbApi mApi;

    @Bind(R.id.movie_detail_backdrop)
    ImageView mBackdropView;

    @Bind(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;

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

//        Bundle bundle = new Bundle();
//        bundle.putParcelable(KEY_MOVIE, movie);

//        if (savedInstanceState == null ) {
//            Fragment fragment = new MovieDetailFragment();
//            fragment.setArguments(bundle);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.container, fragment).commit();
//        }

        // bind data to the app bar
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        Picasso.with(this).load(mMovie.getBackdropUrl(screenWidth)).into(mBackdropView);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) this.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovie.getTitle());
        mIsFavorite = MovieFavorites.isFavoriteMovie(this, mMovie.getId());

        // Create adapter
        mAdapter = new MovieDetailsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Request reviews and trailers
        mApi = MovieDbApi.getInstance(getString(R.string.tmdb_api_key));
        mApi.requestMovieReviews(mMovie.getId(), this);
        mApi.requestMovieVideos(mMovie.getId(), this);

        // set image if favorite
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

    @Override
    public void success(ReviewResponse response) {
        List<Review> reviews = response.getReviews();
        Log.d(TAG, "Number of reviews: " + reviews.size());
        mAdapter.setReviews(reviews);
    }

    @Override
    public void success(VideoResponse response) {
        List<Video> trailers = response.getYoutubeTrailers();
        Log.d(TAG, "Number of YouTube trailers: " + trailers.size());
        mAdapter.setTrailers(trailers);
    }

    @Override
    public void error(ApiError error) {
        Log.e(TAG, "Error retrieving data from API: " + error.getReason());
    }

    /**
     * Adapter for recycler view has 3 view types to display movie information. The first item is
     * always the header. It is followed by any trailers then any reviews.
     *
     *   1. Header  - details about the movie (poster, rating, genres, release, synopsis, etc)
     *   2. Trailer - video trailers on YouTube
     *   3. Review  - user review
     */
    public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static final int HEADER_VIEW_TYPE = 1;
        static final int TRAILER_VIEW_TYPE = 2;
        static final int REVIEW_VIEW_TYPE = 3;

        List<Review> reviews = new ArrayList<>();
        List<Video> trailers = new ArrayList<>();

        public void setReviews(List<Review> reviews) {
            this.reviews.clear();
            this.reviews.addAll(reviews);
            this.notifyDataSetChanged();
        }

        public void setTrailers(List<Video> trailers) {
            this.trailers.clear();
            this.trailers.addAll(trailers);
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case HEADER_VIEW_TYPE:
                    View headerView = inflater.inflate(R.layout.detail_header, parent, false);
                    holder = new MovieHeaderViewHolder(headerView);
                    break;
                case TRAILER_VIEW_TYPE:
                    View trailerView = inflater.inflate(R.layout.video_item, parent, false);
                    holder = new MovieTrailerViewHolder(trailerView);
                    break;
                case REVIEW_VIEW_TYPE:
                    View reviewView = inflater.inflate(R.layout.review_item, parent, false);
                    holder = new MovieReviewViewHolder(reviewView);
                    break;
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            switch (holder.getItemViewType()) {
                case HEADER_VIEW_TYPE:
                    MovieHeaderViewHolder headerViewHolder = (MovieHeaderViewHolder) holder;
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    Picasso.with(headerViewHolder.posterView.getContext()).load(mMovie.getPosterUrl(screenWidth)).into(headerViewHolder.posterView);
                    headerViewHolder.ratingView.setText("" + mMovie.getVoteAverage());
                    headerViewHolder.releaseView.setText(mMovie.getReleaseDate());
                    headerViewHolder.synopsisView.setText(mMovie.getOverview());
                    headerViewHolder.genres.setText(TextUtils.join(", ", mMovie.getMovieGenres()).toUpperCase());
                    break;
                case TRAILER_VIEW_TYPE:
                    Video trailer = trailers.get(position - 1);
                    MovieTrailerViewHolder trailerViewHolder = (MovieTrailerViewHolder) holder;
                    trailerViewHolder.videoTitle.setText(trailer.getName());
                    break;
                case REVIEW_VIEW_TYPE:
                    Review review = reviews.get(position - trailers.size() - 1);
                    MovieReviewViewHolder reviewViewHolder = (MovieReviewViewHolder) holder;
                    reviewViewHolder.author.setText(review.getAuthor());
                    reviewViewHolder.content.setText("\"" + review.getContent() + "\"");
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return 1 + reviews.size() + trailers.size();
        }

        @Override
        public int getItemViewType(int position) {
            int viewType;

            if (position == 0) {
                viewType = HEADER_VIEW_TYPE;
            } else if (position < trailers.size() + 1) {
                viewType =  TRAILER_VIEW_TYPE;
            } else {
                viewType =  REVIEW_VIEW_TYPE;
            }

            Log.d(TAG, "getItemViewType - position:" + position + " type: " + viewType);
            return viewType;
        }

        class MovieHeaderViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.movie_detail_poster)
            ImageView posterView;

            @Bind(R.id.movie_detail_rating)
            TextView ratingView;

            @Bind(R.id.movie_detail_release)
            TextView releaseView;

            @Bind(R.id.movie_detail_synopsis)
            TextView synopsisView;

            @Bind(R.id.movie_genres)
            TextView genres;

            public MovieHeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.video_name)
            TextView videoTitle;

            @Bind(R.id.view_play_button)
            ImageButton playButton;

            public MovieTrailerViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick({R.id.view_play_button, R.id.video_name})
            public void playTrailer(View v) {
                Video video = trailers.get(getAdapterPosition() - 1);
                Uri url = Uri.parse("http://www.youtube.com/watch?v=" + video.getKey());
                Log.d(TAG, "Play url: " + url);
                startActivity(new Intent(Intent.ACTION_VIEW, url));
            }
        }


        class MovieReviewViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.review_author)
            TextView author;

            @Bind(R.id.review_content)
            TextView content;

            public MovieReviewViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.review_content)
            public void openReview() {
                Review review = reviews.get(getAdapterPosition() - trailers.size() - 1);
                Log.d(TAG, "Display complete review: " + mMovie.getTitle());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
            }
        }
    }
}
