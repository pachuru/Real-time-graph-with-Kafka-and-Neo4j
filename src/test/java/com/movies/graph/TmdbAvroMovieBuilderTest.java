package com.movies.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TmdbAvroMovieBuilderTest {

    @Test
    @DisplayName("should extract arraylist of attribute")
    void shouldExtractArrayListOf() {
        String line = "[" +
                "{'cast_id': 14, 'character': 'Woody (voice)', 'credit_id': '52fe4284c3a36847f8024f95', 'gender': 2, " +
                "'id': 31, 'name': '  Tom Hanks', 'order': 0, 'profile_path': '/pQFoyx7rp09CJTAb932F2g8Nlho.jpg'}, " +
                "{'cast_id': 15, 'character': 'Buzz Lightyear (voice)', 'credit_id': '52fe4284c3a36847f8024f99', " +
                "'gender': 2, 'id': 12898, 'name': 'Tim Allen  ', 'order': 1, 'profile_path': '/uX2xVf6pMmPepxnvFWyBtjexzgY.jpg'}, " +
                "{'cast_id': 16, 'character': 'Mr. Potato Head (voice)', 'credit_id': '52fe4284c3a36847f8024f9d', " +
                "'gender': 2, 'id': 7167, 'name': '  Don Rickles  ', 'order': 2, 'profile_path': '/h5BcaDMPRVLHLDzbQavec4xfSdt.jpg'}, " +
                "{'cast_id': 17, 'character': 'Slinky Dog (voice)', 'credit_id': '52fe4284c3a36847f8024fa1', " +
                "'gender': 2, 'id': 12899, 'name': 'Jim Varney', 'order': 3, 'profile_path': '/eIo2jVVXYgjDtaHoF19Ll9vtW7h.jpg'}, " +
                "{'cast_id': 18, 'character': 'Rex (voice)', 'credit_id': '52fe4284c3a36847f8024fa5', 'gender': 2, 'id': 12900, " +
                "'name': 'Wallace Shawn', 'order': 4, 'profile_path': '/oGE6JqPP2xH4tNORKNqxbNPYi7u.jpg'}" +
                "]";
        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        assertEquals(tamb.extractArrayListOf("name", line), "Tom Hanks,Tim Allen,Don Rickles,Jim Varney,Wallace Shawn");
    }

    @Test
    @DisplayName("should extract directors")
    void shouldExtractDirectors() {
        String line = "[" +
                "{'credit_id': '52fe4284c3a36847f8024f49', 'department': 'Directing', 'gender': 2, 'id': 7879, " +
                "'job': 'Director', 'name': '  John Lasseter  ', 'profile_path': '/7EdqiNbr4FRjIhKHyPPdFfEEEFG.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f4f', 'department': 'Writing', 'gender': 2, 'id': 12891, " +
                "'job': 'Screenplay', 'name': 'Joss Whedon', 'profile_path': '/dTiVsuaTVTeGmvkhcyJvKp2A5kr.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f55', 'department': 'Writing', 'gender': 2, 'id': 7, " +
                "'job': 'Screenplay', 'name': 'Andrew Stanton', 'profile_path': '/pvQWsu0qc8JFQhMVJkTHuexUAa1.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f5b', 'department': 'Writing', 'gender': 2, 'id': 12892, " +
                "'job': 'Director', 'name': ' Joel Cohen   ', 'profile_path': '/dAubAiZcvKFbboWlj7oXOkZnTSu.jpg'}" +
                "]";
        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        assertEquals(tamb.extractDirectors(line), "John Lasseter,Joel Cohen");
    }

    @Test
    @DisplayName("should extract duration")
    void shouldExtractDuration() {
        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        assertEquals(tamb.extractDuration("81.0"), 81);
        assertEquals(tamb.extractDuration(""), -1);
    }

    @Test
    @DisplayName("should extract release year")
    void shouldExtractReleaseYear() {
        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        assertEquals(tamb.extractReleaseYear("1995-10-30"), 1995);
        assertEquals(tamb.extractReleaseYear("1835-3-30"), 1835);
        assertEquals(tamb.extractReleaseYear("2060-02-31"), 2060);
    }

    @Test
    @DisplayName("should curate non-empty values")
    void shouldCurateNonEmptyValues() {
        String[] csvLine = new String[27];
        for(int i = 0; i < 27; i++)
            csvLine[i] = "";

        String cast = "[" +
                "{'cast_id': 14, 'character': 'Woody (voice)', 'credit_id': '52fe4284c3a36847f8024f95', 'gender': 2, " +
                "'id': 31, 'name': '  Tom Hanks  ', 'order': 0, 'profile_path': '/pQFoyx7rp09CJTAb932F2g8Nlho.jpg'}, " +
                "{'cast_id': 15, 'character': 'Buzz Lightyear (voice)', 'credit_id': '52fe4284c3a36847f8024f99', " +
                "'gender': 2, 'id': 12898, 'name': 'Tim Allen', 'order': 1, 'profile_path': '/uX2xVf6pMmPepxnvFWyBtjexzgY.jpg'}, " +
                "{'cast_id': 16, 'character': 'Mr. Potato Head (voice)', 'credit_id': '52fe4284c3a36847f8024f9d', " +
                "'gender': 2, 'id': 7167, 'name': 'Don Rickles', 'order': 2, 'profile_path': '/h5BcaDMPRVLHLDzbQavec4xfSdt.jpg'}, " +
                "{'cast_id': 17, 'character': 'Slinky Dog (voice)', 'credit_id': '52fe4284c3a36847f8024fa1', " +
                "'gender': 2, 'id': 12899, 'name': 'Jim Varney', 'order': 3, 'profile_path': '/eIo2jVVXYgjDtaHoF19Ll9vtW7h.jpg'}, " +
                "{'cast_id': 18, 'character': 'Rex (voice)', 'credit_id': '52fe4284c3a36847f8024fa5', 'gender': 2, 'id': 12900, " +
                "'name': 'Wallace Shawn', 'order': 4, 'profile_path': '/oGE6JqPP2xH4tNORKNqxbNPYi7u.jpg'}" +
                "]";
        csvLine[0] = cast;

        String credits = "[" +
                "{'credit_id': '52fe4284c3a36847f8024f49', 'department': 'Directing', 'gender': 2, 'id': 7879, " +
                "'job': 'Director', 'name': ' John Lasseter', 'profile_path': '/7EdqiNbr4FRjIhKHyPPdFfEEEFG.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f4f', 'department': 'Writing', 'gender': 2, 'id': 12891, " +
                "'job': 'Screenplay', 'name': 'Joss Whedon', 'profile_path': '/dTiVsuaTVTeGmvkhcyJvKp2A5kr.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f55', 'department': 'Writing', 'gender': 2, 'id': 7, " +
                "'job': 'Screenplay', 'name': 'Andrew Stanton', 'profile_path': '/pvQWsu0qc8JFQhMVJkTHuexUAa1.jpg'}, " +
                "{'credit_id': '52fe4284c3a36847f8024f5b', 'department': 'Writing', 'gender': 2, 'id': 12892, " +
                "'job': 'Director', 'name': 'Joel Cohen   ', 'profile_path': '/dAubAiZcvKFbboWlj7oXOkZnTSu.jpg'}" +
                "]";
        csvLine[1] = credits;

        String genres = "[{'id': 16, 'name': 'Animation'}, {'id': 35, 'name': 'Comedy'}, {'id': 10751, 'name': 'Family'}]\n";
        csvLine[6] = genres;

        String description = "Led by Woody, Andy's toys live happily in his room until Andy's birthday brings Buzz Lightyear onto the scene.";
        csvLine[12] = description;

        String countries = "[{'iso_3166_1': 'US', 'name': 'United States of America'}, " +
                "{'iso_3166_1': 'BR', 'name': 'Brazil'}]";
        csvLine[16] = countries;

        String releaseDate = "1995-10-30";
        csvLine[17] = releaseDate;

        String duration = "81.0";
        csvLine[19] = duration;

        String title = "Toy Story";
        csvLine[23] = title;


        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        AvroMovie movie = tamb.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(), "Unknown");
        assertEquals(movie.getType(), "Movie");
        assertEquals(movie.getTitle(), "Toy Story");
        assertEquals(movie.getDirector(), "John Lasseter,Joel Cohen");
        assertEquals(movie.getCast(), "Tom Hanks,Tim Allen,Don Rickles,Jim Varney,Wallace Shawn");
        assertEquals(movie.getCountry(), "United States of America,Brazil");
        assertEquals(movie.getDateAdded(), LocalDate.parse("2016-06-12").toString());
        assertEquals(movie.getReleaseYear(), 1995);
        assertEquals(movie.getRating(), "Unknown");
        assertEquals(movie.getDuration(), 81);
        assertEquals(movie.getGenres(), "Animation,Comedy,Family");
        assertEquals(movie.getDescription(), description);
    }

    @Test
    @DisplayName("should replace empty values")
    void shouldReplaceEmptyValues() {
        String[] csvLine = new String[27];
        for(int i = 0; i < 27; i++)
            csvLine[i] = "";

        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        AvroMovie movie = tamb.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(), "Unknown");
        assertEquals(movie.getType(), "Movie");
        assertEquals(movie.getTitle(), "Unknown");
        assertEquals(movie.getDirector(), "Unknown");
        assertEquals(movie.getCast(), "Unknown");
        assertEquals(movie.getCountry(), "Unknown");
        assertEquals(movie.getDateAdded(), LocalDate.parse("2016-06-12").toString());
        assertEquals(movie.getReleaseYear(), -1);
        assertEquals(movie.getRating(), "Unknown");
        assertEquals(movie.getDuration(), -1);
        assertEquals(movie.getGenres(), "Unknown");
        assertEquals(movie.getDescription(), "Unknown");
    }
}