package com.salon.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.salon.app.R;
import com.salon.app.models.ServiceModel;
import java.util.List;

/**
 * SERVICE ADAPTER — Connects data to RecyclerView.
 *
 * RecyclerView shows a scrollable list. The Adapter:
 * 1. Creates a ViewHolder for each visible item (onCreateViewHolder)
 * 2. Fills it with data (onBindViewHolder)
 * 3. RECYCLES views as you scroll (that's why it's called RecyclerView!)
 *
 * The OnServiceClickListener interface lets the Activity handle button clicks.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private final List<ServiceModel> services;
    private final OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onBookClick(ServiceModel service);
    }

    public ServiceAdapter(List<ServiceModel> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceModel service = services.get(position);
        holder.tvName.setText(service.getName());
        holder.tvPrice.setText(String.format("₹%.0f", service.getPrice()));
        holder.tvDescription.setText(service.getDescription());
        holder.tvDuration.setText(String.format("⏱ %d min", service.getDurationMinutes()));
        holder.tvCategory.setText(service.getCategory());
        holder.itemView.setOnClickListener(v -> listener.onBookClick(service));
        holder.btnBook.setOnClickListener(v -> listener.onBookClick(service));
    }

    @Override
    public int getItemCount() { return services.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDescription, tvDuration, tvCategory;
        MaterialButton btnBook;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            tvDescription = itemView.findViewById(R.id.tvServiceDescription);
            tvDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvCategory = itemView.findViewById(R.id.tvServiceCategory);
            btnBook = itemView.findViewById(R.id.btnBookService);
        }
    }
}
