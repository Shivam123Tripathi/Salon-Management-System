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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.salon.app.R;
import com.salon.app.adapters.SlotAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.*;
import com.salon.app.utils.DemoDataProvider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SLOT SELECTION ACTIVITY
 * Shows available time slots for the selected artist + service + date.
 * User picks a date → slots load → user taps a slot → confirms booking.
 */
public class SlotSelectionActivity extends AppCompatActivity implements SlotAdapter.OnSlotClickListener {

    private long artistId, serviceId;
    private String artistName, serviceName;
    private String selectedDate;
    private SlotModel selectedSlot;
    private RecyclerView rvSlots;
    private ProgressBar progressBar;
    private MaterialButton btnSelectDate, btnConfirm;
    private TextView tvSlotsLabel, tvNoSlots;
    private final List<SlotModel> slots = new ArrayList<>();
    private SlotAdapter adapter;
    private ApiService apiService;
    private int slotFallbackAttempts = 0;
    private String selectedPaymentMethod = "PAY_AT_SALON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_selection);

        apiService = ApiClient.getApiService(this);

        // Get data from previous screen
        artistId = getIntent().getLongExtra("artistId", -1);
        artistName = getIntent().getStringExtra("artistName");
        serviceId = getIntent().getLongExtra("serviceId", -1);
        serviceName = getIntent().getStringExtra("serviceName");

        // Display booking summary
        ((TextView) findViewById(R.id.tvServiceInfo)).setText("Service: " + serviceName);
        ((TextView) findViewById(R.id.tvArtistInfo)).setText("Artist: " + artistName);

        rvSlots = findViewById(R.id.rvSlots);
        progressBar = findViewById(R.id.progressBar);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvSlotsLabel = findViewById(R.id.tvSlotsLabel);
        tvNoSlots = findViewById(R.id.tvNoSlots);
        ChipGroup chipPaymentMethods = findViewById(R.id.chipPaymentMethods);
        Chip chipPayAtSalon = findViewById(R.id.chipPayAtSalon);
        Chip chipUpi = findViewById(R.id.chipUpi);
        Chip chipCard = findViewById(R.id.chipCard);

        // 4-column grid for slot chips
        adapter = new SlotAdapter(slots, this);
        rvSlots.setLayoutManager(new GridLayoutManager(this, 4));
        rvSlots.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // Date picker
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Confirm booking
        btnConfirm.setOnClickListener(v -> confirmBooking());

        chipPayAtSalon.setChecked(true);
        applyPaymentChipStyle(chipPayAtSalon, true);
        applyPaymentChipStyle(chipUpi, false);
        applyPaymentChipStyle(chipCard, false);

