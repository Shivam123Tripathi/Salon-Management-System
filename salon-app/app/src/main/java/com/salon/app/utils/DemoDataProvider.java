package com.salon.app.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salon.app.models.ArtistModel;
import com.salon.app.models.BookingResponse;
import com.salon.app.models.ServiceModel;
import com.salon.app.models.SlotModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DemoDataProvider {

    private static final Gson GSON = new Gson();
    private static final Type SERVICE_LIST_TYPE = new TypeToken<List<ServiceModel>>() { }.getType();
    private static final Type ARTIST_LIST_TYPE = new TypeToken<List<ArtistModel>>() { }.getType();
    private static final Type SLOT_LIST_TYPE = new TypeToken<List<SlotModel>>() { }.getType();
    private static final Type BOOKING_LIST_TYPE = new TypeToken<List<BookingResponse>>() { }.getType();

    private static final List<BookingResponse> DEMO_APPOINTMENTS = new ArrayList<>();
    private static long nextDemoBookingId = 900001L;

    private DemoDataProvider() { }

    public static List<ServiceModel> getDemoServices() {
        String json = "["
                + "{\"id\":101,\"name\":\"Signature Haircut\",\"description\":\"Precision cut with styling finish.\",\"durationMinutes\":45,\"price\":699,\"category\":\"Hair\",\"active\":true},"
                + "{\"id\":102,\"name\":\"Hair Spa Ritual\",\"description\":\"Deep nourishment and relaxing scalp massage.\",\"durationMinutes\":60,\"price\":1199,\"category\":\"Hair\",\"active\":true},"
                + "{\"id\":103,\"name\":\"Beard Sculpt\",\"description\":\"Shape, trim and premium grooming treatment.\",\"durationMinutes\":30,\"price\":399,\"category\":\"Grooming\",\"active\":true},"
                + "{\"id\":104,\"name\":\"Detan Facial\",\"description\":\"Glow-boosting facial with skin brightening pack.\",\"durationMinutes\":50,\"price\":999,\"category\":\"Skin\",\"active\":true},"
                + "{\"id\":105,\"name\":\"Bridal Makeup Trial\",\"description\":\"Look design and complete makeup preview.\",\"durationMinutes\":90,\"price\":2499,\"category\":\"Makeup\",\"active\":true},"
                + "{\"id\":106,\"name\":\"Keratin Smoothening\",\"description\":\"Frizz control and long-lasting sleek texture.\",\"durationMinutes\":120,\"price\":3499,\"category\":\"Hair\",\"active\":true},"
                + "{\"id\":107,\"name\":\"Manicure + Pedicure\",\"description\":\"Nail care, exfoliation and polish finish.\",\"durationMinutes\":75,\"price\":1299,\"category\":\"Nails\",\"active\":true},"
                + "{\"id\":108,\"name\":\"Party Styling\",\"description\":\"Express styling for events and occasions.\",\"durationMinutes\":40,\"price\":899,\"category\":\"Styling\",\"active\":true}"
                + "]";
        return GSON.fromJson(json, SERVICE_LIST_TYPE);
    }

    public static List<ArtistModel> getDemoArtists() {
        String json = "["
                + "{\"id\":201,\"fullName\":\"Aarav Khanna\",\"specialization\":\"Hair Stylist\",\"experienceYears\":7,\"rating\":4.8,\"totalReviews\":142,\"availabilityStatus\":\"AVAILABLE\",\"workingHoursStart\":\"10:00\",\"workingHoursEnd\":\"20:00\",\"serviceNames\":[\"Signature Haircut\",\"Hair Spa Ritual\",\"Keratin Smoothening\"]},"
                + "{\"id\":202,\"fullName\":\"Mia Verma\",\"specialization\":\"Skin Expert\",\"experienceYears\":6,\"rating\":4.7,\"totalReviews\":118,\"availabilityStatus\":\"AVAILABLE\",\"workingHoursStart\":\"10:00\",\"workingHoursEnd\":\"19:00\",\"serviceNames\":[\"Detan Facial\",\"Party Styling\"]},"
                + "{\"id\":203,\"fullName\":\"Kabir Singh\",\"specialization\":\"Grooming Specialist\",\"experienceYears\":5,\"rating\":4.6,\"totalReviews\":96,\"availabilityStatus\":\"AVAILABLE\",\"workingHoursStart\":\"11:00\",\"workingHoursEnd\":\"21:00\",\"serviceNames\":[\"Beard Sculpt\",\"Signature Haircut\"]},"
                + "{\"id\":204,\"fullName\":\"Ananya Rao\",\"specialization\":\"Makeup Artist\",\"experienceYears\":8,\"rating\":4.9,\"totalReviews\":173,\"availabilityStatus\":\"AVAILABLE\",\"workingHoursStart\":\"09:00\",\"workingHoursEnd\":\"18:00\",\"serviceNames\":[\"Bridal Makeup Trial\",\"Party Styling\"]}"
                + "]";
        return GSON.fromJson(json, ARTIST_LIST_TYPE);
    }

    public static List<SlotModel> getDemoSlots() {
        String json = "["
                + "{\"startTime\":\"10:00:00\",\"endTime\":\"10:30:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"10:30:00\",\"endTime\":\"11:00:00\",\"status\":\"BOOKED\"},"
                + "{\"startTime\":\"11:00:00\",\"endTime\":\"11:30:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"11:30:00\",\"endTime\":\"12:00:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"12:00:00\",\"endTime\":\"12:30:00\",\"status\":\"BOOKED\"},"
                + "{\"startTime\":\"12:30:00\",\"endTime\":\"13:00:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"14:00:00\",\"endTime\":\"14:30:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"14:30:00\",\"endTime\":\"15:00:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"15:00:00\",\"endTime\":\"15:30:00\",\"status\":\"BLOCKED\"},"
                + "{\"startTime\":\"15:30:00\",\"endTime\":\"16:00:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"16:00:00\",\"endTime\":\"16:30:00\",\"status\":\"AVAILABLE\"},"
                + "{\"startTime\":\"16:30:00\",\"endTime\":\"17:00:00\",\"status\":\"AVAILABLE\"}"
                + "]";
        return GSON.fromJson(json, SLOT_LIST_TYPE);
    }

    public static synchronized List<BookingResponse> getDemoAppointments() {
        if (DEMO_APPOINTMENTS.isEmpty()) {
            String json = "["
                    + "{\"id\":900001,\"customerName\":\"You\",\"artistId\":201,\"artistName\":\"Aarav Khanna\",\"serviceId\":101,\"serviceName\":\"Signature Haircut\",\"appointmentDate\":\"2026-03-26\",\"startTime\":\"11:00:00\",\"endTime\":\"11:45:00\",\"status\":\"BOOKED\",\"notes\":\"\",\"price\":699,\"paymentStatus\":\"PENDING\",\"paymentMethod\":\"PAY_AT_SALON\"},"
                    + "{\"id\":900002,\"customerName\":\"You\",\"artistId\":202,\"artistName\":\"Mia Verma\",\"serviceId\":104,\"serviceName\":\"Detan Facial\",\"appointmentDate\":\"2026-03-28\",\"startTime\":\"16:00:00\",\"endTime\":\"16:50:00\",\"status\":\"BOOKED\",\"notes\":\"\",\"price\":999,\"paymentStatus\":\"PENDING\",\"paymentMethod\":\"PAY_AT_SALON\"}"
                    + "]";
            DEMO_APPOINTMENTS.addAll(GSON.fromJson(json, BOOKING_LIST_TYPE));
            nextDemoBookingId = 900003L;
        }
        return new ArrayList<>(DEMO_APPOINTMENTS);
    }

    public static synchronized BookingResponse createDemoBooking(
            String artistName,
            String serviceName,
            String appointmentDate,
            String startTime,
            String paymentMethod
    ) {
        String safeArtist = artistName == null || artistName.trim().isEmpty() ? "Salon Artist" : artistName;
        String safeService = serviceName == null || serviceName.trim().isEmpty() ? "Salon Service" : serviceName;
        String safeDate = appointmentDate == null || appointmentDate.trim().isEmpty() ? "2026-03-30" : appointmentDate;
        String safeStart = startTime == null || startTime.trim().isEmpty() ? "11:00:00" : startTime;
        String safePayment = paymentMethod == null || paymentMethod.trim().isEmpty()
                ? "PAY_AT_SALON"
                : paymentMethod;

        double price = 899.0;
        String lowered = safeService.toLowerCase(Locale.ROOT);
        if (lowered.contains("haircut")) price = 699.0;
        if (lowered.contains("facial")) price = 999.0;
        if (lowered.contains("spa")) price = 1199.0;
        if (lowered.contains("bridal")) price = 2499.0;

        long id = nextDemoBookingId++;
        String json = "{"
                + "\"id\":" + id + ","
                + "\"customerName\":\"You\","
                + "\"artistId\":201,"
                + "\"artistName\":\"" + escape(safeArtist) + "\","
                + "\"serviceId\":101,"
                + "\"serviceName\":\"" + escape(safeService) + "\","
                + "\"appointmentDate\":\"" + escape(safeDate) + "\","
                + "\"startTime\":\"" + escape(safeStart) + "\","
                + "\"endTime\":\"\","
                + "\"status\":\"BOOKED\","
                + "\"notes\":\"\","
                + "\"price\":" + price + ","
                + "\"paymentStatus\":\"PENDING\","
                + "\"paymentMethod\":\"" + escape(safePayment) + "\""
                + "}";
        BookingResponse booking = GSON.fromJson(json, BookingResponse.class);
        DEMO_APPOINTMENTS.add(0, booking);
        return booking;
    }

    public static synchronized void cancelDemoAppointment(Long appointmentId) {
        if (appointmentId == null) return;
        DEMO_APPOINTMENTS.removeIf(item -> appointmentId.equals(item.getId()));
    }

    public static synchronized void rescheduleDemoAppointment(
            Long appointmentId,
            String appointmentDate,
            String startTime
    ) {
        if (appointmentId == null) return;
        for (int i = 0; i < DEMO_APPOINTMENTS.size(); i++) {
            BookingResponse current = DEMO_APPOINTMENTS.get(i);
            if (appointmentId.equals(current.getId())) {
                String json = "{"
                        + "\"id\":" + current.getId() + ","
                        + "\"customerName\":\"" + escape(safe(current.getCustomerName(), "You")) + "\","
                        + "\"artistId\":" + (current.getArtistId() != null ? current.getArtistId() : 201) + ","
                        + "\"artistName\":\"" + escape(safe(current.getArtistName(), "Salon Artist")) + "\","
                        + "\"serviceId\":" + (current.getServiceId() != null ? current.getServiceId() : 101) + ","
                        + "\"serviceName\":\"" + escape(safe(current.getServiceName(), "Salon Service")) + "\","
                        + "\"appointmentDate\":\"" + escape(safe(appointmentDate, current.getAppointmentDate())) + "\","
                        + "\"startTime\":\"" + escape(safe(startTime, current.getStartTime())) + "\","
                        + "\"endTime\":\"" + escape(safe(current.getEndTime(), "")) + "\","
                        + "\"status\":\"BOOKED\","
                        + "\"notes\":\"\","
                        + "\"price\":" + (current.getPrice() != null ? current.getPrice() : 899.0) + ","
                        + "\"paymentStatus\":\"" + escape(safe(current.getPaymentStatus(), "PENDING")) + "\","
                        + "\"paymentMethod\":\"" + escape(safe(current.getPaymentMethod(), "PAY_AT_SALON")) + "\""
                        + "}";
                DEMO_APPOINTMENTS.set(i, GSON.fromJson(json, BookingResponse.class));
                break;
            }
        }
    }

    public static boolean isDemoAppointment(Long appointmentId) {
        return appointmentId != null && appointmentId >= 900000L;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
