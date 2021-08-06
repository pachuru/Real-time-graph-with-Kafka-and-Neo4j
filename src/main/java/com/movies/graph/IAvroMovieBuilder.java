package com.movies.graph;

public interface IAvroMovieBuilder {
    String extractTitle(String line);
    String extractType(String line);
    String extractCast(String line);
    String extractDescription(String line);
    String extractCountries(String line);
    String extractGenres(String line);
    String extractDirectors(String line);
    Integer extractReleaseYear(String line);
    String extractRating(String line);
    Integer extractDuration(String line);
    AvroMovie createAvroMovieFromCSVLine(String[] line);
}
