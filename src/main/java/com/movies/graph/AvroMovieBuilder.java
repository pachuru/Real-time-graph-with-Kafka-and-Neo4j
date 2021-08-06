package com.movies.graph;

import java.time.LocalDate;
import java.util.UUID;

public class AvroMovieBuilder {

    public String extractStringValue(String value) {
        return value.equals("") ? Constants.UNKNOWN_STR : value.trim();
    }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public String generateDateAdded() {
        return LocalDate.parse("2016-06-12").toString();
    }
}
