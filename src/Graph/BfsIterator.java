package Graph;

public class BfsIterator extends SearchIterator{
    private int layer = -1;
    private int nextLayerSize;
    private int currentLayerLeft = 0;

    public BfsIterator(Node<?> origin) {
        super(origin);
    }

    @Override
    public Node<?> next() {
        if(!this.ready()){
            return null;
        }
        updateLayer();
        Node<?> nextNode = nodesToVisit.getFirst();
        nodesToVisit.removeFirst();
        visited.add(nextNode);
        this.addToList(nextNode);
        return nextNode;
    }

    public int nextIterationLayer(){
        if(this.currentLayerLeft <= 0){
            return layer + 1;
        }else{
            return layer;
        }
    }

    private void updateLayer(){
        if(this.currentLayerLeft <= 0){
            layer++;
            this.currentLayerLeft = this.nextLayerSize;
            this.nextLayerSize = -1;
        }else{
            this.currentLayerLeft--;
        }
    }

    @Override
    public void addToList(Node<?> currentNextNode) {
        Node<?>[] adjacents = currentNextNode.getAdjacencies();
        for(Node<?> n : adjacents){
            if(!visited.contains(n) && !nodesToVisit.contains(n) || !checkIfVisited){
                nodesToVisit.add(n);
                this.nextLayerSize++;
            }
        }
    }

    public int getLayer() {
        return layer;
    }
}
