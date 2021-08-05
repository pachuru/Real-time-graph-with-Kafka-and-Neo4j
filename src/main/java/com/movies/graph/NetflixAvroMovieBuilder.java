package com.movies.graph;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class NetflixAvroMovieBuilder extends AvroMovieBuilder implements IAvroMovieBuilder{

    private String durationPatternStr = "(\\d+) min";
    private Pattern durationPattern;

    public NetflixAvroMovieBuilder() {
        this.durationPattern = Pattern.compile(durationPatternStr);
    }

    @Override
    public String extractTitle(String line) {
        return null;
    }

    @Override
    public String extractType(String line) {
        return null;
    }

    @Override
    public String extractCast(String line) {
        return null;
    }

    @Override
    public String extractDescription(String line) {
        return null;
    }

    @Override
    public String extractCountries(String line) {
        return null;
    }

    @Override
    public String extractGenres(String line) {
        return null;
    }

    @Override
    public String extractDirectors(String line) {
        return null;
    }

    @Override
    public Integer extractReleaseYear(String line) {
        return null;
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
        String id = UUID.randomUUID().toString();
        String type = this.extractStringValue(line[1]);
        String title = this.extractStringValue(line[2]);
        String director = this.extractStringValue(line[3]);
        String cast = this.extractStringValue(line[4]);
        String country = this.extractStringValue(line[5]);
        String dateAdded = LocalDate.parse("2016-06-12").toString();
        Integer releaseYear = line[7].equals("") ? -1 : Integer.parseInt(line[7]);
        String rating = this.extractStringValue(line[8]);
        Integer duration = this.extractDuration(line[9]);
        String genres = this.extractStringValue(line[10]);
        String description = this.extractStringValue(line[11]);

        return AvroMovie.newBuilder()
                .setId(id)
                .setType(type)
                .setTitle(title)
                .setDirector(director)
                .setCast(cast)
                .setCountry(country)
                .setDateAdded(dateAdded)
                .setReleaseYear(releaseYear)
                .setRating(rating)
                .setDuration(duration)
                .setGenres(genres)
                .setDescription(description)
                .build();
    }
}
