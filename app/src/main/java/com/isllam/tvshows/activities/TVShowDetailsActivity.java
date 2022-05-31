package com.isllam.tvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.isllam.tvshows.R;
import com.isllam.tvshows.adapters.EpisodesAdapter;
import com.isllam.tvshows.adapters.ImageSliderAdapter;
import com.isllam.tvshows.databinding.ActivityTvshowDetailsBinding;
import com.isllam.tvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.isllam.tvshows.models.TVShow;
import com.isllam.tvshows.utilities.TempDataHolder;
import com.isllam.tvshows.viewmodels.TVShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {
    private ActivityTvshowDetailsBinding binding;
    private TVShowDetailsViewModel viewModel;
    private BottomSheetDialog bottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding sheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowInWatchlist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        Init();
    }

    private void Init() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        viewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTVShowDetails();
        checkTVShow();
    }


    private void checkTVShow() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTVShowInWatchlist = true;
                    binding.imageWatchList.setImageResource(R.drawable.ic_check);
                    compositeDisposable.dispose();
                }));
    }

    private void getTVShowDetails() {
        binding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        viewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            binding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                binding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                binding.imageTVShow.setVisibility(View.VISIBLE);
                binding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(
                                        tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                        )
                );
                binding.textDescription.setVisibility(View.VISIBLE);
                binding.textReadMore.setVisibility(View.VISIBLE);
                binding.textReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.textReadMore.getText().toString().equals("Read More")) {
                            binding.textDescription.setMaxLines(Integer.MAX_VALUE);
                            binding.textDescription.setEllipsize(null);
                            binding.textReadMore.setText(R.string.read_less);
                        } else {
                            binding.textDescription.setMaxLines(4);
                            binding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                            binding.textReadMore.setText(R.string.read_more);
                        }
                    }
                });
                binding.setRating(
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                        )
                );
                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    binding.setGenre("N/A");
                }
                binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                binding.viewDivider1.setVisibility(View.VISIBLE);
                binding.viewDivider2.setVisibility(View.VISIBLE);
                binding.layoutMisc.setVisibility(View.VISIBLE);
                binding.buttonWebsite.setVisibility(View.VISIBLE);
                binding.buttonEpisodes.setVisibility(View.VISIBLE);
                binding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                        startActivity(intent);
                    }
                });
                binding.buttonEpisodes.setOnClickListener(v -> {
                    if (bottomSheetDialog == null) {
                        bottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        sheetBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer),
                                false
                        );
                        bottomSheetDialog.setContentView(sheetBinding.getRoot());
                        sheetBinding.episodesRecyclerView.setAdapter(
                                new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                        );
                        sheetBinding.textTitle.setText(
                                String.format("Episodes | %s", tvShow.getName())
                        );
                        sheetBinding.imageClose.setOnClickListener(c -> {
                            bottomSheetDialog.dismiss();
                        });
                    }
                    FrameLayout frameLayout = bottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    bottomSheetDialog.show();
                });

                binding.imageWatchList.setOnClickListener(a ->
                {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowInWatchlist) {
                        compositeDisposable.add(
                                viewModel.removeTVShowFromWatchlist(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVShowInWatchlist = false;
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            binding.imageWatchList.setImageResource(R.drawable.ic_eye);
                                            Toast.makeText(getApplicationContext(), "Removed from Watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                        );

                    } else {
                        compositeDisposable.add(
                                viewModel.addToWatchList(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            binding.imageWatchList.setImageResource(R.drawable.ic_check);
                                            Toast.makeText(getApplicationContext(), "Added To Watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                        );
                    }

                });
                binding.imageWatchList.setVisibility(View.VISIBLE);
                getBasicTVShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        binding.sliderViewPager.setOffscreenPageLimit(1);
        binding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        binding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            binding.layoutSilderIndecators.addView(indicators[i]);
        }
        binding.layoutSilderIndecators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int pos) {
        int childCount = binding.layoutSilderIndecators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutSilderIndecators.getChildAt(i);
            if (i == pos) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void getBasicTVShowDetails() {
        binding.setTvShowName(tvShow.getName());
        binding.setNetworkCountry(tvShow.getNetwork() + " (" + tvShow.getCountry() + ")");
        binding.setStatus(tvShow.getStatus());
        binding.setStartedDate(tvShow.getStartDate());
        binding.textName.setVisibility(View.VISIBLE);
        binding.textNetworkCountry.setVisibility(View.VISIBLE);
        binding.textStarted.setVisibility(View.VISIBLE);
        binding.textStatus.setVisibility(View.VISIBLE);
    }
}