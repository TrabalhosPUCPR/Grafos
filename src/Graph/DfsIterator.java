package Graph;

public class DfsIterator extends SearchIterator{

    public DfsIterator(Node<?> origin) {
        super(origin);
    }

    @Override
    public Node<?> next() {
        if(!this.ready()){
            return null;
        }
        Node<?> nextNode = this.nodesToVisit.getLast();
        this.nodesToVisit.removeLast();
        visited.add(nextNode);
        this.addToList(nextNode);
        return nextNode;
    }

    @Override
    void addToList(Node<?> currentNextNode) {
        Node<?>[] adjacencies = currentNextNode.getAdjacencies();
        for(Node<?> n : adjacencies){
            if(!visited.contains(n) || !checkIfVisited){
                nodesToVisit.removeFirstOccurrence(n);
                nodesToVisit.add(n);
            }
        }
    }
}
