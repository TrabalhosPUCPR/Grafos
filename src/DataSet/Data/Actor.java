package DataSet.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Actor implements Serializable {
    String name;
    Set<Movie> movies = new HashSet<>();

    public Actor(String name) {
        this.name = name;
    }

    public void addMovie(Movie movie){
        this.movies.add(movie);
    }
    public void addMovies(Collection<? extends Movie> movies){
        this.movies.addAll(movies);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }
}