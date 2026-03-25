package com.salon.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * TOKEN MANAGER — Stores JWT token and user info securely.
 *
 * SharedPreferences is Android's simple key-value storage system.
 * Think of it as a tiny local database for small data (settings, tokens).
 *
 * HOW IT WORKS:
 * 1. User logs in → backend returns JWT token
 * 2. TokenManager saves it to SharedPreferences
 * 3. Every API call reads the token from here
 * 4. User logs out → TokenManager clears everything
 *
 * WHY NOT A DATABASE?
 * SharedPreferences is faster and simpler for small data like a single token.
 * It's stored in an XML file in the app's private directory (other apps can't read it).
 */
public class TokenManager {

    private static final String PREF_NAME = "salon_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        // MODE_PRIVATE: Only THIS app can access this file
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Save login data after successful authentication */
    public void saveAuthData(String token, Long userId, String fullName, String email, String role) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_FULL_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role)
                .apply(); // apply() is async (faster than commit())
    }

    /** Get the stored JWT token */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /** Check if user is currently logged in */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    /** Check if logged-in user is an admin */
    public boolean isAdmin() {
        return "ADMIN".equals(prefs.getString(KEY_ROLE, ""));
    }

    public String getFullName() { return prefs.getString(KEY_FULL_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getRole() { return prefs.getString(KEY_ROLE, ""); }
    public Long getUserId() { return prefs.getLong(KEY_USER_ID, -1); }

    /** Clear all stored data (logout) */
    public void clear() {
        prefs.edit().clear().apply();
    }
}
