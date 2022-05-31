package com.isllam.tvshows.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.isllam.tvshows.database.TVShowsDatabase;
import com.isllam.tvshows.models.TVShow;
import com.isllam.tvshows.repositories.TVShowDetailsRepository;
import com.isllam.tvshows.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel(@NonNull Application application) {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId) {
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow) {
        return tvShowsDatabase.tvShowDao().addToWatchList(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWatchlist(String tvShowId) {
        return tvShowsDatabase.tvShowDao().getTVShowFromWatchlist(tvShowId);
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow) {
        return tvShowsDatabase.tvShowDao().removeFromWatchList(tvShow);
    }
}
