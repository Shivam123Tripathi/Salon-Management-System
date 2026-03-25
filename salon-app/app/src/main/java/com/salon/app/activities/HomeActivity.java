package com.salon.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salon.app.R;
import com.salon.app.adapters.ServiceAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.ServiceModel;
import com.salon.app.utils.DemoDataProvider;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HOME ACTIVITY — Main screen after login.
 * Shows a list of all salon services.
 * Tapping "Book Now" on a service → navigates to ArtistListActivity.
 */
public class HomeActivity extends AppCompatActivity implements ServiceAdapter.OnServiceClickListener {

    private RecyclerView rvServices;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private final List<ServiceModel> services = new ArrayList<>();
    private ServiceAdapter adapter;
    private ApiService apiService;
    private int fallbackAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Start from primary backend on every fresh HomeActivity creation.
        ApiClient.resetToPrimaryBaseUrl();
        apiService = ApiClient.getApiService(this);

        rvServices = findViewById(R.id.rvServices);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Setup RecyclerView
        adapter = new ServiceAdapter(services, this);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setNestedScrollingEnabled(true);
        rvServices.setAdapter(adapter);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_appointments) {
                startActivity(new Intent(this, MyAppointmentsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return true;
        });

        fallbackAttempts = 0;
        loadServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        apiService = ApiClient.getApiService(this);

        apiService.getAllServices().enqueue(new Callback<ApiResponse<List<ServiceModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceModel>>> call,
                                   Response<ApiResponse<List<ServiceModel>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fallbackAttempts = 0;
                    services.clear();
                    List<ServiceModel> data = response.body().getData();
                    if (data != null) {
                        services.addAll(data);
                    }
                    if (services.isEmpty()) {
                        services.addAll(DemoDataProvider.getDemoServices());
                        Toast.makeText(HomeActivity.this,
                                "Showing demo services", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();

                    if (services.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        tvEmpty.setOnClickListener(null);
                    }
                } else {
                    services.clear();
                    services.addAll(DemoDataProvider.getDemoServices());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this,
                            "Using demo services right now.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceModel>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (ApiClient.tryFallbackToAlternateBaseUrl(t)) {
                    fallbackAttempts++;
                    loadServices();
                    return;
                }
                tvEmpty.setText("Could not connect to server. Tap to retry.");
                tvEmpty.setOnClickListener(v -> {
                    fallbackAttempts = 0;
                    ApiClient.resetToPrimaryBaseUrl();
                    loadServices();
                });
                services.clear();
                services.addAll(DemoDataProvider.getDemoServices());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this,
                        "Backend unavailable. Demo services loaded.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBookClick(ServiceModel service) {
        // Navigate to artist selection, passing the service ID and name
        Intent intent = new Intent(this, ArtistListActivity.class);
        intent.putExtra("serviceId", service.getId());
        intent.putExtra("serviceName", service.getName());
        intent.putExtra("serviceDuration", service.getDurationMinutes());
        startActivity(intent);
    }
}
