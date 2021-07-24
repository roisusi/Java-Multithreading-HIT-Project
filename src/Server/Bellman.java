package Server;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bellman<T> extends Thread {

    //map that holds cheapest path from start vertex to every vertex in the matrix.
    private Map<Node, DistFromSource> weights;


    public Bellman() {
        weights = new HashMap<>();
    }


    public Collection<T> lightPath(CheapestPathMatrix graph) {
        Thread mainThread;
        System.out.println("start: " + graph.getStart() + "dest: " + graph.getDestination());

        Collection<Node<Index>> allMatrix = new ArrayList<>();
        final Collection<Node<Index>>[] neighbors = new Collection[]{new ArrayList<>()};

        allMatrix = graph.getMatrixNodes(graph.getStart());
        weights.put(graph.getStart(), new DistFromSource(0, null));
        neighbors[0] = graph.getNeighborsNodes(graph.getStart());

        //initialize the map with all the vertices(nodes) with distance infinity and parent null(except the start vertex that been already initialized)
        for (Node<Index> element : allMatrix) {
            if (!element.equals(graph.getStart())) {
                weights.put(element, new DistFromSource(Integer.MAX_VALUE, null));
            }
        }
        //initialize start vertex neighbors with the correct parent and calculated distance.
        for (Node<Index> neighbor : neighbors[0]) {
            weights.get(neighbor).setParent(neighbor.getParent());
            weights.get(neighbor).setDistance((graph.getStart().getValue() + neighbor.getValue()));

            // System.out.println(weights.get(neighbor).getDistance() + "\n");

        }
        allMatrix.remove(graph.getStart());
        Collection<Node<Index>> finalAllMatrix = allMatrix;

        //create runnable variable that holds a thread pool and executing the bellman algorithm in multiple thread simultaneously
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
                                    weights.get(neighbor).setDistance((weights.get(element).getDistance() + neighbor.getValue()));
                                    weights.get(neighbor).setParent(element);
                                    try{
//                                        extracted = primitiveMatrix[index.row][index.column+1];
//                                        list.add(new Index(index.row,index.column+1));
                                    }catch (ArrayIndexOutOfBoundsException ignored){}
                                }
                            }
                        }
                    }
                };

                for (int i = 0; i < finalAllMatrix.size(); i++) {
                    executor.submit(r2);
                }
                executor.shutdown();
            }
        };

        //execute the bellman algorithm using a Thread in function in class Bellman that will initiate the multi threading calculation.
        mainThread = new Thread(r);
        mainThread.start();


        //print the map **optional**
        for (Node<Index> name: weights.keySet()) {
            String key = name.toString();
            int value = weights.get(name).getDistance();
            Node<Index> par= weights.get(name).getParent();
            System.out.println(key + " " + value + " " + par);
        }

        //create a list of the cheapest path from starting vertex to destination vertex
        // using the map that contains the cheapest path from start vertex to every vertex in the matrix.
        List<T> pathList = new ArrayList<>();
        pathList.add((T) graph.getDestination().getData());
        Node temp = weights.get(graph.getDestination()).getParent();
        while (!temp.equals(graph.getStart())) {
            pathList.add((T) temp.getData());
            temp = weights.get(temp).getParent();
        }
        pathList.add((T) graph.getStart().getData());

        return pathList;

    }

    @Override
    public void run() {

    }

}