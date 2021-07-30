package Server;

import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
    This Class Represent a Matrix and tools that use the matrix
    such as find neighbors , shortest paths from (x1,y1) to (x2,y2) and all the indices contains 1 on a matrix.
 */


public class Matrix implements Serializable {

    int[][] primitiveMatrix;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static List<Callable<List<Index>>> futureTasksList;
    private static ReadWriteLock readWriteLock;


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

    /**
     * getting number of rows
     * @return number of rows in primitiveMatrix
     */
    public int getRowNumber() {
        return primitiveMatrix.length;
    }

    /**
     * getting number of columns
     * @return return number of columns in primitiveMatrix
     */
    public int getColNumber() {
        return primitiveMatrix[0].length;
    }

    /**
     * task 1 -  finding all bondage components in matrix
     * 1. finding all indices equals to 1 and saving them in "allUnVisitedItems" (list)
     * 2. running thread for each index and by using threadLocalDfsVisit finding his bondage components
     * 3. inserting into "savingEachGraph" (list of HashSets) each bondage components as a graph
     * 4. if bondage components has more then 1 index we will get this bondage components few time,
     *    to fix this problem we are running on each bondage components and deleting indices we saw.
     * 5. deleting empty lists (result of 4)
     * 6. sorting savingEachGraph by size of each list
     * @param matrix - standard matrix we receive
     * @return list of HashSets<Index>
     */


    //----------------//
    // --- Task 1 --- //
    //----------------//


    public static List<HashSet<Index>> findGroups(Matrix matrix) {
        List<Index> allUnVisitedItems = new ArrayList<>(); // Indices = 1 from the matrix we didnt visit
        List<Index> returnedGraphs;   // List of graphs return from DFSVisit
        List<HashSet> savingEachGraph = new ArrayList<>();  // Creating one list that handle all Lists
        readWriteLock = new ReentrantReadWriteLock();
//        DFSvisit DfsVisit = new DFSvisit();
        ThreadLocalDfsVisit threadLocalDfsVisit = new ThreadLocalDfsVisit();


        int rowNumber = matrix.getRowNumber();
        int colNumber = matrix.getColNumber();

        /**
         * finding indices equals to 1 and adding them to "allUnVisitedItems"
         */
        for (int row = 0; row < rowNumber; row++) {
            for (int column = 0; column < colNumber; column++) {
                Index index = new Index(row, column);
                if (matrix.getValue(index) == 1) {
                    allUnVisitedItems.add(index);
                }
            }
        }

        // running on every index from allUnVisitedItems (equals to 1)
        threadPoolExecutor = new ThreadPoolExecutor(4,7,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>());

        //running on every index from allUnVisitedItems (equals to 1)
        futureTasksList = new ArrayList<>();
        Callable<List<Index>> callable;
        for (Index item : allUnVisitedItems) {

            // for each index equals to 1, check his neighbours and creating List of tying components
            // and add all the tasks to future List
            callable = () -> (List<Index>)threadLocalDfsVisit.traverse(new TraversableMatrix(matrix, item));
            futureTasksList.add(callable);

        }

        //Staring to execute the tasks in a parallel
        try {
            List<Future<List<Index>>> allResultsAsFuture = threadPoolExecutor.invokeAll(futureTasksList);
            for (Future<List<Index>> currResult: allResultsAsFuture) {
                returnedGraphs = (List<Index>)currResult.get();

                //save many links in savingEachGraph (may be more the same links
                savingEachGraph.add(new HashSet<>(returnedGraphs));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        /**
         * running on every list(bondage components) in "savingEachGraph" and deleting duplicate indices
         */
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

        /**
         * deleting empty lists
         */
        for(int i = 0; i< savingEachGraph.size(); i++){
            if (savingEachGraph.get(i).isEmpty()){
                savingEachGraph.remove(savingEachGraph.get(i));
                i--; // To not skip list
            }
        }


        /**
         * sorting "savingEachGraph" by size of each HashSet.
         * saving "savingEachGraph" as List of HashSets<Index>
         */
        Collections.sort(savingEachGraph, (a1, a2) -> {
            return (a1.size() - a2.size());
        });
        List<HashSet<Index>> sortFinalCC = new ArrayList(savingEachGraph);  // List of hashset<Index>
        return sortFinalCC;
    }

    //----------------//
    // --- Task 2 --- //
    //----------------//

    /**
     * scanning index neighbors by diagonal
     * @param index - index equal to 1
     * @return list of neighbors
     */
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

    /**
     * fore each index (value == 1) finding his neighbors (including diagonals)
     * and deleting all neighbors with index.value == 0
     * @param index value == 1
     * @return Collection of index neighbors
     */
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


    /**
     * Using BFS algo to find shortest paths from index to index
     * @param matrix standard matrix
     * @param start Index we start from
     * @param end  Target Index
     * @return All shortest paths
     */
    public static List<List<Index>> findPaths(Matrix matrix, Index start, Index end) {

        Queue<List<Index>> matrixQueue = new LinkedList<>();
        List<Index> path = new ArrayList<>();
        path.add(start);
        matrixQueue.offer(path);
        List<List<Index>> pathsList = new ArrayList<>();

        /**
         * if matrixQueue is empty and we didnt reach the end, there is not path
         */
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

            @NotNull Collection<Index> reachableNodes = matrix.deleteZeros(last);

            /**
             * using "isNotVisited" to check if we already went this way.
             */
            for (Index index : reachableNodes){
                if (isNotVisited(index,path)){
                    List<Index> newPath = new ArrayList<>(path);
                    newPath.add(new Index(index.getRow(), index.getColumn()));
                    matrixQueue.offer(newPath);
                }
            }

        }
        if (pathsList.isEmpty())
            System.out.println("No route from start index to end index");
        return pathsList;
    }

    /**
     * receiving index and path and checking if index is in path
     * @param index
     * @param path
     * @return false if index already in path, else true
     */
    public static boolean isNotVisited(Index index, List<Index> path) {
        int size = path.size();
        for (int i = 0; i<size; i++){
            Index pathIndex = path.get(i);
            if (pathIndex.getRow() == index.getRow() && pathIndex.getColumn() == index.getColumn())
                return false;
        }
        return true;
    }

    /**
     * printing func
     * @param path
     * @return printing each path
     */
    public String printPath(List<List<Index>> path) {
        int size = path.size();
        String s = "";
        for (List<Index> index : path){
            s += index + " ";
        }
        return s;
    }

    /**
     * converting HashSet to List, helping us to solve 3 using 1's result
     * @param hashSetList
     * @return list of lists
     */
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
////                {1,0,0},
////                {1,0,1},
////                {1,0,1}
//
//        };
//        Index start = new Index(1,4);
//        Index end = new Index(4,0);
//        Matrix matrix = new Matrix(source);
//
//
//        List<HashSet<Index>> hashSetGroups =  Matrix.findGroups(matrix);
//        System.out.println(hashSetGroups);
////        List<List> listGroups = convertHashToList(hashSetGroups);
////        System.out.println(listGroups);
//
//        System.out.println(findPaths(matrix, start,end));

//    }
}

