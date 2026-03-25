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
import com.salon.app.R;
import com.salon.app.adapters.ArtistAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.ArtistModel;
import com.salon.app.utils.DemoDataProvider;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ARTIST LIST ACTIVITY
 * Shows available artists after a service is selected.
 * Tapping an artist → navigates to SlotSelectionActivity.
 */
public class ArtistListActivity extends AppCompatActivity implements ArtistAdapter.OnArtistClickListener {

    private RecyclerView rvArtists;
    private ProgressBar progressBar;
    private TextView tvSelectedService;
    private TextView tvEmpty;
    private final List<ArtistModel> artists = new ArrayList<>();
    private ArtistAdapter adapter;
    private long serviceId;
    private String serviceName;
    private int serviceDuration;
    private int fallbackAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        serviceId = getIntent().getLongExtra("serviceId", -1);
        serviceName = getIntent().getStringExtra("serviceName");
        serviceDuration = getIntent().getIntExtra("serviceDuration", 30);

        rvArtists = findViewById(R.id.rvArtists);
        progressBar = findViewById(R.id.progressBar);
        tvSelectedService = findViewById(R.id.tvSelectedService);
        tvEmpty = findViewById(R.id.tvEmpty);

        if (serviceName != null && !serviceName.trim().isEmpty()) {
            tvSelectedService.setText("Service: " + serviceName);
        }

        // Back button
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        adapter = new ArtistAdapter(artists, this);
        rvArtists.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setAdapter(adapter);

        loadArtists();
    }

    private void loadArtists() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        ApiService apiService = ApiClient.getApiService(this);

        apiService.getAllArtists().enqueue(new Callback<ApiResponse<List<ArtistModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ArtistModel>>> call,
                                   Response<ApiResponse<List<ArtistModel>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fallbackAttempts = 0;
                    artists.clear();
                    List<ArtistModel> data = response.body().getData();
                    if (data != null) {
                        artists.addAll(data);
                    }
                    if (artists.isEmpty()) {
                        artists.addAll(DemoDataProvider.getDemoArtists());
                        Toast.makeText(ArtistListActivity.this,
                                "Showing demo artists", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(artists.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    artists.clear();
                    artists.addAll(DemoDataProvider.getDemoArtists());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(View.GONE);
                    Toast.makeText(ArtistListActivity.this,
                            "Using demo artists right now.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ArtistModel>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (ApiClient.tryFallbackToAlternateBaseUrl(t)) {
                    fallbackAttempts++;
                    loadArtists();
                    return;
                }
                tvEmpty.setText("Could not connect to server. Tap to retry.");
                tvEmpty.setOnClickListener(v -> {
                    fallbackAttempts = 0;
                    ApiClient.resetToPrimaryBaseUrl();
                    loadArtists();
                });
                artists.clear();
                artists.addAll(DemoDataProvider.getDemoArtists());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(View.GONE);
                Toast.makeText(ArtistListActivity.this,
                        "Backend unavailable. Demo artists loaded.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onArtistClick(ArtistModel artist) {
        Intent intent = new Intent(this, SlotSelectionActivity.class);
        intent.putExtra("artistId", artist.getId());
        intent.putExtra("artistName", artist.getFullName());
        intent.putExtra("serviceId", serviceId);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("serviceDuration", serviceDuration);
        startActivity(intent);
    }
}
