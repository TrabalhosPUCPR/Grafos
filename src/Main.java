import DataSet.DataSet;
import Graph.Graph;
import Graph.GraphMenu;

public class Main {
    public static void main(String[] args) {
        DataSet dataSet = new DataSet("src/netflix_titles.csv");
        dataSet.openGraphMenu();
    }
}