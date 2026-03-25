package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("appointmentId") private Long appointmentId;
    @SerializedName("rating") private Integer rating;
    @SerializedName("comment") private String comment;

    public ReviewRequest(Long appointmentId, Integer rating, String comment) {
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
    }
}
