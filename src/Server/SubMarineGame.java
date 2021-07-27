package Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This Class represent a SubMarineGame it takes :
 * 1. Matrix nXm and by DFS algorithm get indices and save it to a set
 *
 */
public class SubMarineGame<T> {

    private List<HashSet<Index>> sets;
    private ReadWriteLock readWriteLock;
    private ThreadPoolExecutor threadPoolExecutor;
    private List<List> listGroups;
    private List<Callable<T>> futureTasksList;


    /**
     * Constractor to initiate SubMarineGame,The Matrix that sent creating a Hashset
     * drived from DFS Traversal findGroups() {@link Matrix} and save the results to list of indices
     * @param generateMatrix get a Matrix {@link Matrix} to work with
     */
    public SubMarineGame(Matrix generateMatrix) {
        this.sets = generateMatrix.findGroups(generateMatrix);
        listGroups = generateMatrix.convertHashToList(this.sets);
        futureTasksList = new ArrayList<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * This Method invoke the checking of SubMarineGame
     * In this method we split the sets of indices to run on 4 threads as parallel
     *
     * @return The number of the Submarines on the matrix or -1 of there is none Submarines
     */

    public Integer getSubMarine() {
        //Save all kind of objects
        List<Object> data = new ArrayList<>();
        //get the list from the sets
        data = listGroups.stream().collect(Collectors.toList());
        ArrayList<Index> indices;
        Integer countShip = 0;
        threadPoolExecutor = new ThreadPoolExecutor(4,7,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>());

        try {
            for (int i = 0; i < data.size(); i++) {
                //Start adding all the tasks to list of tasks to run
                //them together
                indices = (ArrayList<Index>) data.get(i);
                futureTasksList.add(runSets(indices));
            }
        }catch (ClassCastException e){
            System.out.println("Error");
        }

         try {
            //Now invoke all of the tasks
            //make them run parallel
            List<Future<T>> listOfFutureTasks = threadPoolExecutor.invokeAll(futureTasksList);

            //move all over the results
            for (Future<T> currResult: listOfFutureTasks) {
                // lock and unlock writing to threads the thread
                readWriteLock.writeLock().lock();
                countShip += (Integer)currResult.get();
                readWriteLock.writeLock().unlock();

            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //shut down the thread pool
        threadPoolExecutor.shutdown();

        if (countShip == listGroups.size()){
            return countShip;
        }
        else {
            return -1;
        }
    }


    /**
     * This method runs as Callable task for each set of indices and check if the indices creates a submarine
     * The algorithm works like that : check the possible columns and rows from the List of indices
     * and count them
     * @param indices One set of indices to calculate
     * @return 1 found submarine 0 if not
     */
    private Callable runSets(ArrayList<Index> indices) {
        Callable <Integer> matrixCallable = (()-> {
            int col=1,row=1;
            if (indices.size() == 1){
                return 0;
            }
            else
            {
                for(int i=1 ; i < indices.size() ; i++) {
                    if(indices.get(i).getColumn() - indices.get(i-1).getColumn() ==1){
                        col ++;
                    }else if(indices.get(i).getColumn() - indices.get(i-1).getColumn() < 0)
                        col=1;

                    if(indices.get(i).getRow() - indices.get(i-1).getRow() == 1 ){
                        row ++;
                    }
                }
            }
            if (col*row == indices.size() && indices.size() != 1 ){
                return 1;
            }
            return 0;
        });

    return matrixCallable;
    }

    /**
     * Kill the Thread pool
     */
    public void drian (){
        threadPoolExecutor = null;
    }


//    public static int[][] makeTheMatrix(){
//        //TO BE DEL AFTER
//        int[][] source = {
//                {1, 1, 1, 0, 1, 0, 1},
//                {0, 0, 0, 0, 1, 0, 1},
//                {1, 0, 0, 0, 0, 0, 1},
//                {1, 0, 0, 0, 0, 0, 1},
//                {1, 0, 0, 1, 1, 0, 1}
//
//        };
//        return source;
//    }
//
//    public static void main(String[] args) throws Exception {
//        Matrix matrix = new Matrix(makeTheMatrix());
//        SubMarineGame sub = new SubMarineGame(matrix);
//        int ships =  sub.getSubMarine();
//        if (ships != -1)
//            System.out.println("You Have " + ships + " Ships" );
//        else
//            System.out.println("You Have incorrect Input");
//
//
//  }

}
