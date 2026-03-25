package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("artistId") private Long artistId;
    @SerializedName("serviceId") private Long serviceId;
    @SerializedName("appointmentDate") private String appointmentDate;
    @SerializedName("startTime") private String startTime;
    @SerializedName("paymentMethod") private String paymentMethod;
    @SerializedName("notes") private String notes;

    public BookingRequest(Long artistId, Long serviceId, String appointmentDate,
                          String startTime, String paymentMethod, String notes) {
        this.artistId = artistId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }
}
