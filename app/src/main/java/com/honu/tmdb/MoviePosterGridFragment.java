package com.honu.tmdb;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.honu.tmdb.rest.MovieListResponse;
import com.honu.tmdb.rest.MovieResponse;
import com.honu.tmdb.viewmodels.MainViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment displays grid of movie posters using recycler view
 */
public class MoviePosterGridFragment extends Fragment {

    static final String TAG = MoviePosterGridFragment.class.getSimpleName();

    static final String KEY_MOVIES = "movies";

    @BindView(R.id.grid_recycler_container)
    RecyclerView mRecyclerView;

    Spinner mSortSpinner;

    MovieDbApi mApi;
    MovieGridRecyclerAdapter mAdapter;
    RecyclerScrollListener mScrollListener;

    int mSortMethod = SortOption.POPULARITY;

    // number of pages available for scrolling
    int mPageMax = 25;

    // number of items per page
    int mPageSize = 20;

    // communicates selection events back to listener
    OnMovieSelectedListener mListener;

    MovieDbApi.MovieListener<MovieListResponse> movieListListener = new MovieDbApi.MovieListener<MovieListResponse>() {

        @Override
        public void success(MovieListResponse response) {
            onSuccess(response);
        }

        @Override
        public void error(ApiError error) {
            onError(error);
        }
    };

    MovieDbApi.MovieListener<MovieResponse> movieListener = new MovieDbApi.MovieListener<MovieResponse>() {

        @Override
        public void success(MovieResponse response) {
            onSuccess(response);
        }

        @Override
        public void error(ApiError error) {
            onError(error);
        }
    };


