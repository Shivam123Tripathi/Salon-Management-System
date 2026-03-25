package com.salon.app.api;

import com.salon.app.models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * API SERVICE INTERFACE
 *
 * This is the Retrofit "contract" — it declares ALL the HTTP endpoints your app can call.
 * Retrofit reads these annotations at runtime and generates the actual HTTP code.
 *
 * HOW TO READ THESE:
 *   @POST("auth/login")  → sends POST request to http://base_url/auth/login
 *   @Body                → converts Java object to JSON and puts it in request body
 *   @Path("id")          → replaces {id} in the URL
 *   @Query("date")       → adds ?date=value to the URL
 *   Call<T>              → wraps the response. T = the response type (deserialized from JSON)
 */
public interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/register/admin")
    Call<ApiResponse<AuthResponse>> registerAdmin(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    // ==================== SERVICES (PUBLIC) ====================
    @GET("services")
    Call<ApiResponse<List<ServiceModel>>> getAllServices();

    @GET("services/{id}")
    Call<ApiResponse<ServiceModel>> getServiceById(@Path("id") Long id);

    @GET("services/category/{category}")
    Call<ApiResponse<List<ServiceModel>>> getServicesByCategory(@Path("category") String category);

    // ==================== ARTISTS (PUBLIC) ====================
    @GET("artists")
    Call<ApiResponse<List<ArtistModel>>> getAllArtists();

    @GET("artists/{id}")
    Call<ApiResponse<ArtistModel>> getArtistById(@Path("id") Long id);

    @GET("artists/{artistId}/slots")
    Call<ApiResponse<List<SlotModel>>> getAvailableSlots(
            @Path("artistId") Long artistId,
            @Query("serviceId") Long serviceId,
            @Query("date") String date);

    // ==================== CUSTOMER PROFILE ====================
    @GET("customer/profile")
    Call<ApiResponse<UserProfile>> getProfile();

    @PUT("customer/profile")
    Call<ApiResponse<UserProfile>> updateProfile(@Body UserProfile profile);

    // ==================== APPOINTMENTS ====================
    @POST("appointments")
    Call<ApiResponse<BookingResponse>> bookAppointment(@Body BookingRequest request);

    @GET("appointments")
    Call<ApiResponse<List<BookingResponse>>> getMyAppointments();

    @GET("appointments/upcoming")
    Call<ApiResponse<List<BookingResponse>>> getUpcomingAppointments();

    @GET("appointments/past")
    Call<ApiResponse<List<BookingResponse>>> getPastAppointments();

    @PUT("appointments/{id}/cancel")
    Call<ApiResponse<BookingResponse>> cancelAppointment(@Path("id") Long id);

    @PUT("appointments/{id}/reschedule")
    Call<ApiResponse<BookingResponse>> rescheduleAppointment(
            @Path("id") Long id,
            @Body BookingRequest request);

    // ==================== REVIEWS ====================
    @POST("reviews")
    Call<ApiResponse<Object>> submitReview(@Body ReviewRequest request);

    @PUT("payments/appointment/{appointmentId}/complete")
    Call<ApiResponse<PaymentResponse>> completePayment(
            @Path("appointmentId") Long appointmentId,
            @Query("transactionId") String transactionId);

    // ==================== ADMIN ====================
    @GET("admin/dashboard")
    Call<ApiResponse<DashboardModel>> getDashboard();

    @GET("admin/appointments")
    Call<ApiResponse<List<BookingResponse>>> getAllAppointments();

    // Admin - Services
    @POST("admin/services")
    Call<ApiResponse<ServiceModel>> addService(@Body ServiceModel service);

    @PUT("admin/services/{id}")
    Call<ApiResponse<ServiceModel>> updateService(@Path("id") Long id, @Body ServiceModel service);

    @DELETE("admin/services/{id}")
    Call<ApiResponse<Void>> deleteService(@Path("id") Long id);

    @GET("admin/services")
    Call<ApiResponse<List<ServiceModel>>> getAllServicesAdmin();

    // Admin - Artists
    @POST("admin/artists")
    Call<ApiResponse<ArtistModel>> addArtist(@Body ArtistModel artist);

    @PUT("admin/artists/{id}")
    Call<ApiResponse<ArtistModel>> updateArtist(@Path("id") Long id, @Body ArtistModel artist);

    @DELETE("admin/artists/{id}")
    Call<ApiResponse<Void>> deleteArtist(@Path("id") Long id);

    @POST("admin/artists/{artistId}/services/{serviceId}")
    Call<ApiResponse<Void>> assignServiceToArtist(
            @Path("artistId") Long artistId, @Path("serviceId") Long serviceId);
}
