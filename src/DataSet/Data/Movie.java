package DataSet.Data;

import java.io.Serializable;

public class Movie implements Serializable {
    public enum Type {
        TV_SHOW,
        MOVIE
    }
    String name;
    Type type;
    String director;
    public Movie(String name, Type type, String director) {
        this.name = name;
        this.type = type;
        this.director = director;
    }
}