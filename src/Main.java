import Graph.Graph;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Graph graph = Graph.createDebugGraph(false);
        ArrayList<Graph> components = graph.getComponents();
    }
}