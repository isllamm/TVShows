package com.isllam.tvshows.listeners;

import com.isllam.tvshows.models.TVShow;

public interface WatchlistListener {
    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchlist(TVShow show, int postion);
}
