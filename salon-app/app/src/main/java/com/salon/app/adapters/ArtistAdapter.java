package com.salon.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.salon.app.R;
import com.salon.app.models.ArtistModel;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private final List<ArtistModel> artists;
    private final OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(ArtistModel artist);
    }

    public ArtistAdapter(List<ArtistModel> artists, OnArtistClickListener listener) {
        this.artists = artists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtistModel artist = artists.get(position);
        String artistName = artist.getFullName() != null && !artist.getFullName().trim().isEmpty()
                ? artist.getFullName()
                : "Artist";
        String specialization = artist.getSpecialization() != null && !artist.getSpecialization().trim().isEmpty()
                ? artist.getSpecialization()
                : "Stylist";
        holder.tvName.setText(artistName);
        holder.tvSpecialization.setText(specialization);
        holder.tvRating.setText(String.format("★ %.1f (%d reviews)",
                artist.getRating() != null ? artist.getRating() : 0.0,
                artist.getTotalReviews() != null ? artist.getTotalReviews() : 0));
        holder.tvExperience.setText(String.format("%d yrs exp",
                artist.getExperienceYears() != null ? artist.getExperienceYears() : 0));

        if (artist.getServiceNames() != null && !artist.getServiceNames().isEmpty()) {
            holder.tvServices.setText("Services: " + String.join(", ", artist.getServiceNames()));
            holder.tvServices.setVisibility(View.VISIBLE);
        } else {
            holder.tvServices.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() { return artists.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialization, tvRating, tvExperience, tvServices;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvArtistName);
            tvSpecialization = v.findViewById(R.id.tvSpecialization);
            tvRating = v.findViewById(R.id.tvRating);
            tvExperience = v.findViewById(R.id.tvExperience);
            tvServices = v.findViewById(R.id.tvServices);
        }
    }
}
