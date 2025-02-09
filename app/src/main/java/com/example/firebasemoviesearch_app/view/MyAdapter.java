package com.example.firebasemoviesearch_app.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasemoviesearch_app.databinding.MovieLayoutBinding;
import com.example.firebasemoviesearch_app.model.Movie;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final List<Movie> movieList;
    private final onMovieClickListener listener;

    public MyAdapter(List<Movie> movieList, onMovieClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    public interface onMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public void updateMovies(List<Movie> movies) {
        this.movieList.clear();
        this.movieList.addAll(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieLayoutBinding binding = MovieLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.binding.movieTitle.setText(movie.getTitle());
        holder.binding.movieYear.setText(movie.getYear());

        // Load poster using Glide
        Glide.with(holder.itemView.getContext())
                .load(movie.getPoster())
                .into(holder.binding.moviePoster);

        // Add click listener to navigate to MovieDetailsActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("year", movie.getYear());
                intent.putExtra("poster", movie.getPoster());
                intent.putExtra("imdbID", movie.getImdbID());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
