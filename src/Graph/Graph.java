package Graph;

import java.util.*;
import Graph.Node.AdjacencyHolder;

public class Graph {
    private final LinkedHashMap<String, Node<?>> nodes;
    private final boolean directed;

    public Graph(){
        this.nodes = new LinkedHashMap<>();
        this.directed = false;
    }
    public Graph(boolean directed){
        this.nodes = new LinkedHashMap<>();
        this.directed = directed;
    }

    public int size(){return this.nodes.size();}
    public int edgesCount(){
        int con = 0;
        for(Node<?> n : this.getNodes()){
            con += n.getAdjacencies().length;
        }
        return con;
    }
    public boolean add(Object key){
        return add(new Node<>(key), false);
    }
    public boolean add(Object key, boolean override){
        return add(new Node<>(key), override);
    }
    private boolean add(Node<?> node, boolean override){
        if(override || !this.contains(node.toString())){
            this.nodes.put(node.toString(), node);
            return true;
        }
        return false;
    }

    public boolean remove(Object key){
        if(contains(key)){
            for(Node<?> n : getNodes()){
                n.removeAdjacency(key);
            }
            return this.nodes.remove(key.toString()) != null;
        }
        return false;
    }

    public boolean contains(Object nodeKey){
        return this.nodes.get(nodeKey.toString()) != null;
    }

    public Node<?> getNode(Object key){
        return this.nodes.get(key.toString());
    }

    public boolean newAdjacency(Object node1, Object node2, int weight){ // pega o node1 dentro do grafo e chama a funcao que adiciona adjacencia
        if(!this.contains(node1.toString())){
            this.add(new Node<>(node1));
        }
        if(!this.contains(node2.toString())){
            this.add(new Node<>(node2));
        }
        boolean added = getNode(node1).newAdjacency(getNode(node2), weight);
        if(this.directed || !added){
            return added;
        }
        return getNode(node2).newAdjacency(getNode(node1), weight);
    }

    public boolean removeAdjacency(Object node1, Object node2){
        boolean check = this.getNode(node1).removeAdjacency(node2);
        if(directed){
            return check;
        }
        return check && this.getNode(node2).removeAdjacency(node1);
    }

    public Node<?>[] getNodes() {
        Node<?>[] nodes = new Node<?>[0];
        nodes = this.nodes.values().toArray(nodes);
        return nodes;
    }
    public List<Node<?>> getNodesList() {
        return new ArrayList<>(Arrays.asList(getNodes()));
    }

    public Node<?>[] getBfsTraversal(){
        List<Node<?>> nodes = new ArrayList<>(this.nodes.size());
        BfsIterator bfsIterator = new BfsIterator(this.getNodes()[0]);
        while (bfsIterator.ready()){
            nodes.add(bfsIterator.next());
        }
        return nodes.toArray(new Node<?>[0]);
    }

    public Node<?>[] getDfsTraversal(){
        List<Node<?>> nodes = new ArrayList<>(this.nodes.size());
        DfsIterator dfsIterator = new DfsIterator(this.getNodes()[0]);
        while (dfsIterator.ready()){
            nodes.add(dfsIterator.next());
        }
        return nodes.toArray(new Node<?>[0]);
    }

    public List<Object> getLongestPath(Object originKey, Object destinationKey){ // chama o algoritmo de djikstra com o parametro especifio
        return this.getShortOrLongPath(this.getNode(originKey), this.getNode(destinationKey), false);
    }

    public List<Object> getShortestPath(Object originKey, Object destinationKey){
        return this.getShortOrLongPath(this.getNode(originKey), this.getNode(destinationKey), true);
    }

