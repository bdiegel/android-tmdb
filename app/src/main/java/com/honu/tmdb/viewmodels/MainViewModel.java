package com.honu.tmdb.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.honu.tmdb.data.MovieDatabase;
import com.honu.tmdb.data.MovieRepository;
import com.honu.tmdb.rest.Movie;

import java.util.List;

/**
 *
 */
public class MainViewModel extends AndroidViewModel {

    MovieRepository mRepository;

    LiveData<List<Movie>> favorites;

    public MainViewModel(@NonNull Application application) {
        super(application);

        MovieDatabase movieDatabase = MovieDatabase.getInstance(application);
        mRepository = new MovieRepository(movieDatabase);
        favorites = mRepository.getMovies();
    }

    public LiveData<List<Movie>> getFavorites() {
        return favorites;
    }
}
