package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firebasemoviesearch_app.databinding.ActivityFavouritesBinding;
import com.example.firebasemoviesearch_app.model.Movie;
import com.example.firebasemoviesearch_app.viewmodel.MovieViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity {

    private ActivityFavouritesBinding binding;
    private MovieViewModel viewModel;
    private MyAdapter myAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view favorites", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView and Adapter
        myAdapter = new MyAdapter(new ArrayList<>(), null);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(myAdapter);

        // Fetch favorite movies from Firestore
        fetchFavoriteMovies(userEmail);

        // Optional: Using ViewModel (if needed)
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.getMovies().observe(this, movieList -> {
            if (movieList != null && !movieList.isEmpty()) {
                myAdapter.updateMovies(movieList);
                Log.i("FavouritesActivity", "Movies updated: " + movieList.size());
            } else {
                Toast.makeText(Favourites.this, "No movies found", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        binding.searchBtn.setOnClickListener(v -> {
            // go to mainactivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
    }

    private void fetchFavoriteMovies(String userEmail) {
        db.collection(userEmail)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(Favourites.this, "Error fetching favorite movies", Toast.LENGTH_SHORT).show();
                        Log.e("FavouritesActivity", "Snapshot listener error", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Movie> movieList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Movie movie = document.toObject(Movie.class);
                            if (movie != null) {
                                movieList.add(movie);
                            }
                        }
                        myAdapter.updateMovies(movieList);
                    } else {
                        Toast.makeText(Favourites.this, "No movies found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
