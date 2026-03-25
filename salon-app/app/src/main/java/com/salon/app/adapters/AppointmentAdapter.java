package com.salon.app.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.salon.app.R;
import com.salon.app.models.BookingResponse;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final List<BookingResponse> appointments;
    private final OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onCancelClick(BookingResponse appointment);
        void onRescheduleClick(BookingResponse appointment);
    }

    public AppointmentAdapter(List<BookingResponse> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingResponse appt = appointments.get(position);

        holder.tvService.setText(appt.getServiceName());
        holder.tvArtist.setText("Artist: " + appt.getArtistName());

        String startTime = appt.getStartTime();
        if (startTime != null && startTime.length() > 5) startTime = startTime.substring(0, 5);
        holder.tvDateTime.setText("📅 " + appt.getAppointmentDate() + "  ⏰ " + startTime);

        String paymentMethod = appt.getPaymentMethod() != null ? appt.getPaymentMethod() : "PAY_AT_SALON";
        String paymentLabel;
        switch (paymentMethod) {
            case "UPI":
                paymentLabel = "UPI";
                break;
            case "CARD":
                paymentLabel = "Card";
                break;
            default:
                paymentLabel = "Pay at Salon";
                break;
        }
        double price = appt.getPrice() != null ? appt.getPrice() : 0.0;
        holder.tvPrice.setText(String.format("₹%.0f • %s", price, paymentLabel));

        // Status badge color
        holder.tvStatus.setText(appt.getStatus());
        GradientDrawable bg = (GradientDrawable) holder.tvStatus.getBackground();
        int color;
        switch (appt.getStatus()) {
            case "BOOKED": color = holder.itemView.getContext().getColor(R.color.status_booked); break;
            case "COMPLETED": color = holder.itemView.getContext().getColor(R.color.status_completed); break;
            case "CANCELLED": color = holder.itemView.getContext().getColor(R.color.status_cancelled); break;
            default: color = holder.itemView.getContext().getColor(R.color.text_secondary);
        }
        bg.setColor(color);

        // Show cancel button only for BOOKED appointments
        if ("BOOKED".equals(appt.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(appt));
            holder.btnReschedule.setOnClickListener(v -> listener.onRescheduleClick(appt));
        } else {
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnReschedule.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return appointments.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvService, tvArtist, tvDateTime, tvPrice, tvStatus;
        MaterialButton btnCancel, btnReschedule;

        ViewHolder(View v) {
            super(v);
            tvService = v.findViewById(R.id.tvApptService);
            tvArtist = v.findViewById(R.id.tvApptArtist);
            tvDateTime = v.findViewById(R.id.tvApptDateTime);
            tvPrice = v.findViewById(R.id.tvApptPrice);
            tvStatus = v.findViewById(R.id.tvApptStatus);
            btnCancel = v.findViewById(R.id.btnCancel);
            btnReschedule = v.findViewById(R.id.btnReschedule);
        }
    }
}
