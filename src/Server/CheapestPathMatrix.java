package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheapestPathMatrix implements CheapestPath<Index> {
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


    @Override
    public Node<Index> getStart() throws NullPointerException{
        if (this.strIndex == null) throw new NullPointerException("start index is not initialized");
        Node n = new Node<>(this.strIndex);
        n.setValue(matrix.getValue(strIndex));
        return n;
    }

    @Override
    public Node<Index> getDestination() {
        if (this.destIndex == null) throw new NullPointerException("destination index is not initialized");
        Node n = new Node<>(this.destIndex);
        n.setValue(matrix.getValue(destIndex));
        return n;
    }


    @Override
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

    @Override
    public Collection<Node<Index>> getMatrixNodes(Node<Index> someNode) {
        List<Node<Index>> allIndex = new ArrayList<>();
        List <Index> temp = new ArrayList<>();

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


