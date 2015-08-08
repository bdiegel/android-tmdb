package com.honu.tmdb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.honu.tmdb.rest.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    static final String TAG = MovieDetailFragment.class.getSimpleName();

    Movie mMovie;

    private static final String KEY_MOVIE = "movie";

    @Bind(R.id.movie_detail_poster)
    ImageView mPosterView;

    @Bind(R.id.movie_detail_title)
    TextView mTitleView;

    @Bind(R.id.movie_detail_rating)
    TextView mRatingView;

    @Bind(R.id.movie_detail_release)
    TextView mReleaseView;

    @Bind(R.id.movie_detail_synopsis)
    TextView mSynopsisView;


    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args == null)
            return view;

        mMovie = args.getParcelable(KEY_MOVIE);

        Log.d(TAG, "Backdrop url: " + mMovie.getBackdropUrl());
        Picasso.with(getActivity()).load(mMovie.getBackdropUrl()).into(mPosterView);
        mTitleView.setText(mMovie.getTitle());
        mRatingView.setText(""+mMovie.getVoteAverage());
        mReleaseView.setText(mMovie.getReleaseDate());
        mSynopsisView.setText(mMovie.getOverview() + mMovie.getOverview() + mMovie.getOverview() + mMovie.getOverview() + mMovie.getOverview());

        return view;
    }
}
