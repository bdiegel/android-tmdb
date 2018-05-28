package com.honu.tmdb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.honu.tmdb.data.MovieDatabase;
import com.honu.tmdb.data.MovieRepository;
import com.honu.tmdb.rest.ApiError;
import com.honu.tmdb.rest.Movie;
import com.honu.tmdb.rest.Review;
import com.honu.tmdb.rest.ReviewResponse;
import com.honu.tmdb.rest.Video;
import com.honu.tmdb.rest.VideoResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovieDetailFragment extends Fragment {

    static final String TAG = MovieDetailFragment.class.getSimpleName();

    public static final String KEY_MOVIE = "movie";

    Movie mMovie;

    MovieDbApi mApi;

    MovieDetailsAdapter mAdapter;

    ShareActionProvider mShareActionProvider;

    MenuItem mShareMenuItem;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    MovieDbApi.MovieListener<ReviewResponse> reviewListener = new MovieDbApi.MovieListener<ReviewResponse>() {

        @Override
        public void success(ReviewResponse response) {
            onSuccess(response);
        }

        @Override
        public void error(ApiError error) {
            onError(error);
        }
    };

    MovieDbApi.MovieListener<VideoResponse> videoListener = new MovieDbApi.MovieListener<VideoResponse>() {

        @Override
        public void success(VideoResponse response) {
            onSuccess(response);
        }

        @Override
        public void error(ApiError error) {
            onError(error);
        }
    };

    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(KEY_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, null);
        ButterKnife.bind(this, view);

        // Create adapter
        mAdapter = new MovieDetailsAdapter();

        // Request reviews and trailers
        mApi = MovieDbApi.getInstance(getString(R.string.tmdb_api_key));
        if (mMovie != null) {
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            mApi.requestMovieReviews(mMovie.getId(), reviewListener);
            mApi.requestMovieVideos(mMovie.getId(), videoListener);
            queryGenres();
        }

        setupTransition();

        // show options menu
        setHasOptionsMenu(true);

        return view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition() {
        //setEnterTransition(new Explode());
        setSharedElementEnterTransition(new Slide());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        getActivity().getMenuInflater().inflate(R.menu.menu_movie_detail_frag, menu);
        mShareMenuItem = menu.findItem(R.id.menu_item_share);
        mShareMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                shareVideoUrl();
                return true;
            }
        });
    }

    private void onSuccess(ReviewResponse response) {
        List<Review> reviews = response.getReviews();
        Log.d(TAG, "Number of reviews: " + reviews.size());
        mAdapter.setReviews(reviews);
    }

    private void onSuccess(VideoResponse response) {
        List<Video> trailers = response.getYoutubeTrailers();
        Log.d(TAG, "Number of YouTube trailers: " + trailers.size());
        mAdapter.setTrailers(trailers);
    }

    private void onError(ApiError error) {
        Log.e(TAG, "Error retrieving data from API: " + error.getReason());
    }

    private void shareVideoUrl() {

        Uri url = mAdapter.getFirstTrailerUri();
        if (url != null) {
            Intent shareIntent = new Intent();
            shareIntent.setType("text/plain");
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_action_subject_prefix) + mMovie.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, url.toString());
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.title_share_action)));
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.message_no_trailer) + mMovie.getTitle(), Toast.LENGTH_LONG).show();
        }
    }

    // query local database for genre ids in offline mode
    private void queryGenres() {

        if (mMovie.getGenreIds().length == 0 && MovieFavorites.isFavoriteMovie(getActivity(), mMovie.getId())) {

            // @TODO - query genres / update adpater
            Executors.newSingleThreadExecutor().execute(
                  new Runnable() {
                    @Override
                    public void run() {
                        MovieDatabase movieDatabase = MovieDatabase.getInstance(MovieDetailFragment.this.getActivity().getApplication());
                        MovieRepository repository = new MovieRepository(movieDatabase);
                        List<Integer> genreIds = repository.getGenreIds(mMovie.getId());
                        //mMovie.setGenreIds(genreIds);
                        int[] ids = new int[genreIds.size()];
                        int i = 0;
                        for (Integer genreId : genreIds) {
                            ids[i++] = genreId;
                        }
                        mMovie.setGenreIds(ids);
                        mAdapter.updateGenres();
                    }
                }
            );
        }
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

        public void updateGenres() {
            this.notifyDataSetChanged();
        }

        public Uri getFirstTrailerUri() {
            return !trailers.isEmpty() ? trailers.get(0).getYoutubUrl() : null;
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
                    Picasso.get()
                          .load(mMovie.getPosterUrl(screenWidth))
                          .placeholder(R.drawable.ic_local_movies_white_36dp)
                          .into(headerViewHolder.posterView);
                    headerViewHolder.ratingView.setText("" + mMovie.getVoteAverage());
                    headerViewHolder.releaseView.setText(mMovie.getReleaseDate());
                    headerViewHolder.synopsisView.setText(mMovie.getOverview());
                    headerViewHolder.genres.setText(TextUtils.join(", ", mMovie.getMovieGenres()).toUpperCase());
                    break;
                case TRAILER_VIEW_TYPE:
                    Video trailer = trailers.get(position - 1);
                    MovieTrailerViewHolder trailerViewHolder = (MovieTrailerViewHolder) holder;
                    trailerViewHolder.videoTitle.setText(trailer.getName());
                    Log.d(TAG, "Adding trailer: " + trailer.getName());
                    break;
                case REVIEW_VIEW_TYPE:
                    Review review = reviews.get(position - trailers.size() - 1);
                    MovieReviewViewHolder reviewViewHolder = (MovieReviewViewHolder) holder;
                    reviewViewHolder.author.setText(review.getAuthor());
                    reviewViewHolder.content.setText("\"" + review.getContent() + "\"");
                    Log.d(TAG, "Adding review: " + review.getAuthor());
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

            if (mMovie == null)
                return -1;

            if (position == 0) {
                viewType = HEADER_VIEW_TYPE;
            } else if (position < trailers.size() + 1) {
                viewType =  TRAILER_VIEW_TYPE;
            } else {
                viewType =  REVIEW_VIEW_TYPE;
            }

            //Log.d(TAG, "getItemViewType - position:" + position + " type: " + viewType);
            return viewType;
        }

        class MovieHeaderViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.movie_detail_poster)
            ImageView posterView;

            @BindView(R.id.movie_detail_rating)
            TextView ratingView;

            @BindView(R.id.movie_detail_release)
            TextView releaseView;

            @BindView(R.id.movie_detail_synopsis)
            TextView synopsisView;

            @BindView(R.id.movie_genres)
            TextView genres;

            public MovieHeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.video_name)
            TextView videoTitle;

            @BindView(R.id.view_play_button)
            ImageButton playButton;

            public MovieTrailerViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick({R.id.view_play_button, R.id.video_name})
            public void playTrailer(View v) {
                Uri url = trailers.get(getAdapterPosition() - 1).getYoutubUrl();
                Log.d(TAG, "Play url: " + url);
                startActivity(new Intent(Intent.ACTION_VIEW, url));
            }
        }


        class MovieReviewViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.review_author)
            TextView author;

            @BindView(R.id.review_content)
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
