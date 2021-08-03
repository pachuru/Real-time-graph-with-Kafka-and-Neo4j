package com.movies.graph;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class AvroMovieBuilder {

    private String durationPatternStr = "(\\d+) min";
    private Pattern durationPattern;

    public AvroMovieBuilder() {
        this.durationPattern = Pattern.compile(durationPatternStr);
    }

    public Integer extractDuration(String duration) {
        Matcher m = this.durationPattern.matcher(duration);
        if(m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }

    public AvroMovie createAvroMovieFromCSVLine(String[] line) {
        String id = UUID.randomUUID().toString();
        String type = line[1];
        String title = line[2];
        String director = line[3];
        String cast = line[4];
        String country = line[5];
        String dateAdded = LocalDate.parse("2016-06-12").toString();
        Integer releaseYear = Integer.parseInt(line[7]);
        String rated = line[8];
        Integer duration = this.extractDuration(line[9]);
        String genres = line[10];
        String description = line[11];

        return AvroMovie.newBuilder()
                .setId(id)
                .setType(type)
                .setTitle(title)
                .setDirector(director)
                .setCast(cast)
                .setCountry(country)
                .setDateAdded(dateAdded)
                .setReleaseYear(releaseYear)
                .setRated(rated)
                .setDuration(duration)
                .setGenres(genres)
                .setDescription(description)
                .build();
    }
}
