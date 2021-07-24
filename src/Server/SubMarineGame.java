package Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SubMarineGame {

    private List<HashSet<Index>> sets;
    private ReadWriteLock readWriteLock;
    private Future<Integer> futureStock ;
    private ThreadPoolExecutor threadPoolExecutor;
    private List<List> listGroups;


    public SubMarineGame(Matrix generateMatrix) {
        this.sets = generateMatrix.findGroups(generateMatrix);
        listGroups = generateMatrix.convertHashToList(this.sets);

        readWriteLock = new ReentrantReadWriteLock();
    }

    public Integer checkIfSubMarine() throws Exception {
        //System.out.println(listGroups);
        readWriteLock.writeLock().lock();
        List<Object> data = new ArrayList<>();
        data = listGroups.stream().collect(Collectors.toList());
        ArrayList<Index> indices;
        Integer countShip = 0;
        threadPoolExecutor = new ThreadPoolExecutor(2,5,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>());

        try {
            for (int i = 0; i < data.size(); i++) {

                indices = (ArrayList<Index>) data.get(i);

                futureStock = threadPoolExecutor.submit(runSets(indices));

                //System.out.println("Future getting : " + futureStock.get() + " is Done ? " + futureStock.isDone() );
                countShip +=futureStock.get();
            }
        }catch (ClassCastException e){
            System.out.println("Error");
        }

        threadPoolExecutor.shutdown();

        if (countShip == listGroups.size()){
            readWriteLock.writeLock().unlock();

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
                //System.out.println( "Ships is calculate on Thread : " + Thread.currentThread().getName());
                return 1;
            }
            //System.out.println("Row is : " + row + " and Col is : " + col + " Threads : " + Thread.currentThread().getName());
            return 0;
        });

    return matrixCallable;
    }

    public void drian (){
        threadPoolExecutor.shutdownNow();
    }


    public static int[][] makeTheMatrix(){
        //TO BE DEL AFTER
        int[][] source = {
                {1, 1, 1, 0, 1, 0, 1},
                {0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 0, 0, 0, 1},
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
