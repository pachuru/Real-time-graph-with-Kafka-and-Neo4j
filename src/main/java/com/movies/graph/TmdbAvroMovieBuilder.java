package com.movies.graph;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

public class TmdbAvroMovieBuilder extends AvroMovieBuilder implements IAvroMovieBuilder {


    String jsonObjectPatternStr = "\\{(.*?)\\}";
    Pattern jsonObjectPattern = Pattern.compile(jsonObjectPatternStr);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat yearFormat = new SimpleDateFormat("yyyy");

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
        return extractArrayListOf("name", line);
    }

    @Override
    public String extractDescription(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public String extractCountries(String line) {
        return extractArrayListOf("name", line);
    }

    @Override
    public String extractGenres(String line) {
        return extractArrayListOf("name", line);
    }

    @Override
    public Integer extractReleaseYear(String line){
        if(line.equals("")) { return Constants.UNKNOWN_INT; }
        try {
            return Integer.parseInt(this.yearFormat.format(this.dateFormat.parse(line)));
        }catch(ParseException e){
            System.out.println("Error while extracting release year");
            e.printStackTrace();
            return Constants.UNKNOWN_INT;
        }
    }

    @Override
    public String extractRating(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public Integer extractDuration(String line) {
        if(line.equals("")) { return Constants.UNKNOWN_INT; }
        Double duration = Double.parseDouble(line);
        return duration.intValue();
    }

    public String extractArrayListOf(String attribute, String line) {
        Matcher m = this.jsonObjectPattern.matcher(line);
        ArrayList<String> elements = new ArrayList<>();
        while(m.find()) {
            JSONObject element = new JSONObject(m.group(0));
            elements.add(element.getString(attribute).trim());
        }
        if(elements.size() == 0) { return Constants.UNKNOWN_STR; }
        return String.join(",", elements);
    }

    public String extractDirectors(String line) {
        Matcher m = this.jsonObjectPattern.matcher(line);
        ArrayList<String> directors = new ArrayList<>();
        while(m.find()) {
            JSONObject credit = new JSONObject(m.group(0));
            if(credit.getString("job").equals("Director")) {
                directors.add(credit.getString("name").trim());
            }
        }
        if(directors.size() == 0) { return Constants.UNKNOWN_STR; }
        return String.join(",", directors);
    }

    @Override
    public AvroMovie createAvroMovieFromCSVLine(String[] line) {

        String id = this.generateId();
        String type = "Movie";
        String cast = this.extractCast(line[0]);
        String directors = this.extractDirectors(line[1]);
        String genres = this.extractGenres(line[6]);
        String description = this.extractDescription(line[12]);
        String countries = this.extractCountries(line[16]);
        String dateAdded = this.generateDateAdded();
        Integer releaseYear = this.extractReleaseYear(line[17]);
        String rating = Constants.UNKNOWN_STR;
        Integer duration = this.extractDuration(line[19]);
        String title = this.extractTitle(line[23]);

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


    public static void main(String[] args) {

        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();

        Context context = new Context();
        try (Reader creditsReader = Files.newBufferedReader(Paths.get(context.getEnvVar("DATA_DIR") + "/credits.csv"));
             Reader metadataReader = Files.newBufferedReader(Paths.get(context.getEnvVar("DATA_DIR") + "/movies_metadata.csv"));
             CSVReader creditsCsvReader = new CSVReader(creditsReader);
             CSVReader metadataCsvReader = new CSVReader(metadataReader)
        ) {
            // Skip the header
            creditsCsvReader.readNext();
            metadataCsvReader.readNext();

            String[] creditsLine;
            String[] metadataLine;
            while ((creditsLine = creditsCsvReader.readNext()) != null && (metadataLine = metadataCsvReader.readNext()) != null) {
                AvroMovie movie = tamb.createAvroMovieFromCSVLine(Utils.concatWithCollection(creditsLine, metadataLine));
                System.out.println(movie.getId());
                System.out.println(movie.getType());
                System.out.println(movie.getTitle());
                System.out.println(movie.getDirector());
                System.out.println(movie.getCast());
                System.out.println(movie.getCountry());
                System.out.println(movie.getDateAdded());
                System.out.println(movie.getReleaseYear());
                System.out.println(movie.getRating());
                System.out.println(movie.getDuration());
                System.out.println(movie.getGenres());
                System.out.println(movie.getDescription());
                Thread.sleep(5000);
            }
        } catch (IOException | CsvValidationException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
