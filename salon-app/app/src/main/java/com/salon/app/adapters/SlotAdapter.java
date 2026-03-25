package com.salon.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.salon.app.R;
import com.salon.app.models.SlotModel;
import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {

    private final List<SlotModel> slots;
    private int selectedPosition = -1;
    private final OnSlotClickListener listener;

    public interface OnSlotClickListener {
        void onSlotSelected(SlotModel slot);
    }

    public SlotAdapter(List<SlotModel> slots, OnSlotClickListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SlotModel slot = slots.get(position);
        // Format time from "HH:mm:ss" to "HH:mm"
        String startTime = slot.getStartTime();
        if (startTime != null && startTime.length() > 5) startTime = startTime.substring(0, 5);
        holder.tvTime.setText(startTime);

        if (slot.isAvailable()) {
            if (position == selectedPosition) {
                holder.itemView.setBackgroundResource(R.drawable.slot_chip_selected);
                holder.tvTime.setTextColor(holder.itemView.getContext().getColor(R.color.black));
            } else {
                holder.itemView.setBackgroundResource(R.drawable.slot_chip_available);
                holder.tvTime.setTextColor(holder.itemView.getContext().getColor(R.color.text_primary));
            }
            holder.itemView.setOnClickListener(v -> {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                int prev = selectedPosition;
                selectedPosition = currentPosition;
                if (prev != -1) notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                listener.onSlotSelected(slot);
            });
        } else {
            holder.itemView.setBackgroundResource(R.drawable.slot_chip_booked);
            holder.tvTime.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() { return slots.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        ViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tvSlotTime);
        }
    }
}
