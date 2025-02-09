package com.example.firebasemoviesearch_app.model;

public class Movie {
    private String imdbID;
    private String title;
    private String year;
    private String poster;

    // This is Parameterized Constructor
    public Movie(String title, String year, String poster) {
        this.title = title;
        this.year = year;
        this.poster = poster;
    }

    // This is Default Constructor
    public Movie() {
    }

    public String getImdbID() {
        return imdbID;
    }

    public String setImdbID(String imdbID) {
        return this.imdbID = imdbID;
    }

    public String getTitle() {
        return title;
    }
    public String getYear() {
        return year;
    }
    public String getPoster() {
        return poster;
    }
}
