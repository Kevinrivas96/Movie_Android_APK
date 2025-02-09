package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebasemoviesearch_app.databinding.ActivityMovieDetailsBinding;
import com.example.firebasemoviesearch_app.utils.ApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private ActivityMovieDetailsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        // Retrieve movie data from the intent
        String title = getIntent().getStringExtra("title");
        String year = getIntent().getStringExtra("year");
        String poster = getIntent().getStringExtra("poster");
        String imdbID = getIntent().getStringExtra("imdbID");

        // Null check for intent data
        if (title == null || year == null || poster == null || imdbID == null) {
            Log.e("MovieDetailsActivity", "Received null data in Intent");
            Toast.makeText(this, "Error loading movie details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set initial data using view binding
        binding.detailsTitle.setText(title);
        binding.detailsYear.setText(year);

        // Load poster using Glide
        if (!poster.isEmpty()) {
            Glide.with(this)
                    .load(poster)
                    .into(binding.detailsPoster);
        }

        // Fetch additional details using IMDb ID
        fetchImdbDetails(imdbID);


        // Back button functionality
        binding.backBtn.setOnClickListener(v -> finish());

        // Heart button functionality
        binding.heartBtn.setOnClickListener(v -> addToFavorites(userEmail, imdbID));

        // Edit button functionality
        binding.editBtn.setOnClickListener(v -> updateMovieDetails(userEmail, imdbID));

        // Delete button functionality
        binding.deleteBtn.setOnClickListener(v -> deleteMovie(userEmail, imdbID));
    }

    private void fetchImdbDetails(String imdbID) {
        String url = "https://www.omdbapi.com/?apikey=363a1c42&i=" + imdbID;

        ApiClient.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("MovieDetailsActivity", "API call failed", e);
                    Toast.makeText(MovieDetailsActivity.this, "Failed to load additional movie details", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String genre = jsonObject.optString("Genre", "N/A");
                        String plot = jsonObject.optString("Plot", "N/A");
                        String runtime = jsonObject.optString("Runtime", "N/A");
                        String rating = jsonObject.optString("imdbRating", "N/A");

                        runOnUiThread(() -> {
                            binding.detailsGenre.setText(genre);
                            binding.movieDescription.setText(plot);
                            binding.detailsRuntime.setText(runtime);
                            binding.detailsRating.setText(rating);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Log.e("MovieDetailsActivity", "API call unsuccessful: " + response.message());
                            Toast.makeText(MovieDetailsActivity.this, "Failed to load additional movie details", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        Log.e("MovieDetailsActivity", "Failed to parse JSON", e);
                        Toast.makeText(MovieDetailsActivity.this, "Error parsing movie details", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void addToFavorites(String userEmail, String imdbID) {
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("title", binding.detailsTitle.getText().toString());
        movieData.put("year", binding.detailsYear.getText().toString());
        movieData.put("poster", getIntent().getStringExtra("poster"));
        movieData.put("imdbID", imdbID);
        movieData.put("genre", binding.detailsGenre.getText().toString());
        movieData.put("plot", binding.movieDescription.getText().toString());
        movieData.put("runtime", binding.detailsRuntime.getText().toString());
        movieData.put("rating", binding.detailsRating.getText().toString());

        db.collection(userEmail)
                .document(imdbID)
                .set(movieData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Added to Favorites!", Toast.LENGTH_SHORT).show();
                    Log.i("MovieDetailsActivity", "Movie added to favorites: " + movieData);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                    Log.e("MovieDetailsActivity", "Failed to add movie to favorites", e);
                });
    }

    private void updateMovieDetails(String userEmail, String imdbID) {
        String newTitle = binding.detailsTitle.getText().toString();

        Map<String, Object> movieData = new HashMap<>();
        movieData.put("title", newTitle);
        movieData.put("year", binding.detailsYear.getText().toString());
        movieData.put("poster", getIntent().getStringExtra("poster"));
        movieData.put("imdbID", imdbID);
        movieData.put("genre", binding.detailsGenre.getText().toString());
        movieData.put("plot", binding.movieDescription.getText().toString());
        movieData.put("runtime", binding.detailsRuntime.getText().toString());
        movieData.put("rating", binding.detailsRating.getText().toString());

        db.collection(userEmail)
                .document(imdbID)
                .set(movieData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Movie updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show();
                    Log.e("MovieDetailsActivity", "Failed to update movie", e);
                });

        // go back to favourites activity
        finish();
    }

    private void deleteMovie(String userEmail, String imdbID) {
        db.collection(userEmail)
                .document(imdbID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Movie deleted!", Toast.LENGTH_SHORT).show();
                    // go back to favourites activity
                    startActivity(new Intent(getApplicationContext(), Favourites.class));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete movie", Toast.LENGTH_SHORT).show();
                    Log.e("MovieDetailsActivity", "Failed to delete movie", e);
                });
    }
}
