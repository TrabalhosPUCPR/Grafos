import Graph.Graph;
import Graph.GraphMenu;

public class Main {
    public static void main(String[] args) {
        Graph graph = Graph.loadFromFile("src/Graph/SavedGraphs/Graph4.txt.txt");
        TestClass testClass = new TestClass(1, 2, "aaaa");
        TestClass2 testClass2 = new TestClass2();
        TestClass2 testClass3 = new TestClass2();

    }
}