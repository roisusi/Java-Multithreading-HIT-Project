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

    public Index getStartIndex() {
        return strIndex;
    }

    public Index getDestIndex() { return destIndex; }

    public void setStartIndex(Index startIndex) {
        this.strIndex = startIndex;
    }

    public void setDestIndex(Index destIndex) { this.destIndex = destIndex; }

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
     * @return destination index as Node {@link Node}
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

    public int getLength(){
        return this.matrix.getPrimitiveMatrix().length;
    }


    /**
     * This method takes the matrix {@link Matrix} in the class and sends it back as list of nodes{@link Node}
     * initialized with values from matrix.
     * @param someNode gets a node containing index {@link Index} and setting it to be the first in the list.
     * @return ArrayList of nodes initialized with values related to indexes of the matrix.
     */
    public Collection<Node<Index>> getMatrixNodes(Node<Index> someNode) {
        List<Node<Index>> allIndex = new ArrayList<>();

        someNode.setValue(matrix.getValue(someNode.getData()));
        allIndex.add(someNode);

        List<Node<Index>> list = new ArrayList<>();
        for (int i=0;i <matrix.getPrimitiveMatrix().length; i++){
            for (int j=0; j<matrix.getPrimitiveMatrix()[0].length; j++){
                    Node<Index> tempNode = new Node<>(new Index(i,j));
                    tempNode.setValue(matrix.getValue(tempNode.getData()));
                    if(!tempNode.equals(someNode)){
                        allIndex.add(tempNode);
                    }
            }
        }
        return allIndex;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}


