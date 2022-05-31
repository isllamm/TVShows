package com.isllam.tvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.isllam.tvshows.R;
import com.isllam.tvshows.databinding.ItemContainerEpisodeBinding;
import com.isllam.tvshows.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder> {

    private List<Episode> episodes;
    private LayoutInflater layoutInflater;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerEpisodeBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_episode, parent, false
        );
        return new EpisodesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bindEpisode(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class EpisodesViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerEpisodeBinding binding;

        public EpisodesViewHolder(ItemContainerEpisodeBinding itemContainerEpisodeBinding) {
            super(itemContainerEpisodeBinding.getRoot());
            this.binding = itemContainerEpisodeBinding;
        }

        public void bindEpisode(Episode episode) {
            String title = "S";
            String season = episode.getSeason();
            if (season.length() == 1) {
                season = "0".concat(season);
            }
            String episodeNumber = episode.getEpisode();
            if (episodeNumber.length() == 1) {
                episodeNumber = "0".concat(episodeNumber);
            }
            episodeNumber = "E".concat(episodeNumber);
            title = title.concat(season).concat(episodeNumber);
            binding.setTitle(title);
            binding.setName(episode.getName());
            binding.setAirDate(episode.getAirtDate());
        }
    }


}
