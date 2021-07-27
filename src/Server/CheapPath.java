package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * This Class represent a cheapest path between to given indexes on given matrix it take.
 */
public class CheapPath<T> {
    private CheapestPathMatrix graphMatrix;
    private ThreadPoolExecutor threadPoolExecutor;
    private List<Callable<T>> futureTasksList;
    public ArrayList<ArrayList<Path>> mainList;
    private ReadWriteLock readWriteLock;
    private Matrix matrix;
    private Node<Index> start;
    private Node<Index> dest;

    public CheapPath(CheapestPathMatrix graph){
        graphMatrix = new CheapestPathMatrix(graph);
        readWriteLock = new ReentrantReadWriteLock();
        matrix = new Matrix(graph.getMatrix());
        futureTasksList = new ArrayList<>();
        start = graph.getStrNode();
        dest = graph.getDestNode();
        mainList = new ArrayList<>();

    }
    /**
     * This method runs algorithm to find the cheapest path between 2 given indexes in wighted matrix on multithreading
     * Uses matrix and two indexes (as nodes {@link Node}) that initialised in the builder.
     * @return List of all paths as indexes that have the lowest sum between two indexes from start point to destination.
     */
    public Collection<T> CheapestPath(){

        threadPoolExecutor = new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>());
        Stack<Node<Index>> workStack;
        Collection<Node<Index>> neighbors = new ArrayList<>();

        neighbors = graphMatrix.getNeighborsNodes(start);
        for (Node neighbor: neighbors) {
            workStack = new Stack<>();
            workStack.push(start);
            workStack.push(neighbor);
            futureTasksList.add(runSet(workStack));
        }
        try {
            //invoke all of the tasks
            //make them run parallel
            List<Future<T>> listOfFutureTasks = threadPoolExecutor.invokeAll(futureTasksList);
            //move all over the results
            for (Future<T> currResult: listOfFutureTasks) {
                mainList.add((ArrayList<Path>) currResult.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPoolExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        Collection listOfIndexes = new ArrayList<Index>();
        listOfIndexes = this.makeCheapestList();
         return listOfIndexes;
    }

    /**
     * This method runs as Callable task for each neighbor of the starting node{@link Node} and check if the path is optional
     * (is there a path between starting node and destination node).
     * The algorithm works like that : using a stack to contain a optional path
     * and creating a path object{@link Path} if it gets to the destination node{@link Node}.
     * @param optpath a stack that contains a starting node and one of his neighbors
     * @return a list of paths that start at the start index and ends at destination index and the sum of all values of indexes in the path.
     */
    private Callable runSet(Stack<Node<Index>> optpath) {
        Callable <ArrayList<Path>> matrixCallable = (()-> {
            Stack<Node<Index>> workingStack;
            workingStack = new Stack<>();
            Collection<Node<Index>> neighbors = new ArrayList<>();
            ArrayList<Path> pathArrayList = new ArrayList<>();

            if(optpath.peek().equals(dest)){
                Path optional = new Path(optpath);
                pathArrayList.add(optional);
                optpath.pop();
            }
            else {
                neighbors = graphMatrix.getNeighborsNodes(optpath.peek());
                for (Node neighbor : neighbors) {
                    workingStack.push(neighbor);
                }
                Node<Index> current = null;
                while (!workingStack.empty()) {
                    current = workingStack.pop();
                    if (current.getParent().equals(optpath.peek()) && !optpath.contains(current)) {
                        optpath.push(current);
                        if (current.equals(dest)) {
                            //create optional path and add it to path list.
                            Path optional = new Path(optpath);
                            pathArrayList.add(optional);
                            optpath.pop();
                        }
                        if (!current.equals(dest)) {
                            neighbors = graphMatrix.getNeighborsNodes(current);
                            for (Node neighbor : neighbors) {
                                workingStack.push(neighbor);
                            }
                        }
                    } else if (!current.getParent().equals(optpath.peek())) {
                        optpath.pop();
                        workingStack.push(current);
                    }
                }
            }
            return pathArrayList;
        });

        return matrixCallable;
    }

    public void drian(){
        threadPoolExecutor = null;
    }

    /**
     * This method generates a list of the cheapest path indexes{@link Index} from all the optional paths from start index to destination,
     * by comparing their path value.
     * @return List of all the cheapest paths as indexes{@link Index}
     */
    public Collection<T> makeCheapestList() {
        List<Path> temp = new ArrayList<>();
        for (ArrayList<Path> list1 : mainList) {
            for (Path path : list1) {
                if (temp.isEmpty()) {
                    temp.add(path);
                } else {
                    if (temp.get(temp.size() - 1).getDistance() > path.getDistance()) {
                        temp.clear();
                        temp.add(path);
                    } else if (temp.get(temp.size() - 1).getDistance() == path.getDistance()) {
                        temp.add(path);
                    }
                }
            }
        }
        List finalPath = new ArrayList<Index>();
        for(Path path: temp){
            finalPath.add(path.getIndexPath());
        }
        return finalPath;
    }

}
