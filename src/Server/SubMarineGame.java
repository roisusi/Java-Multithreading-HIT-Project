package Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SubMarineGame<T> {

    private List<HashSet<Index>> sets;
    private ReadWriteLock readWriteLock;
    private Future<Integer> futureStock ;
    private ThreadPoolExecutor threadPoolExecutor;
    private List<List> listGroups;
    private List<Callable<T>> futureTasksList;


    public SubMarineGame(Matrix generateMatrix) {
        this.sets = generateMatrix.findGroups(generateMatrix);
        listGroups = generateMatrix.convertHashToList(this.sets);
        futureTasksList = new ArrayList<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    public Integer checkIfSubMarine() throws Exception {
        //System.out.println(listGroups);
        List<Object> data = new ArrayList<>();
        data = listGroups.stream().collect(Collectors.toList());
        ArrayList<Index> indices;
        Integer countShip = 0;
        threadPoolExecutor = new ThreadPoolExecutor(4,7,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>());

        try {
            for (int i = 0; i < data.size(); i++) {

                indices = (ArrayList<Index>) data.get(i);
                futureStock = threadPoolExecutor.submit(runSets(indices));
                futureTasksList.add(runSets(indices));

            }
        }catch (ClassCastException e){
            System.out.println("Error");
        }

        try {
            // Now invoke all of the tasks
            //make them run parallel
            List<Future<T>> allResultsAsFuture = threadPoolExecutor.invokeAll(futureTasksList);

            for (Future<T> currResult: allResultsAsFuture) {
                readWriteLock.writeLock().lock();
                countShip += (Integer)currResult.get();
                readWriteLock.writeLock().unlock();

                //System.out.println("Future getting : " + futureStock.get() + " is Done ? " + futureStock.isDone() );

            }
        } catch (InterruptedException | ExecutionException e) {
            // Handle the exception here.. Somehow...
            e.printStackTrace();
        }

        //countShip +=futureStock.get();

        threadPoolExecutor.shutdown();

        if (countShip == listGroups.size()){
            return countShip;
        }
        else {
            return -1;
        }
    }

    public Callable runSets(ArrayList<Index> indices) {
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
                System.out.println( "Ships is calculate on Thread : " + Thread.currentThread().getName());
                return 1;
            }
            System.out.println("Row is : " + row + " and Col is : " + col + " Threads : " + Thread.currentThread().getName());
            return 0;
        });

    return matrixCallable;
    }

    public void drian (){
        threadPoolExecutor = null;
    }


    public static int[][] makeTheMatrix(){
        //TO BE DEL AFTER
        int[][] source = {
                {1, 1, 1, 0, 1, 0, 1},
                {0, 0, 0, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 1, 1, 0, 1}

        };
        return source;
    }

    public static void main(String[] args) throws Exception {
        Matrix matrix = new Matrix(makeTheMatrix());
        SubMarineGame sub = new SubMarineGame(matrix);
        int ships =  sub.checkIfSubMarine();
        if (ships != -1)
            System.out.println("You Have " + ships + " Ships" );
        else
            System.out.println("You Have incorrect Input");


  }

}
