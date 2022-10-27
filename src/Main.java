import Graph.Graph;

public class Main {
    public static void main(String[] args) {
        Graph graph = Graph.createDebugGraph(false);

        Graph tree = Graph.genMinimumSpanningTree(graph);
    }
}