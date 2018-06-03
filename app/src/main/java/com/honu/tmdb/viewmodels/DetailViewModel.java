package com.honu.tmdb.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.honu.tmdb.data.MovieDatabase;
import com.honu.tmdb.data.MovieRepository;

import java.util.List;

/**
 *
 */
public class DetailViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private final LiveData<List<Integer>> genreIds;

    public DetailViewModel(MovieDatabase movieDatabase, int movieId) {
        mRepository = new MovieRepository(movieDatabase);
        genreIds = mRepository.getGenreIds(movieId);
    }

    public LiveData<List<Integer>> getGenreIds() {
        return genreIds;
    }
}
