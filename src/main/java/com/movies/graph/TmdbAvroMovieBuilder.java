package com.movies.graph;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        if(line == "") { return -1; }
        try {
            return Integer.parseInt(this.yearFormat.format(this.dateFormat.parse(line)));
        }catch(ParseException e){
            System.out.println("Error while extracting release year");
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String extractRating(String line) {
        return this.extractStringValue(line);
    }

    @Override
    public Integer extractDuration(String line) {
        if(line.equals("")) { return -1; }
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
        if(elements.size() == 0) { return "Unknown"; }
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
        if(directors.size() == 0) { return "Unknown"; }
        return String.join(",", directors);
    }

    @Override
    public AvroMovie createAvroMovieFromCSVLine(String[] line) {

        String id = this.generateId();
        String type = "Movie";
        String cast = this.extractCast(line[0]);
        String directors = this.extractDirectors(line[1]);
        String genres = this.extractGenres(line[3]);
        String description = this.extractDescription(line[12]);
        String countries = this.extractCountries(line[16]);
        String dateAdded = this.generateDateAdded();
        Integer releaseYear = this.extractReleaseYear(line[17]);
        String rating = "Unknown";
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
                System.out.println("Credit line length: " + creditsLine.length);
                System.out.println("Metadata line length: " + metadataLine.length);
                System.out.println(creditsLine[0]);
                System.out.println(creditsLine[1]);
                System.out.println(metadataLine[3]); // genres (array of json)
                System.out.println(metadataLine[9]); // overview (string)
                System.out.println(metadataLine[13]); // countries (array of json)
                System.out.println(metadataLine[14]); // release date (yyyy-mm-dd)
                System.out.println(metadataLine[16]); // runtime (double)
                System.out.println(metadataLine[20]); // title (string)
                Thread.sleep(5000);
            }
        } catch (IOException | CsvValidationException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
