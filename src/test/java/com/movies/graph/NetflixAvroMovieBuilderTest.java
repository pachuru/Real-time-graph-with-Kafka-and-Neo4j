package com.movies.graph;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NetflixAvroMovieBuilderTest {


    @Nested
    @DisplayName("should create from csv line")
    class CreateAvroMovieFromCSVLine {

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

            NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
            AvroMovie movie = netflixAvroMovieBuilder.createAvroMovieFromCSVLine(csvLine);
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

            NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
            AvroMovie movie = netflixAvroMovieBuilder.createAvroMovieFromCSVLine(csvLine);
            assertNotNull(movie);
        }

    }

    @Nested
    @DisplayName("should extract the fields")
    class ExtractFields {
        @Test
        @DisplayName("with string value")
        void shouldExtractStringValueCorrectly() {
            NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
            assert(netflixAvroMovieBuilder.extractStringValue("TV Show")).equals("TV Show");
            assert(netflixAvroMovieBuilder.extractStringValue("    TV   Show       ")).equals("TV   Show");
            assertEquals(netflixAvroMovieBuilder.extractStringValue(""), Constants.UNKNOWN_STR);
        }

        @Test
        @DisplayName("extract duration")
        void shouldExtractDurationCorrectly() {
            NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
            assertEquals(netflixAvroMovieBuilder.extractDuration("118 min"), 118);
            assertEquals(netflixAvroMovieBuilder.extractDuration("4 seasons"), Constants.UNKNOWN_INT);
            assertEquals(netflixAvroMovieBuilder.extractDuration(""), Constants.UNKNOWN_INT);
        }

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

        NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
        AvroMovie movie = netflixAvroMovieBuilder.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(), Constants.UNKNOWN_STR);
        assert(movie.getType()).equals("Movie");
        assert(movie.getTitle()).equals("American Beauty");
        assert(movie.getDirector()).equals("Sam Mendes");
        assert(movie.getCast()).equals("Kevin Spacey, Annette Bening, Thora Birch, Wes Bentley, " +
                "Mena Suvari, Chris Cooper, Peter Gallagher, Allison Janney, " +
                "Scott Bakula, Sam Robards");
        assert(movie.getCountry()).equals("United States");
        assert(movie.getDateAdded()).equals(LocalDate.parse("2016-06-12").toString());
        assertEquals(1999, movie.getReleaseYear());
        assert(movie.getRating()).equals("R");
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

        NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
        AvroMovie movie = netflixAvroMovieBuilder.createAvroMovieFromCSVLine(csvLine);

        assertNotEquals(movie.getId(),Constants.UNKNOWN_STR);
        assertEquals(movie.getType(), Constants.UNKNOWN_STR);
        assertEquals(movie.getTitle(), Constants.UNKNOWN_STR);
        assertEquals(movie.getDirector(), Constants.UNKNOWN_STR);
        assertEquals(movie.getCast(), Constants.UNKNOWN_STR);
        assertEquals(movie.getCountry(), Constants.UNKNOWN_STR);
        assertEquals(movie.getDateAdded(), LocalDate.parse("2016-06-12").toString());
        assertEquals(movie.getReleaseYear(), Constants.UNKNOWN_INT);
        assertEquals(movie.getRating(), Constants.UNKNOWN_STR);
        assertEquals(movie.getDuration(), Constants.UNKNOWN_INT);
        assertEquals(movie.getGenres(), Constants.UNKNOWN_STR);
        assertEquals(movie.getDescription(), Constants.UNKNOWN_STR);
    }
}