package com.example.firebasemoviesearch_app.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasemoviesearch_app.databinding.MovieLayoutBinding;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public final MovieLayoutBinding binding;

    public MyViewHolder(@NonNull MovieLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}