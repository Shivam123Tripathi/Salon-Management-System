package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("id") private Long id;
    @SerializedName("appointmentId") private Long appointmentId;
    @SerializedName("amount") private Double amount;
    @SerializedName("method") private String method;
    @SerializedName("status") private String status;
    @SerializedName("transactionId") private String transactionId;

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public Double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
}
