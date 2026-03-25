package com.salon.app.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.salon.app.R;
import com.salon.app.adapters.ServiceAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.ServiceModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Admin screen to manage salon services (view list + add new via dialog).
 */
public class ManageServicesActivity extends AppCompatActivity implements ServiceAdapter.OnServiceClickListener {

    private RecyclerView rvServices;
    private ProgressBar progressBar;
    private final List<ServiceModel> services = new ArrayList<>();
    private ServiceAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        apiService = ApiClient.getApiService(this);
        rvServices = findViewById(R.id.rvServices);
        progressBar = findViewById(R.id.progressBar);

        adapter = new ServiceAdapter(services, this);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());

        loadServices();
    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getAllServicesAdmin().enqueue(new Callback<ApiResponse<List<ServiceModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceModel>>> call,
                                   Response<ApiResponse<List<ServiceModel>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    services.clear();
                    services.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceModel>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageServicesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDesc = dialogView.findViewById(R.id.etDescription);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);

        new AlertDialog.Builder(this)
                .setTitle("Add New Service")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    if (name.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Fill in required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ServiceModel service = new ServiceModel(name, desc,
                            Integer.parseInt(durationStr), Double.parseDouble(priceStr), category);

                    apiService.addService(service).enqueue(new Callback<ApiResponse<ServiceModel>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ServiceModel>> call,
                                               Response<ApiResponse<ServiceModel>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(ManageServicesActivity.this, "Service added!", Toast.LENGTH_SHORT).show();
                                loadServices();
                            } else {
                                Toast.makeText(ManageServicesActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ServiceModel>> call, Throwable t) {
                            Toast.makeText(ManageServicesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBookClick(ServiceModel service) {
        // In admin context, tapping a service does nothing (or could edit)
        Toast.makeText(this, "Service: " + service.getName(), Toast.LENGTH_SHORT).show();
    }
}
