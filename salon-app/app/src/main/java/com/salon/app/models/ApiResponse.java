package com.salon.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * GENERIC API RESPONSE WRAPPER
 *
 * Every response from our backend is wrapped in this format:
 * { "success": true, "message": "...", "data": { ... } }
 *
 * The <T> generic type means "data" can be ANY type:
 * ApiResponse<AuthResponse>, ApiResponse<List<ServiceModel>>, etc.
 *
 * @SerializedName tells Gson which JSON key maps to which Java field.
 */
public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
