package Graph;

import java.io.*;
import java.util.*;
import Graph.Node.AdjacencyHolder;

public class Graph {
    private final LinkedHashMap<String, Node<?>> nodes;
    private boolean directed;

    public Graph(){
        this.nodes = new LinkedHashMap<>();
        this.directed = false;
    }
    public Graph(boolean directed){
        this.nodes = new LinkedHashMap<>();
        this.directed = directed;
    }
    public Graph(Graph graph){
        this.nodes = new LinkedHashMap<>();
        for(Node<?> node : graph.getNodes()){
            this.nodes.put(node.toString(), new Node<>((Serializable) node));
        }
        this.directed = false;
    }

    public int size(){return this.nodes.size();}
    public int edgesCount(){
        int con = 0;
        for(Node<?> n : this.getNodes()){
            con += n.getAdjacencies().length;
        }
        return directed ? con : con / 2;
    }
    public boolean add(Object key){
        isSerializable(key);
        return add(new Node<>((Serializable)key), false);
    }
    public boolean add(Object key, boolean override){
        isSerializable(key);
        return add(new Node<>((Serializable)key), override);
    }
    private boolean add(Node<?> node, boolean override){
        if(override || !this.contains(node.toString())){
            this.nodes.put(node.toString(), node);
            return true;
        }
        return false;
    }

