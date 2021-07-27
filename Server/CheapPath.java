package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
        //workingStack = new Stack<>();
        //optPath = new Stack<>();

        futureTasksList = new ArrayList<>();

        start = graph.getStrNode();
        dest = graph.getDestNode();

        mainList = new ArrayList<>();

    }

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
                // lock and unlock writing to threads the thread
                //readWriteLock.writeLock().lock();
                mainList.add((ArrayList<Path>) currResult.get());
                //readWriteLock.writeLock().unlock();

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
        //System.out.println(aa);

         return listOfIndexes;
    }


    private Callable runSet(Stack<Node<Index>> optpath) {
        Callable <ArrayList<Path>> matrixCallable = (()-> {
            Stack<Node<Index>> workingStack;
            workingStack = new Stack<>();
            //workingStack.push(start);
            //workingStack.push(optpath.peek());
            Collection<Node<Index>> neighbors = new ArrayList<>();
            ArrayList<Path> pathArrayList = new ArrayList<>();

            neighbors = graphMatrix.getNeighborsNodes(optpath.peek());
            for(Node neighbor: neighbors) {
                workingStack.push(neighbor);
            }
            Node <Index> current = null;
            while (!workingStack.empty()){

                current = workingStack.pop();

                if(current.getParent().equals(optpath.peek())&&!optpath.contains(current)){

                    optpath.push(current);
                    if(current.equals(dest)){
                        //כאן צריכה להיות פונקייצת קריאה ליצירת ליסט של מסלול.
                        Path optional = new Path(optpath);
                        pathArrayList.add(optional);
                        optpath.pop();
                    }
                    if(!current.equals(dest)) {
                        neighbors = graphMatrix.getNeighborsNodes(current);
                        for (Node neighbor : neighbors) {
                            workingStack.push(neighbor);
                        }
                    }
                }
                else if(!current.getParent().equals(optpath.peek()))
                {optpath.pop(); workingStack.push(current);}
            }
            return pathArrayList;
        });

        return matrixCallable;
    }

    public void drian(){
        threadPoolExecutor = null;
    }

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

//    public void PrintWorkingStack(Stack<Node<Index>> temp){
//        // If stack is empty
//        if (temp.empty())
//            return;
//
//        // Extract top of the stack
//        Node<Index> x = temp.peek();
//
//        // Pop the top element
//        temp.pop();
//
//        // Print the current top
//        // of the stack i.e., x
//        System.out.print(x + " ");
//
//        // Proceed to print
//        // remaining stack
//        PrintWorkingStack(temp);
//
//        // Push the element back
//        temp.push(x);
//    }

//    public boolean Contains(Node<Index> o){
//        for(Node<Index> e: optPath){
//            if(e.equals(o)){
//                return true;
//            }
//        }
//        return false;
//    }
}
