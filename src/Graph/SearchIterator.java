package Graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class SearchIterator {

    LinkedList<Node<?>> nodesToVisit = new LinkedList<>();
    Set<Node<?>> visited = new HashSet<>();

    public SearchIterator(Node<?> origin){
        this.nodesToVisit.add(origin);
    }

    public abstract Node<?> next();
    public Node<?> peek(){
        return nodesToVisit.getFirst();
    }
    public boolean ready(){
        return !nodesToVisit.isEmpty();
    }
    abstract void addToList(Node<?> currentNextNode);
}
