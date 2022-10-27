package Graph;

import java.util.*;
import Graph.Node.AdjacencyHolder;

public class Graph {
    private final LinkedHashMap<String, Node<?>> nodes;

    public Graph(){
        this.nodes = new LinkedHashMap<>();
    }

    public int verticesSize(){return this.nodes.size();}
    public int connections(){ // adiciona 1 para todas adjacencia que cada node do grafo tem
        int con = 0;
        for(Node<?> n : this.getNodes()){
            con += n.getAdjacencies().length;
        }
        return con;
    }

    public boolean add(Node<?> node){ // adiciona no grafo o node, caso ja exista, nao faz nada
        if(this.nodes.get(node.toString()) == null){
            this.nodes.put(node.toString(), node);
            return true;
        }
        return false;
    }

    public boolean contains(Node<?> node){
        for(Node<?> n : this.getNodes()){
            if(node.equals(n)){
                return true;
            }
        }
        return false;
    }

    public Node<?> getNode(Object key){
        return this.nodes.get(key.toString());
    }

    public boolean newAdjacency(Object node1, Object node2, int weight){ // pega o node1 dentro do grafo e chama a funcao que adiciona adjacencia
        if(this.nodes.get(node2.toString()) == null){ // caso o node2 nao exista so retorna
            return false;
        }
        this.nodes.get(node1.toString()).newAdjacency(this.nodes.get(node2.toString()), weight);
        return true; // verdadeiro quando foi possivel adicionar
    }
    public boolean newNonDirectedAdjacency(Object node1, Object node2, int weight){
        // chama a msm funcao em cima soq duas vezes para cada node
        return newAdjacency(node1, node2, weight) && newAdjacency(node2, node1, weight); // retorna verdadeiro se os dois tiveram sucesso
    }

    public void setNode(Node<?> node){
        this.nodes.put(node.toString(), node);
    }

    public Node<?>[] getNodes() {
        Node<?>[] nodes = new Node<?>[0];
        nodes = this.nodes.values().toArray(nodes);
        return nodes;
    }

    public void printAdjacencies(){ // pega todas as adjacencias e printa na tela
        for (Node<?> node : getNodes()) {
            System.out.print(node + ": | ");
            for (Node<?> nAdjacent : node.getAdjacencies()) {
                System.out.print(nAdjacent + " | ");
            }
            System.out.println();
        }
    }

    public List<Object> getLongestPath(Object originKey, Object destinationKey){ // chama o algoritmo de djikstra com o parametro especifio
        return this.getShortLongPath(this.getNode(originKey), this.getNode(destinationKey), false);
    }

    public List<Object> getShortestPath(Object originKey, Object destinationKey){
        return this.getShortLongPath(this.getNode(originKey), this.getNode(destinationKey), true);
    }

    private List<Object> getShortLongPath(Node<?> origin, Node<?> destination, boolean shortest){
        if(!this.search(origin.toString(), destination.toString())){ // caso nao exista conexao entre a origem e destino, retorna vazio
            return new ArrayList<>();
        }

        // instancializa todas as variaveis:
        // distancias, visitados, nodes pra visitar, node anterior
        Map<String, Double> distances = new HashMap<>();
        distances.put(origin.toString(), 0.0);

        Set<Node<?>> nodesPassed = new HashSet<>();
        List<Node<?>> nodeToVisit = new ArrayList<>();
        nodeToVisit.add(origin);

        Map<String, Node<?>> previousNode = new HashMap<>();
        while(!nodeToVisit.isEmpty()){ // enquanto a lista que guarda os nodes para visitar nao estiver vazio
            Node<?> current = getUnvisitedNodeWithMinDistance(nodeToVisit, distances); // pega o proximo node que tem a menor distancia dentro do hashmap
            Node<?>[] adjacencies = current.getAdjacencies(); // pega as adjacencias do node
            for(Node<?> n : adjacencies){ // passa por todas as adjacencias
                if(!nodesPassed.contains(n)){ // se o node adjacente ainda nao foi visitado
                    Double currentsDistance = distances.get(current.toString()); // pega a distancia do node atual
                    // soma a distancia do node atual com o peso dela com sua adjacencia, caso for nulo, ele e igual a 0, e caso for pra pegar o caminho mais longo, faz pow com -1
                    double newDistance = Math.pow((current.getWeight(n) + (currentsDistance == null ? 0 : currentsDistance)), (shortest ? 1 : -1));
                    if(!distances.containsKey(n.toString()) || distances.get(n.toString()) > newDistance){ // se o node ainda nao tem uma distancia definida, ou a nova distancia e menor q a que existe
                        distances.put(n.toString(), newDistance); // troca a distancia e o node anterior
                        previousNode.put(n.toString(), current);
                    }
                    if(!nodeToVisit.contains(n) && !n.equals(destination)){ // caso o node ainda nao foi adicionado na lista para visitar e ele nao e igual ao destino
                        nodeToVisit.add(n); // adiciona na lista para visitar
                    }
                }
            }
            nodesPassed.add(current); // adiciona o node visitado como visitado
        }
        // recria o array do caminho percorrido, pegando o node destino e indo para o node definido como anterior
        List<Object> shortestPath = new ArrayList<>();
        ArrayList<Node<?>> path = new ArrayList<>();
        path.add(destination);
        Node<?> current = previousNode.get(destination.toString());
        while(!current.equals(origin)){
            path.add(current);
            current = previousNode.get(current.toString());
        }
        path.add(origin);
        shortestPath.add(path);
        shortestPath.add(shortest ? distances.get(destination.toString()) : Math.pow(distances.get(destination.toString()), -1));
        Collections.reverse(path);
        return shortestPath;
    }

