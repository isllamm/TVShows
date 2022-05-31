package com.isllam.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.isllam.tvshows.repositories.MostPopularTVShowsRepository;
import com.isllam.tvshows.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {
    private MostPopularTVShowsRepository mostPopularTVShowsRepository;

    public MostPopularTVShowsViewModel() {
        mostPopularTVShowsRepository = new MostPopularTVShowsRepository();
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page) {
        return mostPopularTVShowsRepository.getMostPopularTVShows(page);
    }
}
