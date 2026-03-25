package com.salon.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.salon.app.R;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.DashboardModel;
import com.salon.app.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalBookings, tvTotalRevenue, tvTodayBookings, tvActiveArtists;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        apiService = ApiClient.getApiService(this);
        tokenManager = new TokenManager(this);

        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTodayBookings = findViewById(R.id.tvTodayBookings);
        tvActiveArtists = findViewById(R.id.tvActiveArtists);

        // Quick action buttons
        findViewById(R.id.btnManageServices).setOnClickListener(v ->
                startActivity(new Intent(this, ManageServicesActivity.class)));
        findViewById(R.id.btnManageArtists).setOnClickListener(v ->
                startActivity(new Intent(this, ManageArtistsActivity.class)));

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Logout", (d, w) -> {
                        tokenManager.clear();
                        ApiClient.reset();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        loadDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void loadDashboard() {
        apiService.getDashboard().enqueue(new Callback<ApiResponse<DashboardModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardModel>> call,
                                   Response<ApiResponse<DashboardModel>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardModel data = response.body().getData();
                    tvTotalBookings.setText(String.valueOf(data.getTotalBookings() != null ? data.getTotalBookings() : 0));
                    tvTotalRevenue.setText(String.format("₹%.0f", data.getTotalRevenue() != null ? data.getTotalRevenue() : 0.0));
                    tvTodayBookings.setText(String.valueOf(data.getTodayBookings() != null ? data.getTodayBookings() : 0));
                    tvActiveArtists.setText(String.valueOf(data.getActiveArtists() != null ? data.getActiveArtists() : 0));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardModel>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Dashboard failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
