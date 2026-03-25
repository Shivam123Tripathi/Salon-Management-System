package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class SlotModel {
    @SerializedName("startTime") private String startTime;
    @SerializedName("endTime") private String endTime;
    @SerializedName("status") private String status; // AVAILABLE, BOOKED, BLOCKED

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }

    public boolean isAvailable() { return "AVAILABLE".equals(status); }
}