    private void isSerializable(Object key) {
        if(!(key instanceof Serializable)){
            System.err.println("All objects in graph must implement the Serializable interface");
            throw new RuntimeException();
        }
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
    public boolean setNewWeight(Object node1, Object node2, int newWeight){
        if(getNodeClass(node1).getAdjacency(node2) == null){
            return false;
        }
        getNodeClass(node1).getAdjacencyHolder(node2).setWeight(newWeight);
        if(!directed){
            getNodeClass(node2).getAdjacencyHolder(node1).setWeight(newWeight);
        }
        return true;
    }
    public int getWeight(Object node1, Object node2){
        return getNodeClass(node1).getAdjacencyHolder(node2).getWeight();
    }
    public Object getHighestNOfConnections(){
        int n = 0;
        Object value = null;
        for(Node<?> node : this.getNodes()){
            if(node.getAdjacencies().length > n){
                n = node.getAdjacencies().length;
                value = node.getValue();
            }
        }
        return value;
    }
    public Object getLowestNOfConnections(){
        int n = Integer.MAX_VALUE;
        Object value = null;
        for(Node<?> node : this.getNodes()){
            if(node.getAdjacencies().length < n){
                n = node.getAdjacencies().length;
                value = node.getValue();
            }
        }
        return value;
    }

    public boolean contains(Object nodeKey){
        return this.nodes.get(nodeKey.toString()) != null;
    }

    protected Node<?> getNodeClass(Object key){
        return this.nodes.get(key.toString());
    }
    public <T> T get(Object key){
        try{
            return (T) getNodeClass(key).getValue();
        }catch (Exception e){
            return null;
        }
    }

    public List<?> getAdjacencies(Object node){
        List<Object> values = new ArrayList<>();
        for(Node<?> value : getNodeClass(node).getAdjacencies()){
            values.add(value.getValue());
        }
        return values;
    }


    public boolean isDirected(){
        return directed;
    }
    public boolean newAdjacency(Object node1, Object node2, int weight){ // pega o node1 dentro do grafo e chama a funcao que adiciona adjacencia
        if(!this.contains(node1.toString())){
            this.add(node1);
        }
        if(!this.contains(node2.toString())){
            this.add(node2);
        }
        boolean added = getNodeClass(node1).newAdjacency(getNodeClass(node2), weight);
        if(this.directed || !added){
            return added;
        }
        return getNodeClass(node2).newAdjacency(getNodeClass(node1), weight);
    }

    public boolean removeAdjacency(Object node1, Object node2){
        boolean check = this.getNodeClass(node1).removeAdjacency(node2);
        if(directed){
            return check;
        }
        return check && this.getNodeClass(node2).removeAdjacency(node1);
    }

    protected Node<?>[] getNodes() {
        Node<?>[] nodes = new Node<?>[0];
        nodes = this.nodes.values().toArray(nodes);
        return nodes;
    }
    protected List<Node<?>> getNodesList() {
        return new ArrayList<>(Arrays.asList(getNodes()));
    }
    public List<?> toList(){
        List<Serializable> list = new ArrayList<>();
        for(Node<?> node : getNodes()){
            list.add(node.getValue());
        }
        return list;
    }

    private List<?> toValuesList(Node<?>[] array){
        List<Serializable> list = new ArrayList<>();
        for(Node<?> node : array){
            list.add(node.getValue());
        }
        return list;
    }
    private List<?> toValuesList(List<Node<?>> list){
        List<Serializable> l = new ArrayList<>();
        for(Node<?> node : list){
            l.add(node.getValue());
        }
        return l;
    }

    protected Node<?>[] getNodeBfsTraversal(){
        List<Node<?>> nodes = new ArrayList<>(this.nodes.size());
        BfsIterator bfsIterator = new BfsIterator(this.getNodes()[0]);
        while (bfsIterator.ready()){
            nodes.add(bfsIterator.next());
        }
        return nodes.toArray(new Node<?>[0]);
    }
    public List<?> getBfsTraversal(){
        return toValuesList(getNodeBfsTraversal());
    }

    protected Node<?>[] getNodeDfsTraversal(){
        List<Node<?>> nodes = new ArrayList<>(this.nodes.size());
        DfsIterator dfsIterator = new DfsIterator(this.getNodes()[0]);
        while (dfsIterator.ready()){
            nodes.add(dfsIterator.next());
        }
        return nodes.toArray(new Node<?>[0]);
    }
    public List<?> getDfsTraversal(){
        return toValuesList(getNodeDfsTraversal());
    }

    public List<Node<?>> getLongestPath(Object originKey, Object destinationKey){ // chama o algoritmo de djikstra com o parametro especifio
        return this.getShortOrLongPath(this.getNodeClass(originKey), this.getNodeClass(destinationKey), false);
    }

    public List<?> getShortestPath(Object originKey, Object destinationKey){
        return toValuesList(this.getShortOrLongPath(this.getNodeClass(originKey), this.getNodeClass(destinationKey), true));
    }
    public ArrayList<ArrayList<Node<?>>> getAllShortestPaths(Object originKey, Object destinationKey){
        return getAllShortestPaths(getNodeClass(originKey), getNodeClass(destinationKey));
    }

    private ArrayList<ArrayList<Node<?>>> getAllShortestPaths(Node<?> origin, Node<?> destination){
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

        Map<String, LinkedList<Node<?>>> previousNode = new HashMap<>();
        while(!nodeToVisit.isEmpty()){ // enquanto a lista que guarda os nodes para visitar nao estiver vazio
            Node<?> current = getUnvisitedNodeWithMinDistance(nodeToVisit, distances); // pega o proximo node que tem a menor distancia dentro do hashmap
            Node<?>[] adjacencies = current.getAdjacencies(); // pega as adjacencias do node
            for(Node<?> n : adjacencies){ // passa por todas as adjacencias
                if(!nodesPassed.contains(n)){ // se o node adjacente ainda nao foi visitado
                    Double currentsDistance = distances.get(current.toString()); // pega a distancia do node atual
                    // soma a distancia do node atual com o peso dela com sua adjacencia, caso for nulo, ele e igual a 0, e caso for pra pegar o caminho mais longo, faz pow com -1
                    double newDistance = current.getWeight(n) + (currentsDistance == null ? 0 : currentsDistance);
                    if(!distances.containsKey(n.toString()) || distances.get(n.toString()) > newDistance){ // se o node ainda nao tem uma distancia definida, ou a nova distancia e menor q a que existe
                        distances.put(n.toString(), newDistance); // troca a distancia e o node anterior
                        LinkedList<Node<?>> list = new LinkedList<>();
                        list.add(current);
                        previousNode.put(n.toString(), list);
                    }else if(!distances.containsKey(n.toString()) || distances.get(n.toString()) == newDistance){
                        distances.put(n.toString(), newDistance);
                        previousNode.get(n.toString()).add(current);
                    }
                    if(!nodeToVisit.contains(n) && !n.equals(destination)){ // caso o node ainda nao foi adicionado na lista para visitar e ele nao e igual ao destino
                        nodeToVisit.add(n); // adiciona na lista para visitar
                    }
                }
            }
            nodesPassed.add(current); // adiciona o node visitado como visitado
        }
        // recria o array do caminho percorrido, pegando o node destino e indo para o node definido como anterior
        return recreateAllShortPaths(origin, destination, previousNode, distances);
    }
    private ArrayList<ArrayList<Node<?>>> recreateAllShortPaths(Node<?> origin, Node<?> destination, Map<String, LinkedList<Node<?>>> previousNode, Map<String, Double> distances){
        ArrayList<ArrayList<Node<?>>> paths = new ArrayList<>();
        for(Node<?> prev : previousNode.get(destination.toString())){
            ArrayList<Node<?>> path = new ArrayList<>();
            path.add(destination);
            getPathFrom(origin, prev, previousNode, path);
            Collections.reverse(path);
            paths.add(path);
        }
        return paths;
    }

    /**
     * Recursive function to reconstruct all shortest paths found
     */
    private void getPathFrom(Node<?> origin, Node<?> current, Map<String, LinkedList<Node<?>>> previousNode, ArrayList<Node<?>> path){
        path.add(current);
        if(!origin.equals(current)){
            for(Node<?> prev : previousNode.get(current.toString())){
                getPathFrom(origin, prev, previousNode, path);
            }
        }
    }
    private ArrayList<Node<?>> recreateShortestPath(Node<?> origin, Node<?> destination, Map<String, Node<?>> previousNode) {
        ArrayList<Node<?>> path = new ArrayList<>();
        path.add(destination);
        Node<?> current = previousNode.get(destination.toString());
        while(!current.equals(origin)){
            path.add(current);
            current = previousNode.get(current.toString());
        }
        path.add(origin);
        Collections.reverse(path);
        return path;
    }

    private List<Node<?>> getShortOrLongPath(Node<?> origin, Node<?> destination, boolean shortest){
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
        return recreateShortestPath(origin, destination, previousNode);
    }

    public int getPathWeight(Object[] path){
        int weight = 0;
        for(int i = 0; i < path.length - 1; i++){
            weight += getNodeClass(path[i]).getAdjacencyHolder(path[i+1]).getWeight();
        }
        return weight;
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
        BfsIterator bfs = new BfsIterator(this.getNodeClass(originKey));
        while(bfs.ready()){
            if(bfs.next().toString().equals(destinationKey.toString())){
                return true;
            }
        }
        return false;
    }
    public boolean dfsSearch(Object originKey, Object destinationKey){ // itera sobre o grafo inteiro ate encontrar o destino ou nao ter mais elementos
        DfsIterator dfs = new DfsIterator(this.getNodeClass(originKey));
        while(dfs.ready()){
            if(dfs.next().toString().equals(destinationKey.toString())){
                return true;
            }
        }
        return false;
    }

    public List<?> getNodesAtDistance(Object origin, int distance){
        return this.getNodesAtDistance(this.getNodeClass(origin.toString()), distance);
    }
    public List<?> getNodesAtDistance(Node<?> origin, int distance){
        BfsIterator bfs = new BfsIterator(origin);
        List<Serializable> nodes = new ArrayList<>();
        while(bfs.ready() && !(bfs.nextIterationLayer() == distance)){ // itera sobre o grafo ate a camada dele for igual a distancia
            bfs.next();
        }
        nodes.add(bfs.next().getValue());
        while (bfs.ready() && bfs.nextIterationLayer() == distance){ // enquanto a distancia for igual a distancia
            nodes.add(bfs.next().getValue()); // adiciona na lista de nodes que tao na camada
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
        public int hashCode() {
            return toString().hashCode();
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
    private List<Edge> getAllEdges(){
        Node<?>[] nodes = getNodes();
        HashSet<Edge> edgesSet = new HashSet<>();
        LinkedList<Edge> edges = new LinkedList<>();
        for(Node<?> node : nodes){
            for(AdjacencyHolder adjacentNode : node.getAdjacencyHolders()){
                Edge e = new Edge(node, adjacentNode.getNode(), adjacentNode.getWeight());
                if(edgesSet.add(e)){
                    edges.add(e);
                }
            }
        }
        return edges;
    }
    public Graph genMinimumSpanningTree(){
        return Graph.genMinimumSpanningTree(this);
    }
    public static Graph genMinimumSpanningTree(Graph graph){
        // ordena o arraylist baseado no peso das arestas
        Edge[] edges = graph.getAllEdges().toArray(new Edge[0]);
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
            if(minTree.size() == graph.size() && minTree.size() == minTree.edgesCount()){
                break;
            }
        }
        return minTree;
    }

    /**
     * Gets all components inside this graph or null if its connected, to check if its connected or not, use
     * isConnected() instead
     * @return an arrayList on the components found as subGraphs or null if graph is connected
     */
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
        if(components.size() == 1){
            return components;
        }
        return null;
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

    public boolean isClique(Object[] nodes){
        Queue<Node<?>> queue = new LinkedList<>(Arrays.asList(getNodes(nodes)));
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
            if(getNodeClass(nodeKeys[i]) != null){
                nodes[i] = getNodeClass(nodeKeys[i]);
            }
        }
        return nodes;
    }
    public boolean isMaximalClique(Object[] nodeKeys){
        Node<?>[] nodes = getNodes(nodeKeys);
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

    public boolean isConnected(){
        Queue<Node<?>> queue = new LinkedList<>(getNodesList());
        while (!queue.isEmpty()){
            Node<?> node = queue.poll();
            for(Node<?> n : queue){
                if(node.getAdjacency(n.toString()) == null){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEulerian(){
        List<Node<?>> oddNodes = new ArrayList<>(2);
        for(Node<?> node : getNodes()){
            if(node.getAdjacencies().length % 2 == 1){
                oddNodes.add(node);
            }
            if(oddNodes.size() > 2){
                return false;
            }
        }
        return oddNodes.size() == 0 || oddNodes.size() == 2;
    }

    public boolean isCyclic(){
        if(directed){
            Graph graph = new Graph(this); // faz uma copia pois a gente vai ficar testando se possui folhas
            Queue<Node<?>> queue = new LinkedList<>(graph.getNodesList());
            // procura todos os nodes folhas
            List<Node<?>> leafs = graph.getAllLeafs();
            while (leafs.size() > 0){
                queue.addAll(graph.getAllLeafs());
                while (!queue.isEmpty()){
                    graph.remove(queue.poll());
                }
                if(graph.size() == 0){
                    return false;
                }
                leafs = graph.getAllLeafs();
            }
            return true;
        }else {
            HashSet<Node<?>> set = new HashSet<>();
            DfsIterator dfs = new DfsIterator(getNodes()[0]);
            dfs.checkForRepeats(false);
            while (dfs.ready()){
                Node<?> node = dfs.next();
                if(set.contains(node)){
                    return true;
                }else {
                    set.add(node);
                }
            }
        }
        return false;
    }

    private List<Node<?>> getAllLeafs(){
        LinkedList<Node<?>> list = new LinkedList<>();
        for(Node<?> node : getNodes()){ // procura todos os nodes folhas
            if(node.getAdjacencies().length == 0){
                list.add(node);
            }
        }
        return list;
    }

    public double getNodeBetwenessCentrality(Object nodeKey){
        Node<?> node = getNodeClass(nodeKey);
        Queue<Node<?>> queue = new LinkedList<>(getNodesList());
        int betweenCount = 0;
        int totalPathCount = 0;
        while (!queue.isEmpty()){
            Node<?> origin = queue.poll();
            if (origin.equals(node)) continue;
            for(Node<?> n : getNodes()){
                if(n.equals(origin) || n.equals(node)) continue;
                ArrayList<ArrayList<Node<?>>> allShortestPaths = getAllShortestPaths(origin, n);
                for(ArrayList<Node<?>> path : allShortestPaths){
                    if(path.contains(node)){
                        betweenCount++;
                    }
                }
                totalPathCount += allShortestPaths.size();
            }
        }
        return betweenCount / (double)totalPathCount;
    }

    public double getNodeClosenessCentrality(Object node){
        int allDistances = 0;
        for(Node<?> n : getNodes()){
            if(n.equals(node)) continue;
            allDistances += getPathWeight((getShortestPath(node, n).toArray()));
        }
        return (size()-1)/(double)allDistances;
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

    private int getSavedGraphsAmount(String location){
        File graphsFolder = new File(location);
        if(graphsFolder.isDirectory()){
            File[] files = graphsFolder.listFiles();
            if (files != null) {
                return files.length;
            }
        }
        return 0;
    }
    public boolean saveToFile(String location, String fileName, boolean overwrite) throws IOException {
        if(fileName == null || fileName.isEmpty()){
            fileName = "Graph" + getSavedGraphsAmount(location);
        }
        fileName += ".txt";
        File file = new File(location + "/" + fileName);
        if(!file.createNewFile() && !overwrite){
            throw new IOException("File already exists!");
        }
        StringBuilder builder = new StringBuilder();
        System.out.println("Saving vertices:");
        builder.append("*Vertices ").append(this.size()).append("\n");
        Node<?>[] nodes = this.getNodes();
        for(int i = 0; i < nodes.length; i++){
            builder.append(i+1).append(" \"").append(nodes[i]).append("\" ").append(encodeSerializable(nodes[i].getValue())).append("\n");
            System.out.print((i+1) + "/" + nodes.length + "\r");
        }
        System.out.println("Complete");
        System.out.println("Saving edges:");
        int edgesN = this.edgesCount();
        int counter = 0;
        builder.append(directed ? "*Arcs " : "*Edges ").append(edgesN).append("\n");
        for (Edge edge : this.getAllEdges()){
            builder.append("\"").append(edge.node1).append("\" \"").append(edge.node2).append("\" ").append(edge.weight).append("\n");
            System.out.print(++counter + "/" + edgesN + "\r");
        }
        System.out.println("Complete");
        FileWriter writer = new FileWriter(file);
        writer.write(builder.toString());
        writer.close();
        return true;
    }

    private static String encodeSerializable(Serializable object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static Object decodeString(String string) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(string);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
    public static Graph loadFromFile(String location){
        Graph graph = new Graph();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            boolean mode = false; // false = vertices, true = edges
            String line = reader.readLine();
            while (line != null){
                if(line.startsWith("*")){
                    String[] strings = line.split(" ");
                    String aa = strings[0].substring(1);
                    if(aa.equals("Arcs")){
                        mode = true;
                        graph.directed = true;
                    }else{
                        mode = aa.equals("Edges");
                    }
                }
                line = reader.readLine();
                if(mode){
                    System.out.println("Loading edges/arcs:");
                    int count = 0;
                    while (line != null && !line.startsWith("*")){
                        String[] nodes = line.replaceAll("[\t\n]", "").split("(\")");
                        if(nodes[2].equals("Jr.")){
                            System.out.println("aaaa");
                        }
                        graph.newAdjacency(nodes[1].replace("\"", ""), nodes[2].replace("\"", ""), Integer.parseInt(nodes[4].substring(1)));
                        line = reader.readLine();
                        System.out.print(++count + " loaded\r");
                    }
                    System.out.println("Complete");
                }else {
                    System.out.println("Loading vertices:");
                    int count = 0;
                    while (line != null && !line.startsWith("*")){
                        String[] entries = line.replaceAll("[\n\t]", "").split("\"");
                        graph.add(decodeString(entries[2].replace(" ", "")));
                        line = reader.readLine();
                        System.out.print(++count + " loaded\r");
                    }
                    System.out.println("Complete");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return graph;
    }
}
