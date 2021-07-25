package Server;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Bellman<T> extends Thread {

    //map that holds cheapest path from start vertex to every vertex in the matrix.
    private Map<Node, DistFromSource> weights;
    ReadWriteLock lock = new ReentrantReadWriteLock();

    public Bellman() {
        weights = new HashMap<>();
    }


    /**
     * This method runs Bellman algorithm to find the cheapest path between 2 given indexes in wighted matrix on multithreading
     * @param graph get Matrix and 2 indexes(start and destination) from CheapestPathMatrix Class {@link CheapestPathMatrix}
     * @return List of all the indexes from start point to destination as the cheapest path
     */
    public Collection<T> lightPath(CheapestPathMatrix graph) {
        Thread mainThread;
        Collection<Node<Index>> allMatrix = new ArrayList<>();
        final Collection<Node<Index>>[] neighbors = new Collection[]{new ArrayList<>()};

        allMatrix = graph.getMatrixNodes(graph.getOrigin());
        weights.put(graph.getOrigin(), new DistFromSource(0, null));
        neighbors[0] = graph.getNeighborsNodes(graph.getOrigin());

        //initialize the map with all the vertices(nodes) with distance infinity and parent null(except the start vertex that been already initialized)
        for (Node<Index> element : allMatrix) {
            if (!element.equals(graph.getOrigin())) {
                weights.put(element, new DistFromSource(Integer.MAX_VALUE, null));
            }
        }
        //initialize start vertex neighbors with the correct parent and calculated distance.
        for (Node<Index> neighbor : neighbors[0]) {
            weights.get(neighbor).setParent(neighbor.getParent());
            weights.get(neighbor).setDistance((graph.getOrigin().getValue() + neighbor.getValue()));

        }
        allMatrix.remove(graph.getOrigin());
        Collection<Node<Index>> finalAllMatrix = allMatrix;

        //create runnable variable that holds a thread pool and executing the bellman algorithm in multiple thread simultaneously
        //keeping a safe update of the 'weights' map by using a write lock.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ExecutorService executor = Executors.newFixedThreadPool(10);
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        for (Node<Index> element : finalAllMatrix) {
                            DistFromSource dis;
                            neighbors[0] = graph.getNeighborsNodes(element);
                            for (Node<Index> neighbor : neighbors[0]) {
                                if (weights.get(neighbor).getDistance() > (weights.get(element).getDistance() + neighbor.getValue())) {
                                    lock.writeLock().lock();
                                    try {
                                        weights.get(neighbor).setDistance((weights.get(element).getDistance() + neighbor.getValue()));
                                        weights.get(neighbor).setParent(element);
                                    } finally {
                                        lock.writeLock().unlock();
                                    }
                                }
                            }
                        }
                    }
                };

                for (int i = 0; i < finalAllMatrix.size(); i++) {
                    executor.submit(r2);
                }
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        };

        //execute the bellman algorithm using a Thread in function in class Bellman that will initiate the multi threading calculation.
        mainThread = new Thread(r);
        mainThread.start();
        //using join() to verify that the mainThread as finished running and we have a complete answer(map)
        try {
            mainThread.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        //create a list of the cheapest path from starting vertex to destination vertex
        // using the map that contains the cheapest path from start vertex to every vertex in the matrix.
        List<T> pathList = new ArrayList<>();
        pathList.add((T) graph.getDestination().getData());
        Node temp = weights.get(graph.getDestination()).getParent();
        while (!temp.equals(graph.getOrigin())) {
            pathList.add((T) temp.getData());
            temp = weights.get(temp).getParent();
        }
        pathList.add((T) graph.getOrigin().getData());

        return pathList;

    }

    @Override
    public void run() {

    }

}