    // interface to communicate movie selection events to MainActivity
    public interface OnMovieSelectedListener {
        //public void onMovieSelected(Movie selection, boolean onClick);
        public void onMovieSelected(Movie selection, boolean onClick, View view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movie_poster_grid, container, false);
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
        mScrollListener = new RecyclerScrollListener();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 3));
        mRecyclerView.addOnScrollListener(mScrollListener);

        // initialize api
        mApi = MovieDbApi.getInstance(getString(R.string.tmdb_api_key));

        // request movies
        if (mAdapter.getItemCount() == 0) {
            if (mSortMethod == SortOption.POPULARITY) {
                mApi.requestMostPopularMovies(movieListListener);
            } else {
                mApi.requestHighestRatedMovies(movieListListener);
            }
        }

        // check for network connection
        checkNetwork();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIES, mAdapter.data);
        super.onSaveInstanceState(outState);
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
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }


    private void onSuccess(MovieListResponse response) {

        if (response.getPage() == 1) {
            // initialize with results for first page
            int pageMax = (response.getTotalPages() < mPageMax) ? response.getTotalPages() : mPageMax;
            mScrollListener.totalPages = pageMax;
            mAdapter.setData(response.getMovies(), mPageSize, pageMax);
        } else {
            // append results for subsequent pages
            mAdapter.appendData(response.getMovies());
        }
    }

    private void onSuccess(MovieResponse response) {
        mAdapter.appendData(response.getMovie());
    }

    private void onError(ApiError error) {
        if (error.isNetworkError()) {
            Toast.makeText(getActivity(), "Unable to connect to remote host", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, error.toString());
    }

    public void handleSortSelection(int sortType) {
        if (mSortMethod == sortType)
            return;

        mSortMethod = sortType;
        mScrollListener.init();
        mRecyclerView.scrollToPosition(0);

        switch (mSortMethod) {
            case SortOption.POPULARITY:
                mApi.requestMostPopularMovies(movieListListener);
                return;
            case SortOption.RATING:
                mApi.requestHighestRatedMovies(movieListListener);
                return;
            case SortOption.FAVORITE:
                queryFavorites();
                return;
            default:
                Toast.makeText(getActivity(), "Sort type not supported", Toast.LENGTH_SHORT).show();
                return;
        }
    }

    public void removeMovie(Movie movie) {
        mAdapter.removeData(movie);
    }

    private void queryFavorites() {

        mListener.onMovieSelected(null, false, null);

        if (isNetworkAvailable()) {
            Log.d(TAG, "Query favorites (online mode)");
            mAdapter.clearData();
            List<Integer> favoriteIds = MovieFavorites.getFavoriteMovies(getActivity());
            for (int favoriteId : favoriteIds) {
                mApi.requestMovie(favoriteId, movieListener);
            }
        } else {
            Log.d(TAG, "Query favorites (offline mode)");
            mAdapter.clearData();

            // query  viewmodel for favorites
            MainViewModel mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
            mainViewModel.getFavorites().observe(MoviePosterGridFragment.this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable final List<Movie> movies) {
                    mAdapter.setData(movies);
                }
            });

        }
    }

    private void loadNextPage(int page) {
        //Log.d(TAG, "Load page: " + page);

        switch (mSortMethod) {
            case SortOption.POPULARITY:
                mApi.requestMostPopularMovies(page, movieListListener);
                return;
            case SortOption.RATING:
                mApi.requestHighestRatedMovies(page, movieListListener);
                return;
            default:
                return;
        }

    }

    private boolean checkNetwork() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), "Network unavailable (check your connection)", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    
    class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridRecyclerAdapter.MovieGridItemViewHolder> {

        ArrayList<Movie> data = new ArrayList<>();

        int maxPages = -1;
        int pageSize = 20;

        public void setData(List<Movie> data, int pageSize, int maxPages) {
            this.maxPages = maxPages;
            this.pageSize = pageSize;
            setData(data);
        }

        public void setData(List<Movie> data) {
            this.data.clear();
            if (data !=null && data.size() > 0) {
                this.data.addAll(data);
            }
            this.notifyDataSetChanged();
            notifyMovieSelectionListener();
        }

//        // ran into bug that forced switching from notifyItemRangeInserted to notifyDataSetChanged
//        public void appendData(List<Movie> movies) {
//            int previousSize = this.data.size();
//            this.data.addAll(movies);
//            this.notifyItemRangeInserted(previousSize, previousSize + movies.size());
//        }

        public void appendData(List<Movie> movies) {
            this.data.addAll(movies);
            this.notifyDataSetChanged();
        }

        public void appendData(Movie movie) {
            this.data.add(movie);
            this.notifyItemChanged(this.data.size() - 1);
            if (this.data.size() == 1) {
                notifyMovieSelectionListener();
            }
        }

        public void removeData(Movie movie) {
            int index = this.data.indexOf(movie);
            if (index != -1)
                this.data.remove(index);

            this.notifyItemRemoved(index);
            notifyMovieSelectionListener();
        }

        public void clearData() {
            this.maxPages = -1;
            this.data.clear();
            this.notifyDataSetChanged();
        }

        public void notifyMovieSelectionListener() {
            if (mListener != null && !data.isEmpty()) {
                View view = mRecyclerView.getChildAt(0);
                Log.d(TAG, "Found child view: " + view);
                View posterView = null;
                if (view != null) {
                    posterView = view.findViewById(R.id.movie_poster);
                    Log.d(TAG, "Found poster view: " + posterView);
                }
               mListener.onMovieSelected(data.get(0), false, posterView);
                //mRecyclerView.setSelected();
            }
        }

        @Override
        public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
            return new MovieGridItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieGridItemViewHolder holder, int position) {

            // pending results
            if (position >= data.size()) {
                holder.movieTitle.setText("");
                holder.moviePoster.setImageResource(R.drawable.ic_image_white_36dp);
                return;
            }

            // display movie details
            Movie movie = data.get(position);
            holder.movieTitle.setText(movie.getTitle());
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            //Picasso.with(getActivity().getApplicationContext()).setIndicatorsEnabled(true);
            Picasso.get()
                  .load(movie.getPosterUrl(screenWidth))
                  .placeholder(R.drawable.ic_local_movies_white_36dp)
                  .error(R.drawable.ic_local_movies_white_36dp)
                  .into(holder.moviePoster);
        }

        @Override
        public int getItemCount() {
            // returns the expected size when paging is enabled; this prevents an 'invalid view holder position'
            // exception thrown by validateViewHolderForOffsetPosition
            if (maxPages == -1) {
                return  data.size();
            } else {
                return maxPages * pageSize;
            }
        }

        class MovieGridItemViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.movie_title)
            TextView movieTitle;

            @BindView(R.id.movie_poster)
            ImageView moviePoster;

            public MovieGridItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.movie_poster)
            public void onClick() {
                int adapterPosition = this.getAdapterPosition();
                if (adapterPosition < data.size()) {
                    Movie movie = data.get(adapterPosition);
                    if (mListener != null) {
                        mListener.onMovieSelected(movie, true, moviePoster);
                    }
                }
            }
        }
    }

    class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        int currentPage;
        int totalPages;
        int previousTotal;
        int visibleThreshold;
        boolean loading;

        public void init() {
            currentPage = 1;
            totalPages = 1;
            previousTotal = 0;
            visibleThreshold = 5;
            loading = false;
        }

        public RecyclerScrollListener() {
            super();
            init();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            //int totalItemCount = gridLayoutManager.getItemCount();
            int totalItemCount = mAdapter.data.size();
            int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

            // load finished
            if (loading && totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }

            // load more data when near end of scroll view (within threshold)
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if (currentPage < totalPages) {
                    loadNextPage(currentPage + 1);
                    loading = true;
                }
            }
        }
    }
}
