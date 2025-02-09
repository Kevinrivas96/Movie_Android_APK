package com.example.firebasemoviesearch_app.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebasemoviesearch_app.model.Movie;
import com.example.firebasemoviesearch_app.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MovieViewModel extends ViewModel {

    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();

    public MutableLiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void searchMovies(String searchTerm) {
        // Make API call to search for movies with the given query

        String getUrl = "https://www.omdbapi.com/?apikey=363a1c42&type=movie&s=" + searchTerm;

        ApiClient.get(getUrl, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseData = response.body().string();
                Log.i("MovieViewModel", responseData); // Log the response

                try{
                    JSONObject jsonObject = new JSONObject(responseData);

                    if (jsonObject.has("Search")) {
                        JSONArray movieArray = jsonObject.getJSONArray("Search");

                        List<Movie> movieList = new ArrayList<>();
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            Movie movie = new Movie(movieObject.getString("Title"), movieObject.getString("Year"), movieObject.getString("Poster"));
                            movie.setImdbID(movieObject.getString("imdbID"));
                            movieList.add(movie);
                        }
                        movies.postValue(movieList); // Update LiveData
                    } else {
                        Log.i("MovieViewModel", "No movies found");
                        movies.postValue(new ArrayList<>()); // Clear the list if no movies are found
                    }
                } catch (JSONException e) {
                    Log.e("MovieViewModel", "JSON parsing error: " + e.getMessage());
                    movies.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MovieViewModel", "API call failed: " + e.getMessage());
                movies.postValue(new ArrayList<>()); // Clear the list on failure
            }
        });
    }
}

