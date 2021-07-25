package Server;

/**
 * a class that holds the distance of a vertex from source vertex and the parent vertex that is previous in the path to the current one
 * this class is a helper for the map Map<currentVertex,[distanceFromSource,previousVertex]>
 */
public class DistFromSource {
    Integer distance;
    Node<Index> parent;

    public DistFromSource(){
        this.distance = null;
        this.parent = null;
    }
    public DistFromSource(Integer dist, Node parent){
        this.distance = dist;
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setParent(Node<Index> parent) {
        this.parent = parent;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }


}
