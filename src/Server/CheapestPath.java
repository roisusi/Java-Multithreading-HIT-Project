package Server;

import java.util.Collection;

public interface CheapestPath<T>{
    public Node<T> getStart();
    public Node<T> getDestination();
    public Collection<Node<T>> getNeighborsNodes(Node<T> someNode);
    public Collection<Node<T>> getMatrixNodes(Node<T> someNode);
}