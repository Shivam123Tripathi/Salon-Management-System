package com.salon.app.api;

import android.content.Context;
import com.salon.app.utils.TokenManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API CLIENT — Retrofit Singleton
 *
 * This creates a SINGLE shared instance of Retrofit that the whole app uses.
 *
 * KEY CONCEPT: INTERCEPTORS
 * An interceptor is a "middleware" for HTTP requests. Before any request goes out:
 * 1. The Auth Interceptor reads the JWT token from TokenManager
 * 2. Adds it as: "Authorization: Bearer <token>" header
 * 3. The request goes out with authentication built-in
 *
 * BASE_URL: 10.0.2.2 is Android Emulator's alias for your PC's localhost.
 * When running on a REAL phone, change this to your PC's local IP (e.g., 192.168.1.5).
 */
public class ApiClient {

    private static final String[] BASE_URLS = {
            "http://10.0.2.2:8080/api/",
            "http://10.0.3.2:8080/api/",
            "http://10.0.2.2:8000/api/",
            "http://127.0.0.1:8080/api/"
    };
    private static int activeBaseUrlIndex = 0;

    private static ApiService apiService;

    /**
     * Get the shared API service instance.
     * Uses the Singleton pattern — creates it once, reuses everywhere.
     */
    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            TokenManager tokenManager = new TokenManager(context);

            // Logging Interceptor: Shows HTTP traffic in Logcat (Android's console)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Build OkHttp client with auth and logging interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    // AUTH INTERCEPTOR: Adds JWT token to every request automatically
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        // If we have a token, add it to the Authorization header
                        String token = tokenManager.getToken();
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                        builder.header("Content-Type", "application/json");

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Build Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URLS[activeBaseUrlIndex])
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON ↔ Java conversion
                    .build();

            // Create the API service from the interface
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static synchronized boolean tryFallbackToAlternateBaseUrl(Throwable throwable) {
        String message = throwable != null && throwable.getMessage() != null
                ? throwable.getMessage().toLowerCase()
                : "";
        boolean isConnectionIssue = throwable instanceof java.io.IOException
                || message.contains("failed to connect")
                || message.contains("connect")
                || message.contains("timeout")
                || message.contains("unable to resolve host");
        if (!isConnectionIssue) {
            return false;
        }
        if (activeBaseUrlIndex < BASE_URLS.length - 1) {
            activeBaseUrlIndex++;
            apiService = null;
            return true;
        }
        return false;
    }

    public static synchronized void resetToPrimaryBaseUrl() {
        activeBaseUrlIndex = 0;
        apiService = null;
    }

    public static synchronized String getActiveBaseUrl() {
        return BASE_URLS[activeBaseUrlIndex];
    }

    /** Reset the client (call after logout to clear cached token) */
    public static synchronized void reset() {
        activeBaseUrlIndex = 0;
        apiService = null;
    }
}
