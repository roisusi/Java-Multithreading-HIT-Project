package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class implements adapter/wrapper/decorator design pattern implements Traversable interface{@link Traversable}
 * containing matrix {@link Matrix}, start index , destination index {@link Index}
 */
public class CheapestPathMatrix implements Traversable<Index> {
    protected final Matrix matrix;
    protected Index strIndex;
    protected Index destIndex;

    public CheapestPathMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
    /**
     * Constractor to initialize CheapestPathMatrix class object with same class object.
     * @param graph of class CheapestPathMatrix
     */
    public CheapestPathMatrix(CheapestPathMatrix graph){
        matrix = new Matrix(graph.getMatrix());
        strIndex = new Index(graph.getStartIndex().getRow(),graph.getStartIndex().getColumn());
        destIndex = new Index(graph.getDestIndex().getRow(),graph.getDestIndex().getColumn());
    }

    public Index getStartIndex() {
        return strIndex;
    }

    public Index getDestIndex() { return destIndex; }

    public void setStartIndex(Index startIndex) {
        this.strIndex = startIndex;
    }

    public void setDestIndex(Index destIndex) { this.destIndex = destIndex; }

    /**
     * This method gets the predefined destination index in the class, and setting his value.
     * @return Destination index as Node {@link Node}
     */
    public Node<Index> getDestNode() {
        Node<Index> dest = new Node<>(destIndex,null, this.matrix.getValue(destIndex));
        return dest;
    }

    /**
     * This method gets the start index as node{@link Node} and sets the value of that index.
     * @return Starting node{@link Node}
     */
    public Node<Index> getStrNode() {
        Node<Index> str = new Node<>(strIndex,null, this.matrix.getValue(strIndex));
        return str; }

    public int[][] getMatrix() {
        return this.matrix.getPrimitiveMatrix();
    }

    /**
     * This method gets the start index predefined in class, as node{@link Node} and setting his value.
     * @return start index as Node {@link Node}
     * @throws NullPointerException
     */
    @Override
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.strIndex == null) throw new NullPointerException("start index is not initialized");
        Node n = new Node<>(this.strIndex);
        n.setValue(matrix.getValue(strIndex));
        return n;
    }

    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        return null;
    }

    /**
     * This method gets the predefined destination index in the class, and setting his value.
     * @return Destination index as Node {@link Node}
     */
    public Node<Index> getDestination() {
        if (this.destIndex == null) throw new NullPointerException("destination index is not initialized");
        Node n = new Node<>(this.destIndex);
        n.setValue(matrix.getValue(destIndex));
        return n;
    }

    /**
     *This method gets neighboring nodes(up,down,right,left) for a given node and setting their values from matrix and their parent node as the given node.
     * @param someNode gets a node containing index {@link Index}
     * @return ArrayList of neighboring nodes initialized with values and parent node.
     */
    public Collection<Node<Index>> getNeighborsNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
            Node<Index> indexNode = new Node<>(index, someNode);
            indexNode.setValue(matrix.getValue(index));
            reachableIndex.add(indexNode);
        }
        return reachableIndex;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}


