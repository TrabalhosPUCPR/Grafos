import Graph.Graph;
import Graph.GraphMenu;

public class Main {
    public static void main(String[] args) {
        Graph graph = Graph.createExampleGraph(false);
        GraphMenu menu = new GraphMenu(graph);
        menu.run();
    }
}