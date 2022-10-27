package Graph;

import java.util.LinkedHashMap;

public class Node<T> {
    protected static class AdjacencyHolder {
        Node<?> node;
        Integer weight;

        public AdjacencyHolder(Node<?> node, Integer weight) {
            this.node = node;
            this.weight = weight;
        }

        public Node<?> getNode() {
            return node;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setNode(Node<?> node) {
            this.node = node;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }
    }

    private T label;
    private final LinkedHashMap<Object, AdjacencyHolder> adjacencies;

    public Node(T label) {
        this.label = label;
        this.adjacencies = new LinkedHashMap<>();
    }

    public void setLabel(T label){
        this.label = label;
    }

    public T getLabel() {
        return label;
    }

    public Integer getWeight(Object adjacentNode) {
        // pega o peso de uma conexao
        return adjacencies.get(adjacentNode.toString()).getWeight();
    }

    @Override
    public String toString() {
        return this.label.toString();
    }

    protected void newAdjacency(Node<?> node, int weight){ // adiciona no hashmap de adjacencias
        // a chave e o rotulo, e cria um adjacencyholder pra guarda o peso e o valor
        this.adjacencies.put(node.toString(), new AdjacencyHolder(node, weight));
    }
    public Node<?> getAdjacency(String key){ // retorna a adjacencia se a chave existir
        AdjacencyHolder adjacent = this.adjacencies.get(key);
        if(adjacent != null){
            return adjacent.getNode();
        }
        return null;
    }

    public Node<?>[] getAdjacencies() { // retorna um array com os nodes adjacentes, tudo isso pra pega do hashmap
        AdjacencyHolder[] adjacencyHolders = new AdjacencyHolder[0];
        adjacencyHolders = this.adjacencies.values().toArray(adjacencyHolders);
        Node<?>[] nodesList = new Node<?>[adjacencyHolders.length];
        for(int i = 0; i < adjacencyHolders.length; i++){
            nodesList[i] = adjacencyHolders[i].getNode();
        }
        return nodesList;
    }

    public AdjacencyHolder[] getAdjacencyHolders(){ // retorna o objeto q segura o node e o peso
        AdjacencyHolder[] adjacencyHolders = new AdjacencyHolder[0];
        return this.adjacencies.values().toArray(adjacencyHolders);
    }

    public AdjacencyHolder getAdjacencyHolder(String key){
        return this.adjacencies.get(key);
    }

    public void setWeight(Object adjacentNode, int weights){
        this.adjacencies.get(adjacentNode.toString()).setWeight(weights);
    }

    public int sumWeights(){ // soma todos os pesos das adjacencias
        int sum = 0;
        for(AdjacencyHolder adH : this.getAdjacencyHolders()){
            sum += adH.weight;
        }
        return sum;
    }

    public boolean equals(Node<?> n) {
        return n.getLabel().equals(this.label);
    }
}