        chipPaymentMethods.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedPaymentMethod = "PAY_AT_SALON";
                chipPayAtSalon.setChecked(true);
                applyPaymentChipStyle(chipPayAtSalon, true);
                applyPaymentChipStyle(chipUpi, false);
                applyPaymentChipStyle(chipCard, false);
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipUpi) {
                selectedPaymentMethod = "UPI";
            } else if (checkedId == R.id.chipCard) {
                selectedPaymentMethod = "CARD";
            } else {
                selectedPaymentMethod = "PAY_AT_SALON";
            }
            applyPaymentChipStyle(chipPayAtSalon, checkedId == R.id.chipPayAtSalon);
            applyPaymentChipStyle(chipUpi, checkedId == R.id.chipUpi);
            applyPaymentChipStyle(chipCard, checkedId == R.id.chipCard);
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                    btnSelectDate.setText("📅 " + selectedDate);
                    loadSlots();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        // Don't allow picking past dates
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void loadSlots() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoSlots.setVisibility(View.GONE);
        btnConfirm.setEnabled(false);
        selectedSlot = null;
        apiService = ApiClient.getApiService(this);

        apiService.getAvailableSlots(artistId, serviceId, selectedDate)
                .enqueue(new Callback<ApiResponse<List<SlotModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SlotModel>>> call,
                                   Response<ApiResponse<List<SlotModel>>> response) {
                progressBar.setVisibility(View.GONE);
                tvSlotsLabel.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    slotFallbackAttempts = 0;
                    slots.clear();
                    List<SlotModel> data = response.body().getData();
                    if (data != null) {
                        slots.addAll(data);
                    }
                    if (slots.isEmpty()) {
                        slots.addAll(DemoDataProvider.getDemoSlots());
                        Toast.makeText(SlotSelectionActivity.this,
                                "Showing demo slots", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();

                    if (slots.isEmpty()) {
                        tvNoSlots.setVisibility(View.VISIBLE);
                    }
                } else {
                    slots.clear();
                    slots.addAll(DemoDataProvider.getDemoSlots());
                    adapter.notifyDataSetChanged();
                    tvNoSlots.setVisibility(View.GONE);
                    Toast.makeText(SlotSelectionActivity.this,
                            "Using demo slots right now.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SlotModel>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (ApiClient.tryFallbackToAlternateBaseUrl(t)) {
                    slotFallbackAttempts++;
                    loadSlots();
                    return;
                }
                tvNoSlots.setText("Could not connect to server. Tap to retry.");
                tvNoSlots.setOnClickListener(v -> {
                    slotFallbackAttempts = 0;
                    ApiClient.resetToPrimaryBaseUrl();
                    loadSlots();
                });
                slots.clear();
                slots.addAll(DemoDataProvider.getDemoSlots());
                adapter.notifyDataSetChanged();
                tvNoSlots.setVisibility(View.GONE);
                Toast.makeText(SlotSelectionActivity.this,
                        "Backend unavailable. Demo slots loaded.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSlotSelected(SlotModel slot) {
        selectedSlot = slot;
        btnConfirm.setEnabled(true);
    }

    private void confirmBooking() {
        if (selectedSlot == null || selectedDate == null) return;
        if (!"PAY_AT_SALON".equals(selectedPaymentMethod)) {
            showPaymentDialogAndBook();
            return;
        }
        executeBooking(null);
    }

    private void showPaymentDialogAndBook() {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint(selectedPaymentMethod + " transaction reference");
        new AlertDialog.Builder(this)
                .setTitle("Complete " + selectedPaymentMethod + " payment")
                .setView(input)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String txn = input.getText() != null ? input.getText().toString().trim() : "";
                    if (txn.isEmpty()) {
                        Toast.makeText(this, "Transaction reference required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    executeBooking(txn);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executeBooking(String transactionId) {
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Booking...");

        BookingRequest request = new BookingRequest(
                artistId, serviceId, selectedDate,
                selectedSlot.getStartTime(), selectedPaymentMethod, null);

        apiService.bookAppointment(request).enqueue(new Callback<ApiResponse<BookingResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingResponse>> call,
                                   Response<ApiResponse<BookingResponse>> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText(R.string.confirm_booking);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BookingResponse booking = response.body().getData();
                    if (!"PAY_AT_SALON".equals(selectedPaymentMethod)
                            && booking != null && booking.getId() != null
                            && transactionId != null && !transactionId.isEmpty()) {
                        apiService.completePayment(booking.getId(), transactionId)
                                .enqueue(new Callback<ApiResponse<PaymentResponse>>() {
                                    @Override
                                    public void onResponse(Call<ApiResponse<PaymentResponse>> call,
                                                           Response<ApiResponse<PaymentResponse>> paymentResponse) {
                                        Toast.makeText(SlotSelectionActivity.this,
                                                "Payment successful. Appointment booked!",
                                                Toast.LENGTH_LONG).show();
                                        goToAppointments();
                                    }

                                    @Override
                                    public void onFailure(Call<ApiResponse<PaymentResponse>> call, Throwable t) {
                                        Toast.makeText(SlotSelectionActivity.this,
                                                "Booking created, payment pending verification.",
                                                Toast.LENGTH_LONG).show();
                                        goToAppointments();
                                    }
                                });
                        return;
                    }
                    Toast.makeText(SlotSelectionActivity.this,
                            "✅ Appointment booked successfully!", Toast.LENGTH_LONG).show();
                    goToAppointments();
                } else {
                    DemoDataProvider.createDemoBooking(
                            artistName, serviceName, selectedDate, selectedSlot.getStartTime(), selectedPaymentMethod);
                    Toast.makeText(SlotSelectionActivity.this,
                            "Booked in demo mode (" + selectedPaymentMethod + ").", Toast.LENGTH_SHORT).show();
                    goToAppointments();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingResponse>> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText(R.string.confirm_booking);
                DemoDataProvider.createDemoBooking(
                        artistName, serviceName, selectedDate, selectedSlot.getStartTime(), selectedPaymentMethod);
                Toast.makeText(SlotSelectionActivity.this,
                        "Booked in demo mode (" + selectedPaymentMethod + ").", Toast.LENGTH_SHORT).show();
                goToAppointments();
            }
        });
    }

    private void goToAppointments() {
        Intent intent = new Intent(SlotSelectionActivity.this, MyAppointmentsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void applyPaymentChipStyle(Chip chip, boolean selected) {
        if (selected) {
            chip.setChipBackgroundColorResource(R.color.primary);
            chip.setTextColor(getColor(R.color.black));
            chip.setChipStrokeColorResource(R.color.primary);
        } else {
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setTextColor(getColor(R.color.text_primary));
            chip.setChipStrokeColorResource(R.color.divider);
        }
    }
}
