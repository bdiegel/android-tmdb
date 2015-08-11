package com.honu.tmdb;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    static final String KEY_MOVIES = "movies";

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;

    Spinner mSortSpinner;

    MovieDbApi mApi;
    MovieGridRecyclerAdapter mAdapter;

    int mSortMethod = SortOption.POPULARITY;

    public MoviePosterGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movie_poster_grid, null);
        ButterKnife.bind(this, view);

        ArrayList<Movie> movies = new ArrayList<>();

        // restore movie list from instance state on orientation change
        if (savedInstanceState != null) {
            mSortMethod = AppPreferences.getCurrentSortMethod(getActivity());
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
        } else {
            mSortMethod = AppPreferences.getPreferredSortMethod(getActivity());
        }

        mAdapter = new MovieGridRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));

        // initialize api
        mApi = MovieDbApi.getInstance(getString(R.string.tmdb_api_key));

        // request movies
        if (mAdapter.getItemCount() == 0) {
            if (mSortMethod == SortOption.POPULARITY) {
                mApi.requestMostPopularMovies(this);
            } else {
                mApi.requestHighestRatedMovies(this);
            }
        }

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MOVIES, mAdapter.data);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_poster_grid, menu);

        MenuItem menuItem = menu.findItem(R.id.spin_test);

        // specify layout for the action
        menuItem.setActionView(R.layout.sort_spinner);
        View view = menuItem.getActionView();

        // set custom adapter on spinner
        mSortSpinner = (Spinner) view.findViewById(R.id.spinner_nav);
        mSortSpinner.setAdapter(new SortSpinnerAdapter(this, getActivity(), SortOption.getSortOptions()));
        mSortSpinner.setSelection(mSortMethod);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppPreferences.setCurrentSortMethod(getActivity(), position);
                handleSortSelection(SortOption.getSortMethod(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Options menu item selected: " + item.getItemId());
        return true;
    }


    @Override
    public void success(MovieResponse response) {
        mAdapter.setData(response.getMovies());
    }


    @Override
    public void error(ApiError error) {
        Log.d(TAG, error.getReason());
    }

    public void handleSortSelection(int sortType) {
        if (mSortMethod == sortType)
            return;

        mSortMethod = sortType;

        switch (sortType) {
            case SortOption.POPULARITY:
                mApi.requestMostPopularMovies(this);
                return;
            case SortOption.RATING:
                mApi.requestHighestRatedMovies(this);
                return;
            default:
                // TODO: P2 - add support for Favorites
                Toast.makeText(getActivity(), "Sort type not supported", Toast.LENGTH_SHORT).show();
                return;
        }
    }


    class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridRecyclerAdapter.MovieGridItemViewHolder> {

        ArrayList<Movie> data = new ArrayList<>();

        public void setData(List<Movie> data) {
            this.data.clear();
            this.data.addAll(data);
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
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            Picasso.with(holder.moviePoster.getContext()).load(movie.getPosterUrl(screenWidth)).into(holder.moviePoster);
        }

        @Override
        public int getItemCount() {
            return data.size();
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
                int adapterPosition = this.getAdapterPosition();
                Log.d(TAG, "AdapterPosition: " + adapterPosition);
                Movie movie = data.get(adapterPosition);
                openDetails(movie);
            }

            private void openDetails(Movie movie) {
                Log.d(TAG, "Show movie details: " + movie.getTitle());
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("movie", movie);
                getActivity().startActivity(intent);
            }
        }
    }

}
