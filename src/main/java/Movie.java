import java.lang.reflect.Array;
import java.util.ArrayList;

public class Movie {

    private Integer id;
    private String title;
    private String director;
    private ArrayList<String> cast;
    private String country;
    private Integer releaseYear;
    private Double rating;
    private Double duration;
    private ArrayList<String> genres;
    private String description;


    public Movie(Integer id, String title, String director, ArrayList<String> cast, String country, Integer releaseYear,
                 Double rating, Double duration, ArrayList<String> genres, String description) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.cast = cast;
        this.country = country;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.duration = duration;
        this.genres = genres;
        this.description = description;
    }
}
