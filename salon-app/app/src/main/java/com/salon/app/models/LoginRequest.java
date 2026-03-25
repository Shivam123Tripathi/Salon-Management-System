package com.salon.app.models;

public class LoginRequest {
    private String email;
    private String password;
    private String emailOtp;
    private String phoneOtp;

    public LoginRequest(String email, String password, String emailOtp, String phoneOtp) {
        this.email = email;
        this.password = password;
        this.emailOtp = emailOtp;
        this.phoneOtp = phoneOtp;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getEmailOtp() { return emailOtp; }
    public String getPhoneOtp() { return phoneOtp; }
}