    private List<Object> getShortOrLongPath(Node<?> origin, Node<?> destination, boolean shortest){
        if(!this.bfsSearch(origin.toString(), destination.toString())){ // caso nao exista conexao entre a origem e destino, retorna vazio
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
        Collections.reverse(path);
        shortestPath.add(path);
        shortestPath.add(shortest ? distances.get(destination.toString()) : Math.pow(distances.get(destination.toString()), -1));
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

    public boolean bfsSearch(Object originKey, Object destinationKey){ // itera sobre o grafo inteiro ate encontrar o destino ou nao ter mais elementos
        BfsIterator bfs = new BfsIterator(this.getNode(originKey));
        while(bfs.ready()){
            if(bfs.next().toString().equals(destinationKey.toString())){
                return true;
            }
        }
        return false;
    }
    public boolean dfsSearch(Object originKey, Object destinationKey){ // itera sobre o grafo inteiro ate encontrar o destino ou nao ter mais elementos
        DfsIterator dfs = new DfsIterator(this.getNode(originKey));
        while(dfs.ready()){
            if(dfs.next().toString().equals(destinationKey.toString())){
                return true;
            }
        }
        return false;
    }

    public List<?> getNodesAtDistance(Object origin, int distance){
        return this.getNodesAtDistance(this.getNode(origin.toString()), distance);
    }
    public List<?> getNodesAtDistance(Node<?> origin, int distance){
        BfsIterator bfs = new BfsIterator(origin);
        List<Node<?>> nodes = new ArrayList<>();
        while(bfs.ready() && !(bfs.nextIterationLayer() == distance)){ // itera sobre o grafo ate a camada dele for igual a distancia
            bfs.next();
        }
        nodes.add(bfs.next());
        while (bfs.ready() && bfs.nextIterationLayer() == distance){ // enquanto a distancia for igual a distancia
            nodes.add(bfs.next()); // adiciona na lista de nodes que tao na camada
        }
        return nodes;
    }

    // KRUSKAL ALGORITHM
    private record Edge(Node<?> node1, Node<?> node2, int weight) implements Comparable<Edge> {
        @Override
        public boolean equals(Object obj) {
            if(obj.getClass() == this.getClass()){
                return obj.toString().equals(this.toString()) || obj.toString().equals(node2.toString() + node1.toString() + weight);
            }
            return false;
        }
        @Override
        public String toString() {
            if(node1.toString().compareTo(node2().toString()) < 0){
                return node1.toString() + node2.toString() + weight;
            }
            return node2.toString() + node1.toString() + weight;
        }
        @Override
        public int compareTo(Edge edge) {
            return this.weight - edge.weight;
        }
    }
    public Graph genMinimumSpanningTree(){
        return Graph.genMinimumSpanningTree(this);
    }
    public static Graph genMinimumSpanningTree(Graph graph){
        Node<?>[] nodes = graph.getNodes();
        HashSet<Edge> edgesSet = new HashSet<>();
        // pega todas as conexoes eliminando repeticoes, se o grafo tiver nodes
        // adicionado de forma direcionada, nao vai funciona
        System.out.println(graph.edgesCount());
        for(Node<?> node : nodes){
            for(AdjacencyHolder adjacentNode : node.getAdjacencyHolders()){
                Edge e = new Edge(node, adjacentNode.getNode(), adjacentNode.getWeight());
                System.out.println(edgesSet.add(e));
            }
        }
        // ordena o arraylist baseado no peso das arestas
        Edge[] edges = edgesSet.toArray(new Edge[0]);
        Arrays.sort(edges);
        Graph minTree = new Graph(graph.directed);
        for(Edge e : edges){
            Node<?> originNode = new Node<>(e.node1.getValue());
            Node<?> destinationNode = new Node<>(e.node2.getValue());
            minTree.add(originNode);
            minTree.add(destinationNode);
            // SE ja tiver uma conexao entre o node de origem e o de destino, pula pra prox iteracao
            if(!minTree.bfsSearch(originNode, destinationNode)){ // o search faz bfs na arvore e retorna verdadeiro qnd encontra o destino
                minTree.newAdjacency(originNode, destinationNode, e.weight());
            }
            if(minTree.size() == nodes.length && minTree.size() == minTree.edgesCount()){
                break;
            }
        }
        return minTree;
    }

    public ArrayList<Graph> getComponents(){
        ArrayList<Graph> components = new ArrayList<>();
        LinkedList<Node<?>> nodesList = new LinkedList<>(this.getNodesList());
        while(!nodesList.isEmpty()){
            Graph component = getComponent(nodesList.getFirst());
            components.add(component);
            for(Node<?> node : component.getNodes()){
                nodesList.remove(node);
            }
        }
        return components;
    }

    private Graph getComponent(Node<?> origin){
        Graph component = new Graph(this.directed);
        BfsIterator bfs = new BfsIterator(origin);
        component.add(origin);
        while (bfs.ready()){
            component.add(bfs.next());
        }
        return component;
    }

    public boolean isClique(Object[] nodeKeys){
        return isClique(getNodes(nodeKeys));
    }

    public boolean isClique(Node<?>[] nodes){
        Queue<Node<?>> queue = new LinkedList<>(Arrays.asList(nodes));
        while (!queue.isEmpty()){
            Node<?> n = queue.poll();
            int connected = 0;
            for(Node<?> node : queue){
                if(n.getAdjacency(node.toString()) != null){
                    connected++;
                }
            }
            if(!(connected == queue.size())){
                return false;
            }
        }
        return true;
    }
    private Node<?>[] getNodes(Object[] nodeKeys) {
        Node<?>[] nodes = new Node<?>[nodeKeys.length];
        for(int i = 0; i < nodeKeys.length; i++){
            if(getNode(nodeKeys[i]) != null){
                nodes[i] = getNode(nodeKeys[i]);
            }
        }
        return nodes;
    }

    public boolean isMaximalClique(Object[] nodeKeys){
        return isMaximalClique(getNodes(nodeKeys));
    }
    public boolean isMaximalClique(Node<?>[] nodes){
        if(isClique(nodes)){
            HashSet<Node<?>> checkedNodes = new HashSet<>(Arrays.asList(nodes));
            for(Node<?> n : nodes){
                ArrayList<Node<?>> adjs = new ArrayList<>(Arrays.asList(n.getAdjacencies()));
                adjs.removeAll(checkedNodes);
                for(Node<?> adj : adjs){
                    checkedNodes.add(adj);
                    Node<?>[] maxCliqueCheck = Arrays.copyOf(nodes, nodes.length+1);
                    maxCliqueCheck[nodes.length] = adj;
                    if(isClique(maxCliqueCheck)){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.nodes.values().toArray());
    }

    public static Graph createExampleGraph(boolean directed){
        Graph graph = new Graph(directed);

        Object[] keys;
        keys = new Object[]{1, 2, 3, 4, 5, 6, 7, 8};

        for(Object key : keys) {
            graph.add(key);
        }

        //https://media.geeksforgeeks.org/wp-content/uploads/graphhh.png
        graph.newAdjacency(0, 1, 4);
        graph.newAdjacency(0, 7, 8);
        graph.newAdjacency(1, 2, 8);
        graph.newAdjacency(1, 7, 11);
        graph.newAdjacency(2, 8, 2);
        graph.newAdjacency(2, 3, 7);
        graph.newAdjacency(2, 5, 4);
        graph.newAdjacency(3, 4, 9);
        graph.newAdjacency(3, 5, 14);
        graph.newAdjacency(4, 5, 10);
        graph.newAdjacency(5, 6, 2);
        graph.newAdjacency(6, 7, 1);
        graph.newAdjacency(6, 8, 6);
        graph.newAdjacency(7, 8, 7);

        return graph;
    }
}
