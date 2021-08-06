package com.movies.graph;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class NetflixAvroMovieBuilder extends AvroMovieBuilder implements IAvroMovieBuilder {

    private String durationPatternStr = "(\\d+) min";
    private Pattern durationPattern;

    public NetflixAvroMovieBuilder() {
        this.durationPattern = Pattern.compile(durationPatternStr);
    }

    @Override
    public String extractTitle(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractType(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractCast(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractDescription(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractCountries(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractGenres(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractDirectors(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public Integer extractReleaseYear(String line) {
        return line.equals("") ? -1 : Integer.parseInt(line);
    }

    @Override
    public String extractRating(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public Integer extractDuration(String duration) {
        Matcher m = this.durationPattern.matcher(duration);
        if(m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    public AvroMovie createAvroMovieFromCSVLine(String[] line) {
        String id = this.generateId();
        String type = this.extractType(line[1]);
        String title = this.extractTitle(line[2]);
        String directors = this.extractDirectors(line[3]);
        String cast = this.extractCast(line[4]);
        String countries = this.extractCountries(line[5]);
        String dateAdded = this.generateDateAdded();
        Integer releaseYear = this.extractReleaseYear(line[7]);
        String rating = this.extractRating(line[8]);
        Integer duration = this.extractDuration(line[9]);
        String genres = this.extractGenres(line[10]);
        String description = this.extractDescription(line[11]);

        return AvroMovie.newBuilder()
                .setId(id)
                .setType(type)
                .setTitle(title)
                .setDirector(directors)
                .setCast(cast)
                .setCountry(countries)
                .setDateAdded(dateAdded)
                .setReleaseYear(releaseYear)
                .setRating(rating)
                .setDuration(duration)
                .setGenres(genres)
                .setDescription(description)
                .build();
    }
}
