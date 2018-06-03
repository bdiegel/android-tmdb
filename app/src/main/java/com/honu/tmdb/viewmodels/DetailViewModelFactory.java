package com.honu.tmdb.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.honu.tmdb.data.MovieDatabase;

/**
 *
 */
public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieDatabase movieDatabase;
    private final int movieId;

    public DetailViewModelFactory(MovieDatabase movieDatabase, int movieId) {
        this.movieDatabase = movieDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(movieDatabase, movieId);
    }
}
