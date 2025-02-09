package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firebasemoviesearch_app.databinding.ActivityMainBinding;
import com.example.firebasemoviesearch_app.viewmodel.MovieViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MovieViewModel viewModel;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize RecyclerView and Adapter
        myAdapter = new MyAdapter(new ArrayList<>(), movie -> {
            // Navigate to the details screen, passing the movie data
            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("year", movie.getYear());
            intent.putExtra("poster", movie.getPoster());
            intent.putExtra("imdbID", movie.getImdbID());
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(myAdapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // Observe movie list updates
        viewModel.getMovies().observe(this, movieList -> {
            if (movieList != null && !movieList.isEmpty()) {
                myAdapter.updateMovies(movieList); // Update adapter data
                Log.i("MainActivity", "Movies updated: " + movieList.size());
            } else {
                Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
            }
        });

        // Search button click listener
        binding.searchBtn.setOnClickListener(v -> {
            String searchTerm = binding.searchField.getText().toString().trim();
            if (searchTerm.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.searchMovies(searchTerm); // Trigger API call through ViewModel
            Log.i("MainActivity", "Searching for: " + searchTerm);
        });

        binding.favouritesBtn.setOnClickListener(v -> {
            Intent intentObj = new Intent(getApplicationContext(), Favourites.class);
            startActivity(intentObj);
            finish();
        });
    }
}
