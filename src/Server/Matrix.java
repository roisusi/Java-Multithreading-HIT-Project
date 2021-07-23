package Server;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Matrix implements Serializable
{
    /**
     * Neighboring Indices are up,down, left,right
     *   1 0 0
     *   0 1 1
     *   0 0 0
     *   1 1 1
     *
     * [[(0,0),
     * [(1,1) ,(1,2)],
     * [(3,0),(3,1),(3,2)]]
     *
     *
     * 1 0 0
     * 0 1 1
     * 0 1 0
     * 0 1 1
     *
     *
     */

    int[][] primitiveMatrix;

    public Matrix(){
        //Nothing need to be
    }

    public Matrix(int[][] oArray){
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }

    public Matrix(int a , int b) {
        Random r = new Random();
        primitiveMatrix = new int[a][b];
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[0].length; j++) {
                primitiveMatrix[i][j] = r.nextInt(2);
            }
        }
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public List<Index> getNeighbors(final Index index){
        List<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row+1][index.column];
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column+1];
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column];
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column-1];
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public Collection<Index> getAdjacentIndices(final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.getRow()+1][index.getColumn()];
            list.add(new Index(index.getRow()+1,index.getColumn()));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.getRow()][index.getColumn()+1];
            list.add(new Index(index.getRow(),index.getColumn()+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.getRow()-1][index.getColumn()];
            list.add(new Index(index.getRow()-1,index.getColumn()));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.getRow()][index.getColumn()-1];
            list.add(new Index(index.getRow(),index.getColumn()-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public int getValue(Index index) {
        return primitiveMatrix[index.row][index.column];
    }

    public List<Index> getReachables(Index index) {
        ArrayList<Index> filteredIndices = new ArrayList<>();
        this.getNeighbors(index).stream().filter(i-> getValue(i)==1)
                .map(neighbor->filteredIndices.add(neighbor)).collect(Collectors.toList());
        return filteredIndices;
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }


    public int getRowNumber() {
        return primitiveMatrix.length;
    }


    public int getColNumber() {
        return primitiveMatrix[0].length;
    }

    public List<Set<Index>> getOnes(Matrix matrix) {
        List<Index> allUnVisitedItems = new ArrayList<>();
        List<Index> returnedGraphs;
        List<List> savingEachGraph = new ArrayList<>();
        TraversableMatrix graph = new TraversableMatrix(matrix);
        DFSvisit dfSvisit = new DFSvisit();

//        dfSvisit.traverse(graph);//one CC item
        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();

        for (int row = 0; row < rowNumber; row++) {
            for (int column = 0; column < colNumber; column++) {
                Index index = new Index(row, column);
                if (matrix.getValue(index) == 1) {
                    allUnVisitedItems.add(index);
                }
            }
        }
        for (Index item : allUnVisitedItems) {
            returnedGraphs = (List<Index>) dfSvisit.traverse(new TraversableMatrix(matrix, item));
            returnedGraphs.sort(Comparator.comparing(Index::getRow).thenComparing(Index::getColumn));
//            neiboursIndexes indexes = new neiboursIndexes(returnedGraphs);
            savingEachGraph.add(returnedGraphs.stream().distinct().collect(Collectors.toList()));
        }
//        System.out.println(savingEachGraph.get(0));
        for(int i = 1; i< savingEachGraph.size(); i++){
            if (savingEachGraph.get(i - 1).equals(savingEachGraph.get(i)) ){
                savingEachGraph.remove(savingEachGraph.get(i-1));
                i--;
            }
        }

        // keep track of Indices we've already seen
        Set<Index> elementCache = new HashSet<Index>();
        for (List<Index> list: savingEachGraph){
            for (Iterator<Index> it = list.iterator(); it.hasNext();){
                Index element = it.next();

                if (elementCache.contains(element)){
                    it.remove();
                }else {
                    elementCache.add(element);
                }
            }
        }




        Collections.sort(savingEachGraph, (a1, a2) -> {
            return (a1.size() - a2.size());
        });
        List<Set<Index>> sortFinalCC = new ArrayList(savingEachGraph);
//        System.out.println(sortFinalCC);
        return sortFinalCC;
    }

    public static void main(String[] args) {
        int[][] source = {
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1},
                {0, 0, 0, 1, 1},
                {1, 1, 0, 0, 0}

        };
        Index start = new Index(1,1);
        Index end = new Index(2,2);
        Matrix matrix = new Matrix(source);

//        System.out.println(matrix.BFS(source, start, end));
//        matrix.printMatrix();
        System.out.println(matrix.getOnes(matrix));
        System.out.println((matrix.getOnes(matrix).get(0)));
//        System.out.println(matrix.getNeighbors(new Server.Index(0,0)));
//        System.out.println(matrix.getReachables(new Server.Index(1,1)));
    }
}

