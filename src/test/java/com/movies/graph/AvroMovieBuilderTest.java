package com.movies.graph;

import org.junit.jupiter.api.*;

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
    @DisplayName("should extract duration correctly")
    void shouldExtractDurationCorrectly() {
        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();
        assert(avroMovieBuilder.extractDuration("118 min")).equals(118);
        assert(avroMovieBuilder.extractDuration("4 seasons")).equals(-1);
        assert(avroMovieBuilder.extractDuration("")).equals(-1);
    }
}