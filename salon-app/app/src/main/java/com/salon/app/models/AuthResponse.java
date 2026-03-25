package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token") private String token;
    @SerializedName("userId") private Long userId;
    @SerializedName("fullName") private String fullName;
    @SerializedName("email") private String email;
    @SerializedName("role") private String role;

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
