package Server;

import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class Matrix implements Serializable {

    int[][] primitiveMatrix;

    public Matrix(){
    // Holds Empty for just initialization
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
        }
        System.out.println("\n");
    }

    /**
     *
     * @return
     */

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    @NotNull
    public Collection<Index> getNeighbors(@NotNull final Index index){
        Collection<Index> list = new ArrayList<>();
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

    public int getValue(@NotNull final Index index){
        return primitiveMatrix[index.row][index.column];
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

    public List<HashSet<Index>> findGroups(Matrix matrix) {
        List<Index> allUnVisitedItems = new ArrayList<>(); // Indices = 1 from the matrix we didnt visit
        List<Index> returnedGraphs;   // List of graphs return from DFSVisit
        List<HashSet> savingEachGraph = new ArrayList<>();  // Creating one list that handle all Lists
        DFSvisit threadLocalDfsVisit = new DFSvisit();


        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();

        // checking which Indices equals to 1
        for (int row = 0; row < rowNumber; row++) {
            for (int column = 0; column < colNumber; column++) {
                Index index = new Index(row, column);
                if (matrix.getValue(index) == 1) {
                    allUnVisitedItems.add(index);
                }
            }
        }

        // running on every index from allUnVisitedItems (equals to 1)

        //TODO Threads
        for (Index item : allUnVisitedItems) {

            // for each index equals to 1, check his neighbours and creating List of tying components
            returnedGraphs = (List<Index>) threadLocalDfsVisit.traverse(new TraversableMatrix(matrix, item));

            // sorting every tying components by row then column
            returnedGraphs.sort(Comparator.comparing(Index::getRow).thenComparing(Index::getColumn));

            // collect each tying component to list of lists
            savingEachGraph.add(new HashSet<>(returnedGraphs));
        }
        //TODO Threads

        // delete duplicate lists
        for(int i = 1; i< savingEachGraph.size(); i++){
            if (savingEachGraph.get(i - 1).equals(savingEachGraph.get(i)) ){
                savingEachGraph.remove(savingEachGraph.get(i-1));
                i--; // To not skip list
            }
        }

        // keep track of Indices we've already seen and remove
        Set<Index> elementCache = new HashSet<>(); // store indices we've already seen
        for (HashSet list: savingEachGraph){
            for (Iterator<Index> it = list.iterator(); it.hasNext();){
                Index element = it.next();

                if (elementCache.contains(element)){
                    it.remove();
                }else {
                    elementCache.add(element);
                }
            }
        }

        // sort by list size
        Collections.sort(savingEachGraph, (a1, a2) -> {
            return (a1.size() - a2.size());
        });
        List<HashSet<Index>> sortFinalCC = new ArrayList(savingEachGraph);  // List of hashset<Index>
        return sortFinalCC;
    }

    // finding neighbours in diagonal line
    @NotNull
    public Collection<Index> getDiagonalNeighbors(@NotNull final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row +1][index.column -1];
            list.add(new Index(index.row +1 ,index.column -1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row -1][index.column +1];
            list.add(new Index(index.row -1,index.column +1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column-1];
            list.add(new Index(index.row-1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row+1][index.column+1];
            list.add(new Index(index.row+1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}

        return list;
    }

    // delete neighbours with value 0
    public Collection<Index> deleteZeros(@NotNull final Index index){
        Collection<Index> neighbors = this.getNeighbors(index);
        neighbors.addAll(this.getDiagonalNeighbors(index));
        Collection<Index> neighborsValueIsOne = new ArrayList<>();
        for (Iterator<Index> it = neighbors.iterator(); it.hasNext();){
            Index element = it.next();
            if (this.getValue(element) == 1 ){ //&& !checked.contains(element)
                neighborsValueIsOne.add(element);
            }
        }
        return neighborsValueIsOne;
    }

    // scanning matrix and saving all the shortest paths (Using BFS)
    public List<List<Index>> findPaths(Matrix matrix, Index start, Index end) {

        Queue<List<Index>> matrixQueue = new LinkedList<>();
        List<Index> path = new ArrayList<>();
        path.add(start);
        matrixQueue.offer(path);
        List<List<Index>> pathsList = new ArrayList<>();


        while (!matrixQueue.isEmpty()){

            path = matrixQueue.poll();
            Index last = path.get(path.size() -1);

            if (last.getRow() == end.getRow() && last.getColumn() == end.getColumn()){
                pathsList.add(path);
                if (1 < pathsList.size()){
                    if (pathsList.get(pathsList.size() -2).size() < path.size()){
                        pathsList.remove(path);
                    }
                }
            }
//           List<Index> lastNode = new ArrayList<Index>(Collections.singleton(last));
            @NotNull Collection<Index> reachableNodes = matrix.deleteZeros(last);

            for (Index index : reachableNodes){
                if (isNotVisited(index,path)){
                    List<Index> newPath = new ArrayList<>(path);
                    newPath.add(new Index(index.getRow(), index.getColumn()));
                    matrixQueue.offer(newPath);
                }
            }

        }
        return pathsList;
    }

    // checking if we visited this index already
    public boolean isNotVisited(Index index, List<Index> path) {
        int size = path.size();
        for (int i = 0; i<size; i++){
            Index pathIndex = path.get(i);
            if (pathIndex.getRow() == index.getRow() && pathIndex.getColumn() == index.getColumn())
                return false;
        }
        return true;
    }

    // print Lists
    public String printPath(List<List<Index>> path) {
        int size = path.size();
        String s = "";
        for (List<Index> index : path){
            s += index + " ";
        }
        return s;
    }

    public static List<List> convertHashToList(List<HashSet<Index>> hashSetList){
        List<List> listOfLists = new ArrayList<>();
        for (HashSet hashSet : hashSetList){
            listOfLists.add((List) hashSet.stream().collect(Collectors.toList()));
        }

        for (List list : listOfLists){
            list.sort(Comparator.comparing(Index::getRow).thenComparing(Index::getColumn));
        }
        return listOfLists;
    }

//    public static void main(String[] args) {
//        int[][] source = {
//                {1, 0, 1, 0, 0},
//                {1, 0, 1, 0, 1},
//                {1, 0, 0, 1, 1},
//                {0, 0, 1, 0, 1},
//                {1, 1, 1, 0, 0}
//
//        };
//        Index start = new Index(1,4);
//        Index end = new Index(4,0);
//        Matrix matrix = new Matrix(source);
//
//
////        List<HashSet<Index>> hashSetGroups =  Matrix.findGroups(matrix);
////        System.out.println(hashSetGroups);
////        List<List> listGroups = convertHashToList(hashSetGroups);
////        System.out.println(listGroups);
////        findPaths(matrix, start,end);
//
//    }
}