    private Node<?> getUnvisitedNodeWithMinDistance(List<Node<?>> nodesToVisit, Map<String, Double> distance){
        int lowestNodeIndex = 0;
        Double lowestValue = Double.MAX_VALUE;
        for(int i = 0; i < nodesToVisit.size(); i++){
            if(!distance.containsKey(nodesToVisit.get(i).toString()) || lowestValue > distance.get(nodesToVisit.get(i).toString())){
                lowestValue = distance.get(nodesToVisit.get(i).toString());
                lowestNodeIndex = i;
            }
        }
        return nodesToVisit.remove(lowestNodeIndex);
    }

    public boolean search(Object originKey, Object destinationKey){ // itera sobre o grafo inteiro ate encontrar o destino ou nao ter mais elementos
        BfsIterator bfs = new BfsIterator(this.getNode(originKey));
        while(bfs.ready()){
            if(bfs.next().toString().equals(destinationKey.toString())){
                return true;
            }
        }
        return false;
    }

    public List<?> adjacentNodesAtDistance(Object origin, int distance){
        return this.adjacentNodesAtDistance(this.getNode(origin.toString()), distance);
    }
    public List<?> adjacentNodesAtDistance(Node<?> origin, int distance){
        BfsIterator bfs = new BfsIterator(origin);
        List<Node<?>> nodes = new ArrayList<>();
        while(bfs.ready() && !(bfs.nextIterationLayer() == distance)){ // itera sobre o grafo ate a camada dele for igual a distancia
            bfs.next();
        }
        while (bfs.ready() && bfs.nextIterationLayer() == distance){ // enquanto a distancia for igual a distancia
            nodes.add(bfs.next()); // adiciona na lista de nodes que tao na camada
        }
        return nodes;
    }

    // KRUSKAL ALGORITHM
    private static class Edge implements Comparable<Edge>{
        private final Node<?> node1, node2;
        private final int weight;

        public Edge(Node<?> node1, Node<?> node2, int weight) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }

        public Node<?> getOriginNode() {
            return node1;
        }

        public Node<?> getDestinationNode() {
            return node2;
        }

        public int getWeight() {
            return weight;
        }

        public boolean equals(Edge edge) {
            return (edge.getOriginNode() == this.node1 || edge.getOriginNode() == this.node2) && (edge.getDestinationNode() == this.node1 || edge.getDestinationNode() == this.node2);
        }

