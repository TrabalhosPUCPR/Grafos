package Graph;

import java.util.LinkedHashMap;

public class Node<T> {
    protected static class AdjacencyHolder {
        final Node<?> node;
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

        public void setWeight(Integer weight) {
            this.weight = weight;
        }
    }

    private T value;
    private final LinkedHashMap<String, AdjacencyHolder> adjacencies;

    public Node(T value) {
        this.value = value;
        this.adjacencies = new LinkedHashMap<>();
    }
    public Node(Node<T> value){
        this.value = value.value;
        this.adjacencies = value.adjacencies;
    }

    public void setValue(T value){
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    protected Integer getWeight(Object adjacentNode) {
        // pega o peso de uma conexao
        return adjacencies.get(adjacentNode.toString()).getWeight();
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    protected boolean newAdjacency(Node<?> node, int weight){ // adiciona no hashmap de adjacencias
        if(this.equals(node)){ // cancela caso o node seja igual a ele mesmo
            return false;
        }
        // a chave e o rotulo, e cria um adjacencyholder pra guarda o peso e o valor
        return this.adjacencies.put(node.toString(), new AdjacencyHolder(node, weight)) == null;
    }
    protected Node<?> getAdjacency(String key){ // retorna a adjacencia se a chave existir
        AdjacencyHolder adjacent = this.adjacencies.get(key);
        if(adjacent != null){
            return adjacent.getNode();
        }
        return null;
    }

    protected Node<?>[] getAdjacencies() { // retorna um array com os nodes adjacentes, tudo isso pra pega do hashmap
        AdjacencyHolder[] adjacencyHolders = new AdjacencyHolder[0];
        adjacencyHolders = this.adjacencies.values().toArray(adjacencyHolders);
        Node<?>[] nodesList = new Node<?>[adjacencyHolders.length];
        for(int i = 0; i < adjacencyHolders.length; i++){
            nodesList[i] = adjacencyHolders[i].getNode();
        }
        return nodesList;
    }

    protected AdjacencyHolder[] getAdjacencyHolders(){ // retorna o objeto q segura o node e o peso
        AdjacencyHolder[] adjacencyHolders = new AdjacencyHolder[0];
        return this.adjacencies.values().toArray(adjacencyHolders);
    }

    protected AdjacencyHolder getAdjacencyHolder(Object key){
        return this.adjacencies.get(key.toString());
    }

    protected boolean removeAdjacency(Object key){
        return this.adjacencies.remove(key.toString()) != null;
    }

    protected void setWeight(Object adjacentNode, int weights){
        this.adjacencies.get(adjacentNode.toString()).setWeight(weights);
    }

    protected int sumWeights(){ // soma todos os pesos das adjacencias
        int sum = 0;
        for(AdjacencyHolder adH : this.getAdjacencyHolders()){
            sum += adH.weight;
        }
        return sum;
    }
    @Override
    public boolean equals(Object obj) {
        return obj.toString().equals(this.toString());
    }
}
