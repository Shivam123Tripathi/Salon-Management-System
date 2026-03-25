package com.salon.app.models;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String emailOtp;
    private String phoneOtp;

    public RegisterRequest(
            String fullName,
            String email,
            String password,
            String phone,
            String address,
            String emailOtp,
            String phoneOtp
    ) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.emailOtp = emailOtp;
        this.phoneOtp = phoneOtp;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getEmailOtp() { return emailOtp; }
    public String getPhoneOtp() { return phoneOtp; }
}
