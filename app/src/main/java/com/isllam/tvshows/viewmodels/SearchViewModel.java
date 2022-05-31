package com.isllam.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.isllam.tvshows.repositories.SearchTVShowRepository;
import com.isllam.tvshows.responses.TVShowsResponse;

public class SearchViewModel extends ViewModel {
    private SearchTVShowRepository searchTVShowRepository;

    public SearchViewModel() {
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowsResponse> searchTVSow(String query, int page) {
        return searchTVShowRepository.searchTVShow(query, page);
    }
}
