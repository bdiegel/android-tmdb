package com.honu.tmdb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.honu.tmdb.rest.ApiError;
import com.honu.tmdb.rest.Movie;
import com.honu.tmdb.rest.MovieResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment displays grid of movie posters using recycler view
 */
public class MoviePosterGridFragment extends Fragment implements MovieDbApi.MovieListener {

    static final String TAG = MoviePosterGridFragment.class.getSimpleName();

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;

    MovieDbApi mApi;
    MovieGridRecyclerAdapter mAdapter;

    //int mSortBy = R.id.sort_popularity;

    public MoviePosterGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movie_poster_grid, null);
        ButterKnife.bind(this, view);

        mAdapter = new MovieGridRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));

        mApi = MovieDbApi.getInstance(getString(R.string.tmdb_api_key));
        mApi.requestMostPopularMovies(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_poster_grid, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_popularity:
                if (!item.isChecked()) {
                    item.setChecked(false);
                    mApi.requestMostPopularMovies(this);
                }
                return true;
            case R.id.sort_rating:
                if (!item.isChecked()) {
                    item.setChecked(false);
                    mApi.requestHighestRatedMovies(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void success(MovieResponse response) {
        mAdapter.setData(response.getMovies());
    }


    @Override
    public void error(ApiError error) {
//                    Log.d(TAG, error.getReason());
    }


    class MovieGridItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_title)
        TextView movieTitle;

        @Bind(R.id.movie_poster)
        ImageView moviePoster;

        public MovieGridItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.movie_poster)
        public void onClick() {
            Log.d(TAG, "Show movie details ...");
        }
    }


    class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridItemViewHolder> {

        List<Movie> data = new ArrayList<>();

        public void setData(List<Movie> data) {
            this.data = data;
            this.notifyDataSetChanged();
        }

        @Override
        public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
            return new MovieGridItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieGridItemViewHolder holder, int position) {
            Movie movie = data.get(position);
            holder.movieTitle.setText(movie.getTitle());
            Log.d(TAG, "Rating: " + movie.getVoteAverage());
            Picasso.with(holder.moviePoster.getContext()).load(movie.getPosterUrl()).into(holder.moviePoster);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
