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
import com.salon.app.adapters.ArtistAdapter;
import com.salon.app.api.ApiClient;
import com.salon.app.api.ApiService;
import com.salon.app.models.ApiResponse;
import com.salon.app.models.ArtistModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageArtistsActivity extends AppCompatActivity implements ArtistAdapter.OnArtistClickListener {

    private RecyclerView rvArtists;
    private ProgressBar progressBar;
    private final List<ArtistModel> artists = new ArrayList<>();
    private ArtistAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_artists);

        apiService = ApiClient.getApiService(this);
        rvArtists = findViewById(R.id.rvArtists);
        progressBar = findViewById(R.id.progressBar);

        adapter = new ArtistAdapter(artists, this);
        rvArtists.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());

        loadArtists();
    }

    private void loadArtists() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getAllArtists().enqueue(new Callback<ApiResponse<List<ArtistModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ArtistModel>>> call,
                                   Response<ApiResponse<List<ArtistModel>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    artists.clear();
                    artists.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ArtistModel>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageArtistsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_artist, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etSpec = dialogView.findViewById(R.id.etSpecialization);
        EditText etExp = dialogView.findViewById(R.id.etExperience);
        EditText etStart = dialogView.findViewById(R.id.etStartTime);
        EditText etEnd = dialogView.findViewById(R.id.etEndTime);

        new AlertDialog.Builder(this)
                .setTitle("Add New Artist")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String spec = etSpec.getText().toString().trim();
                    String expStr = etExp.getText().toString().trim();
                    String start = etStart.getText().toString().trim();
                    String end = etEnd.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArtistModel artist = new ArtistModel(name, spec,
                            expStr.isEmpty() ? 0 : Integer.parseInt(expStr), start, end);

                    apiService.addArtist(artist).enqueue(new Callback<ApiResponse<ArtistModel>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ArtistModel>> call,
                                               Response<ApiResponse<ArtistModel>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(ManageArtistsActivity.this, "Artist added!", Toast.LENGTH_SHORT).show();
                                loadArtists();
                            } else {
                                Toast.makeText(ManageArtistsActivity.this, "Failed to add artist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ArtistModel>> call, Throwable t) {
                            Toast.makeText(ManageArtistsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onArtistClick(ArtistModel artist) {
        Toast.makeText(this, "Artist: " + artist.getFullName(), Toast.LENGTH_SHORT).show();
    }
}
