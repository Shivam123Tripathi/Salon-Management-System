package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    @SerializedName("id") private Long id;
    @SerializedName("customerName") private String customerName;
    @SerializedName("artistId") private Long artistId;
    @SerializedName("artistName") private String artistName;
    @SerializedName("serviceId") private Long serviceId;
    @SerializedName("serviceName") private String serviceName;
    @SerializedName("appointmentDate") private String appointmentDate;
    @SerializedName("startTime") private String startTime;
    @SerializedName("endTime") private String endTime;
    @SerializedName("status") private String status;
    @SerializedName("notes") private String notes;
    @SerializedName("price") private Double price;
    @SerializedName("paymentStatus") private String paymentStatus;
    @SerializedName("paymentMethod") private String paymentMethod;

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public Long getArtistId() { return artistId; }
    public String getArtistName() { return artistName; }
    public Long getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public Double getPrice() { return price; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
}
