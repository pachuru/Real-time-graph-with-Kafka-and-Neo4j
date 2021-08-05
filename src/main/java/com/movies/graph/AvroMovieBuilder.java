package com.movies.graph;

public class AvroMovieBuilder {
    public String extractStringValue(String value) {
        return value.equals("") ? "Unknown" : value.trim();
    }
}
