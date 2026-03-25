package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("id") private Long id;
    @SerializedName("fullName") private String fullName;
    @SerializedName("email") private String email;
    @SerializedName("phone") private String phone;
    @SerializedName("address") private String address;
    @SerializedName("role") private String role;

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRole() { return role; }
}
