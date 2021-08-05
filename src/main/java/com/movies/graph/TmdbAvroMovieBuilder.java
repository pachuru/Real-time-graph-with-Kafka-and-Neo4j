package com.movies.graph;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.kafka.clients.producer.ProducerRecord;

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

public class TmdbAvroMovieBuilder {


    String jsonObjectPatternStr = "\\{(.*?)\\}";
    Pattern jsonObjectPattern = Pattern.compile(jsonObjectPatternStr);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat yearFormat = new SimpleDateFormat("yyyy");

    public String extractCast(String line) {
        return extractArrayListOf("name", line);
    }

    public String extractCountries(String line) {
        return extractArrayListOf("name", line);
    }

    public String extractGenres(String line) {
        return extractArrayListOf("name", line);
    }

    public String extractArrayListOf(String attribute, String line) {
        Matcher m = this.jsonObjectPattern.matcher(line);
        ArrayList<String> cast = new ArrayList<>();
        while(m.find()) {
            JSONObject actor = new JSONObject(m.group(0));
            cast.add(actor.getString(attribute));
        }
        return String.join(",", cast).trim();
    }

    public String extractDirectors(String line) {
        Matcher m = this.jsonObjectPattern.matcher(line);
        ArrayList<String> directors = new ArrayList<>();
        while(m.find()) {
            JSONObject credit = new JSONObject(m.group(0));
            if(credit.getString("job").equals("Director")) {
                directors.add(credit.getString("name"));
            }
        }
        return String.join(",", directors).trim();
    }

    public String extractReleaseYear(String line) throws ParseException {
        return this.yearFormat.format(this.dateFormat.parse(line));
    }

    public Integer extractDuration(String line) {
        Double duration = Double.parseDouble(line);
        return duration.intValue();
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
                System.out.println(tamb.extractCast(creditsLine[0]));
                System.out.println(tamb.extractDirectors(creditsLine[1]));
                System.out.println(tamb.extractGenres(metadataLine[3])); // genres (array of json)
                System.out.println(metadataLine[9]); // overview (string)
                System.out.println(tamb.extractCountries(metadataLine[13])); // countries (array of json)
                System.out.println(tamb.extractReleaseYear(metadataLine[14])); // release date (yyyy-mm-dd)
                System.out.println(tamb.extractDuration(metadataLine[16])); // runtime (double)
                System.out.println(metadataLine[20]); // title (string)
                Thread.sleep(5000);
            }
        } catch (IOException | CsvValidationException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

}
