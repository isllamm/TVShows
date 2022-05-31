package com.isllam.tvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.isllam.tvshows.R;
import com.isllam.tvshows.adapters.WatchlistAdapter;
import com.isllam.tvshows.databinding.ActivityWatchlistBinding;
import com.isllam.tvshows.listeners.WatchlistListener;
import com.isllam.tvshows.models.TVShow;
import com.isllam.tvshows.utilities.TempDataHolder;
import com.isllam.tvshows.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchlistBinding watchlistBinding;
    private WatchlistViewModel watchlistViewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        watchlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_watchlist);
        Init();
        loadWatchlist();
    }

    private void Init() {
        watchlistViewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        watchlistBinding.imageBack.setOnClickListener(v -> onBackPressed());
        watchlist = new ArrayList<>();
    }


    private void loadWatchlist() {
        watchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchlistViewModel.loadWatchlist().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        tvShows -> {
                            watchlistBinding.setIsLoading(false);
                            if (watchlist.size() > 0) {
                                watchlist.clear();
                            }
                            watchlist.addAll(tvShows);
                            watchlistAdapter = new WatchlistAdapter(watchlist, this);
                            watchlistBinding.watchlistRecyclerview.setAdapter(watchlistAdapter);
                            watchlistBinding.watchlistRecyclerview.setVisibility(View.VISIBLE);
                            compositeDisposable.dispose();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }

    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);

    }

    @Override
    public void removeTVShowFromWatchlist(TVShow show, int postion) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchlistViewModel.removeTVShowFromWatchlist(show)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchlist.remove(postion);
                    watchlistAdapter.notifyItemRemoved(postion);
                    watchlistAdapter.notifyItemRangeChanged(postion, watchlistAdapter.getItemCount());
                    compositeDisposable.dispose();
                }));
    }
}