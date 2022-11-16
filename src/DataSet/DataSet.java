package DataSet;

import DataSet.Data.Actor;
import DataSet.Data.Movie;
import Graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import Graph.GraphMenu;

public class DataSet {
    private final Graph graph = new Graph();
    private final File dataFile;
    private final String datasetGraphs = "src/DataSet/DataSetGraphs";
    public DataSet(String location){
        this.dataFile = new File(location);
        loadActors();
    }
    private void loadActors(){
        String lineString = "";
        int line = 0;
        try {
            Scanner scanner =  new Scanner(this.dataFile);
            scanner.nextLine(); // skip first line
            LinkedHashMap<Integer, String> errors = new LinkedHashMap<>();
            line = 0;
            while (scanner.hasNextLine()){
                line++;
                lineString = scanner.nextLine();
                List<Actor> actors = getActorsInLine(lineString);
                if(actors == null){
                    errors.put(line, lineString);
                    continue;
                }
                Queue<Actor> queue = new LinkedList<>(actors);
                while (!queue.isEmpty()){
                    Actor actor = queue.poll();
                    if(actor != null){
                        for(Actor a : queue){
                            Actor savedA = graph.get(a);
                            if(savedA != null){
                                savedA.addMovies(a.getMovies());
                            }
                            if(!graph.newAdjacency(actor, a, 1)){ // adiciona adjacencia entre os atores, caso nao seja possivel (ja estao adjacentes)
                                graph.setNewWeight(actor, a, graph.getWeight(actor, a) + 1); // incrementa 1 ao peso da aresta
                            }
                        }
                    }
                }
                System.out.print(line + "...\r");
            }
            System.out.println();
            System.out.println("Finished reading " + line + " lines!");
            if(errors.size() > 0) System.err.println("Found " + errors.size() + " errors in lines: ");
            else System.out.println();
            for(int i : errors.keySet()){
                System.err.println("Line " + i + ": " + errors.get(i));
            }
            Thread.sleep(1000);
        } catch (FileNotFoundException | InterruptedException e) {
            System.err.println("Erro irrecuperavel na linha + " + line + "; conteudo: " + lineString);
            throw new RuntimeException(e);
        }
    }

    private static List<Actor> getActorsInLine(String line){
        try {
            if(!line.startsWith("s")){
                throw new RuntimeException();
            }
            Set<Actor> actors = new HashSet<>(); // set pq a msm linha pd sem querer colocar o msm nome duas vezes, dai ja elimina
            line = skipCommas(line, 1);
            Movie.Type movieType;
            if(line.startsWith("M")) movieType = Movie.Type.MOVIE;
            else movieType = Movie.Type.TV_SHOW;
            line = skipCommas(line, 1);
            String name = getFullColString(line);
            line = skipCommas(line, 1);
            Movie movie = new Movie(name, movieType, getFullColString(line));
            line = skipCommas(line, 1);
            int endingIndex = 0;
            if(line.charAt(endingIndex) == '\"'){
                endingIndex++;
                while (line.charAt(endingIndex) != '\"'){
                    endingIndex++;
                }
                for(String actor : line.substring(0, endingIndex).replace("\"", "").split(", ")){
                    if(!actor.isEmpty()){
                        Actor a = new Actor(actor);
                        a.addMovie(movie);
                        actors.add(a);
                    }
                }
            }else if(line.charAt(endingIndex) != ','){
                while (line.charAt(endingIndex) != ','){
                    endingIndex++;
                }
                String actor = line.substring(0, endingIndex);
                if(!actor.isEmpty()){
                    Actor a = new Actor(actor);
                    a.addMovie(movie);
                    actors.add(a);
                }
            }
            return actors.stream().toList();
        }catch (Exception e){
            //System.err.println("Invalid input: " + line + " - Line " + lineNumber);
        }
        return null;
    }

    public void openGraphMenu(){
        GraphMenu menu = new GraphMenu(graph);
        menu.setNewSaveLocation(datasetGraphs);
        menu.run();
    }

    private static String skipCommas(String line, int toSkip){
        int startingIndex = 0;
        int commaCounter = 0;
        while (commaCounter < toSkip){
            if (line.charAt(startingIndex) == ',') commaCounter++;
            else if(line.charAt(startingIndex) == '\"') {
                while (line.charAt(startingIndex) != '\"'){
                    startingIndex++;
                }
                startingIndex++;
            }
            startingIndex++;
        }
        return line.substring(startingIndex);
    }

    private static String getFullColString(String line){
        int endingIndex = 0;
        while (line.charAt(endingIndex) != ','){
            if(line.charAt(endingIndex) == '\"') {
                while (line.charAt(endingIndex) != '\"'){
                    endingIndex++;
                }
            }
            endingIndex++;
        }
        return line.substring(0, endingIndex);
    }
}