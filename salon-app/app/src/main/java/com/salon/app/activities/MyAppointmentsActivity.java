package com.salon.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salon.app.R;
import com.salon.app.adapters.AppointmentAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.BookingRequest;
import com.salon.app.models.BookingResponse;
import com.salon.app.utils.DemoDataProvider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAppointmentsActivity extends AppCompatActivity
        implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView rvAppointments;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private final List<BookingResponse> appointments = new ArrayList<>();
    private AppointmentAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        apiService = ApiClient.getApiService(this);
        rvAppointments = findViewById(R.id.rvAppointments);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new AppointmentAdapter(appointments, this);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(adapter);

        // Bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_appointments);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return true;
        });

        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_appointments);
    }

    private void loadAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        apiService.getMyAppointments().enqueue(new Callback<ApiResponse<List<BookingResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BookingResponse>>> call,
                                   Response<ApiResponse<List<BookingResponse>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    appointments.clear();
                    List<BookingResponse> data = response.body().getData();
                    if (data != null) {
                        appointments.addAll(data);
                    }
                    if (appointments.isEmpty()) {
                        appointments.addAll(DemoDataProvider.getDemoAppointments());
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    appointments.clear();
                    appointments.addAll(DemoDataProvider.getDemoAppointments());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BookingResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                appointments.clear();
                appointments.addAll(DemoDataProvider.getDemoAppointments());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(MyAppointmentsActivity.this,
                        "Backend unavailable. Demo appointments loaded.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCancelClick(BookingResponse appointment) {
        if (DemoDataProvider.isDemoAppointment(appointment.getId())) {
            DemoDataProvider.cancelDemoAppointment(appointment.getId());
            loadAppointments();
            Toast.makeText(this, "Demo appointment cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    apiService.cancelAppointment(appointment.getId())
                            .enqueue(new Callback<ApiResponse<BookingResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<BookingResponse>> call,
                                               Response<ApiResponse<BookingResponse>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(MyAppointmentsActivity.this,
                                        "Appointment cancelled", Toast.LENGTH_SHORT).show();
                                loadAppointments();
                            } else {
                                Toast.makeText(MyAppointmentsActivity.this,
                                        "Cannot cancel (may be within cancellation policy window)",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<BookingResponse>> call, Throwable t) {
                            Toast.makeText(MyAppointmentsActivity.this,
                                    "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRescheduleClick(BookingResponse appointment) {
        if (DemoDataProvider.isDemoAppointment(appointment.getId())) {
            openDemoReschedulePicker(appointment);
            return;
        }

        if (appointment.getArtistId() == null || appointment.getServiceId() == null) {
            Toast.makeText(this,
                    "Reschedule unavailable for this item. Please refresh appointments.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        openBackendReschedulePicker(appointment);
    }

    private void openDemoReschedulePicker(BookingResponse appointment) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                    String[] slots = new String[]{"10:00:00", "11:30:00", "13:00:00", "15:30:00", "17:00:00"};
                    new AlertDialog.Builder(this)
                            .setTitle("Pick New Time")
                            .setItems(slots, (d, which) -> {
                                DemoDataProvider.rescheduleDemoAppointment(
                                        appointment.getId(),
                                        selectedDate,
                                        slots[which]);
                                loadAppointments();
                                Toast.makeText(this, "Demo appointment rescheduled", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void openBackendReschedulePicker(BookingResponse appointment) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                    String[] slots = new String[]{"10:00:00", "11:00:00", "12:30:00", "14:00:00", "16:00:00"};
                    new AlertDialog.Builder(this)
                            .setTitle("Pick New Time")
                            .setItems(slots, (d, which) -> {
                                BookingRequest request = new BookingRequest(
                                        appointment.getArtistId(),
                                        appointment.getServiceId(),
                                        selectedDate,
                                        slots[which],
                                        appointment.getPaymentMethod() != null
                                                ? appointment.getPaymentMethod()
                                                : "PAY_AT_SALON",
                                        appointment.getNotes());

                                apiService.rescheduleAppointment(appointment.getId(), request)
                                        .enqueue(new Callback<ApiResponse<BookingResponse>>() {
                                            @Override
                                            public void onResponse(Call<ApiResponse<BookingResponse>> call,
                                                                   Response<ApiResponse<BookingResponse>> response) {
                                                if (response.isSuccessful()
                                                        && response.body() != null
                                                        && response.body().isSuccess()) {
                                                    Toast.makeText(MyAppointmentsActivity.this,
                                                            "Appointment rescheduled", Toast.LENGTH_SHORT).show();
                                                    loadAppointments();
                                                } else {
                                                    Toast.makeText(MyAppointmentsActivity.this,
                                                            "Reschedule failed. Switched to demo mode.",
                                                            Toast.LENGTH_LONG).show();
                                                    DemoDataProvider.rescheduleDemoAppointment(
                                                            appointment.getId(),
                                                            selectedDate,
                                                            slots[which]);
                                                    loadAppointments();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ApiResponse<BookingResponse>> call, Throwable t) {
                                                Toast.makeText(MyAppointmentsActivity.this,
                                                        "Backend unavailable. Rescheduled in demo mode.",
                                                        Toast.LENGTH_LONG).show();
                                                DemoDataProvider.rescheduleDemoAppointment(
                                                        appointment.getId(),
                                                        selectedDate,
                                                        slots[which]);
                                                loadAppointments();
                                            }
                                        });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }
}
