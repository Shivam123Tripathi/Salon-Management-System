package com.salon.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardModel {
    @SerializedName("totalBookings") private Long totalBookings;
    @SerializedName("todayBookings") private Long todayBookings;
    @SerializedName("activeArtists") private Long activeArtists;
    @SerializedName("totalRevenue") private Double totalRevenue;
    @SerializedName("todayRevenue") private Double todayRevenue;
    @SerializedName("completedBookings") private Long completedBookings;
    @SerializedName("cancelledBookings") private Long cancelledBookings;
    @SerializedName("recentBookings") private List<BookingResponse> recentBookings;

    public Long getTotalBookings() { return totalBookings; }
    public Long getTodayBookings() { return todayBookings; }
    public Long getActiveArtists() { return activeArtists; }
    public Double getTotalRevenue() { return totalRevenue; }
    public Double getTodayRevenue() { return todayRevenue; }
    public Long getCompletedBookings() { return completedBookings; }
    public Long getCancelledBookings() { return cancelledBookings; }
    public List<BookingResponse> getRecentBookings() { return recentBookings; }
}
