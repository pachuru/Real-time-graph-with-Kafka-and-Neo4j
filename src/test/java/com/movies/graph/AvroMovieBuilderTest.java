package com.movies.graph;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvroMovieBuilderTest {


    @DisplayName("should create from csv line")
    public static class CreateAvroMovieFromCSVLine {

        @Test
        @DisplayName("when there's data for all fields")
        void thereIsDataForAllFields() {
            String[] csvLine = {
                    "s1",
                    "Movie",
                    "American Beauty",
                    "Sam Mendes",
                    "Kevin Spacey, Annette Bening, Thora Birch, Wes Bentley, " +
                            "Mena Suvari, Chris Cooper, Peter Gallagher, Allison Janney, " +
                            "Scott Bakula, Sam Robards",
                    "United States",
                    "January 1, 2020",
                    "1999",
                    "R",
                    "112 min",
                    "Dramas",
                    "While struggling to endure his tightly wound wife, an unfulfilling job and a surly teen, " +
                            "a man becomes obsessed with one of his daughter's friends."
            };

            AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
            AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(csvLine);
            assertNotNull(movie);
        }

        @Test
        @DisplayName("when there's no data at all")
        void theresNoDataAtAll() {
            String[] csvLine = {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
            };

            AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
            AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(csvLine);
            assertNotNull(movie);
        }

    }

    @Test
    @DisplayName("should extract duration")
    void shouldExtractDurationCorrectly() {
        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
        assert(avroMovieBuilder.extractDuration("118 min")).equals(118);
        assert(avroMovieBuilder.extractDuration("4 seasons")).equals(-1);
        assert(avroMovieBuilder.extractDuration("")).equals(-1);
    }

    @Test
    @DisplayName("should extract string value")
    void shouldExtractStringValueCorrectly() {
        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
        assert(avroMovieBuilder.extractStringValue("TV Show")).equals("TV Show");
        assert(avroMovieBuilder.extractStringValue("    TV   Show       ")).equals("TV   Show");
        assert(avroMovieBuilder.extractStringValue("")).equals("Unknown");
    }

    @Test
    @DisplayName("should curate non-empty values")
    void shouldCurateNonEmptyValues() {
        String[] csvLine = {
                "s1",
                "Movie",
                "American Beauty",
                "Sam Mendes",
                "Kevin Spacey, Annette Bening, Thora Birch, Wes Bentley, " +
                        "Mena Suvari, Chris Cooper, Peter Gallagher, Allison Janney, " +
                        "Scott Bakula, Sam Robards",
                "United States",
                "January 1, 2020",
                "1999",
                "R",
                "112 min",
                "Dramas",
                "While struggling to endure his tightly wound wife, an unfulfilling job and a surly teen, " +
                        "a man becomes obsessed with one of his daughter's friends."
        };

        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
        AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(), "Unknown");
        assert(movie.getType()).equals("Movie");
        assert(movie.getTitle()).equals("American Beauty");
        assert(movie.getDirector()).equals("Sam Mendes");
        assert(movie.getCast()).equals("Kevin Spacey, Annette Bening, Thora Birch, Wes Bentley, " +
                "Mena Suvari, Chris Cooper, Peter Gallagher, Allison Janney, " +
                "Scott Bakula, Sam Robards");
        assert(movie.getCountry()).equals("United States");
        assert(movie.getDateAdded()).equals(LocalDate.parse("2016-06-12").toString());
        assertEquals(1999, movie.getReleaseYear());
        assert(movie.getRated()).equals("R");
        assertEquals(112, movie.getDuration());
        assert(movie.getGenres()).equals("Dramas");
        assert(movie.getDescription()).equals("While struggling to endure his tightly wound wife, an unfulfilling job " +
                "and a surly teen, " +
                "a man becomes obsessed with one of his daughter's friends.");
    }

    @Test
    @DisplayName("should replace empty values")
    void shouldReplaceEmptyValues() {
        String[] csvLine = { "", "", "", "", "", "", "", "", "", "", "", ""};

        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
        AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(), "Unknown");
        assert(movie.getType()).equals("Unknown");
        assert(movie.getTitle()).equals("Unknown");
        assert(movie.getDirector()).equals("Unknown");
        assert(movie.getCast()).equals("Unknown");
        assert(movie.getCountry()).equals("Unknown");
        assert(movie.getDateAdded()).equals(LocalDate.parse("2016-06-12").toString());
        assertEquals(movie.getReleaseYear(), -1);
        assert(movie.getRated()).equals("Unknown");
        assertEquals(movie.getDuration(), -1);
        assert(movie.getGenres()).equals("Unknown");
        assert(movie.getDescription()).equals("Unknown");
    }
}