        @Override
        public int compareTo(Edge o) {
            return this.weight - o.weight;
        }
    }

    public static Graph genMinimumSpanningTree(Graph graph){
        Node<?>[] nodes = graph.getNodes();
        ArrayList<Edge> edges = new ArrayList<>();
        // pega todas as conexoes eliminando repeticoes, se o grafo tiver nodes
        // adicionado de forma direcionada, nao vai funciona
        for(Node<?> node : nodes){
            for(AdjacencyHolder adjacentNode : node.getAdjacencyHolders()){
                Edge e = new Edge(node, adjacentNode.getNode(), adjacentNode.getWeight());
                if(!alreadyAdded(edges, e)){
                    edges.add(e);
                }
            }
        }
        // ordena o arraylist baseado no peso das arestas
        heapSortEdges(edges);

        Graph minTree = new Graph();
        HashSet<Node<?>> addedSet = new HashSet<>();
        for(Edge e : edges){
            if(addedSet.add(e.node1) || addedSet.add(e.node2)){
                e.getOriginNode().newAdjacency(e.getDestinationNode(), e.getWeight());
                e.getDestinationNode().newAdjacency(e.getOriginNode(), e.getWeight());
                minTree.add(e.getOriginNode());
                minTree.add(e.getDestinationNode());
            }
        }
        return minTree;
    }

    private static boolean alreadyAdded(ArrayList<Edge> edges, Edge edge){
        for(Edge e : edges){
            if(e.equals(edge)){
                return true;
            }
        }
        return false;
    }

    private static void heapSortEdges(ArrayList<Edge> edges){
        int n = edges.size();
        for (int i = n / 2 - 1; i >= 0; i--) heapify(edges, n, i);
        for (int i = n - 1; i > 0; i--) {
            Edge temp = edges.get(0);
            edges.set(0, edges.get(i));
            edges.set(i, temp);
            heapify(edges, i, 0);
        }
    }
    private static void heapify(ArrayList<Edge> edges, int n, int i){
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < n && edges.get(l).weight > edges.get(largest).getWeight()) largest = l;
        if (r < n && edges.get(r).weight > edges.get(largest).getWeight()) largest = r;
        if (largest != i) {
            Edge swap = edges.get(i);
            edges.set(i, edges.get(largest));
            edges.set(largest, swap);
            heapify(edges, n, largest);
        }
    }


    @Override
    public String toString() {
        return Arrays.toString(this.nodes.values().toArray());
    }

    private static void createGraph(Graph graph, Object[] keys){ // codigo so pra debuga ali em baixo
        for(Object key : keys) {
            graph.add(new Node<>(key));
        }
    }
    
    public static Graph createDebugGraph(boolean directed){
        Graph graph = new Graph();

        int distance = 2;

        Object[] keys;
        Object iteratorStart, searchOrigin, searchEnd, pathStart, pathEnd;
        if(directed){
            keys = new String[]{"s", "a", "b", "c", "d", "e"};

            createGraph(graph, keys);
            iteratorStart = "s";
            searchOrigin = "s";
            searchEnd = "e";
            pathStart = "s";
            pathEnd = "e";

            //https://www.gatevidyalay.com/wp-content/uploads/2018/03/Dijkstra-Algorithm-Problem-01.png
            graph.newAdjacency("s", "a", 1);
            graph.newAdjacency("s", "b", 5);
            graph.newAdjacency("a", "b", 2);
            graph.newAdjacency("a", "c", 2);
            graph.newAdjacency("a", "d", 1);
            graph.newAdjacency("b", "d", 2);
            graph.newAdjacency("c", "d", 3);
            graph.newAdjacency("c", "e", 1);
            graph.newAdjacency("d", "e", 2);
        }else{
            keys = new Object[] {0, 1, 2, 3, 4, 5, 6, 7, 8};

            createGraph(graph, keys);
            iteratorStart = 0;
            searchOrigin = 0;
            searchEnd = 4;
            pathStart = 0;
            pathEnd = 4;

            // https://www.geeksforgeeks.org/wp-content/uploads/Fig-11.jpg
            graph.newNonDirectedAdjacency(0, 1, 4);
            graph.newNonDirectedAdjacency(0, 7, 8);
            graph.newNonDirectedAdjacency(1, 7, 11);
            graph.newNonDirectedAdjacency(1, 2, 8);
            graph.newNonDirectedAdjacency(2, 8, 2);
            graph.newNonDirectedAdjacency(2, 3, 7);
            graph.newNonDirectedAdjacency(2, 5, 4);
            graph.newNonDirectedAdjacency(8, 7, 7);
            graph.newNonDirectedAdjacency(7, 6, 1);
            graph.newNonDirectedAdjacency(8, 6, 6);
            graph.newNonDirectedAdjacency(6, 5, 2);
            graph.newNonDirectedAdjacency(5, 3, 14);
            graph.newNonDirectedAdjacency(5, 4, 10);
            graph.newNonDirectedAdjacency(3, 4, 9);
        }

        graph.printAdjacencies();

        BfsIterator bfsIterator = new BfsIterator(graph.getNode(iteratorStart));
        DfsIterator dfsIterator = new DfsIterator(graph.getNode(iteratorStart));

        System.out.print("BFS: ");
        while(bfsIterator.ready()){
            System.out.print(bfsIterator.next() + " ");
        }
        System.out.println();
        System.out.print("DFS: ");
        while(dfsIterator.ready()){
            System.out.print(dfsIterator.next() + " ");
        }
        System.out.println();

        System.out.println(graph.search(searchOrigin, searchEnd));

        System.out.println("ShortestPath: " + graph.getShortestPath(pathStart, pathEnd));
        System.out.println("LongestPath: " + graph.getLongestPath(pathStart, pathEnd).get(0));

        System.out.println("Nodes at distance 3 from node: " + graph.adjacentNodesAtDistance(pathStart, distance));
        return graph;
    }
}
