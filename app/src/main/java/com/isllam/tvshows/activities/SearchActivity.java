package com.isllam.tvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.isllam.tvshows.R;
import com.isllam.tvshows.adapters.TVShowsAdapter;
import com.isllam.tvshows.databinding.ActivitySearchBinding;
import com.isllam.tvshows.listeners.TVShowListener;
import com.isllam.tvshows.models.TVShow;
import com.isllam.tvshows.viewmodels.SearchViewModel;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {

    private ActivitySearchBinding searchBinding;
    private SearchViewModel searchViewModel;
    private final List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        Init();
    }

    private void Init() {
        searchBinding.imageBack.setOnClickListener(b -> onBackPressed());
        searchBinding.tvShowsRecyclerview.setHasFixedSize(true);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        searchBinding.tvShowsRecyclerview.setAdapter(tvShowsAdapter);

        searchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    currentPage = 1;
                                    totalAvailablePages = 1;
                                    searchTVShow(editable.toString());
                                }
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });

        searchBinding.tvShowsRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!searchBinding.tvShowsRecyclerview.canScrollVertically(1)) {
                    if (!searchBinding.inputSearch.getText().toString().isEmpty()) {
                        if (currentPage < totalAvailablePages) {
                            currentPage += 1;
                            searchTVShow(searchBinding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });

        searchBinding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();
        searchViewModel.searchTVSow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePages = tvShowsResponse.getTotalPages();
                if (tvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(tvShowsResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                    Toast.makeText(this, "ss "+tvShows.get(0).getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (searchBinding.getIsLoading() != null && searchBinding.getIsLoading()) {
                searchBinding.setIsLoading(false);
            } else {
                searchBinding.setIsLoading(true);
            }
        } else {
            if (searchBinding.getIsLoadingMore() != null && searchBinding.getIsLoadingMore()) {
                searchBinding.setIsLoadingMore(false);
            } else {
                searchBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}