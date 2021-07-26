package Server;

import Client.Client;

/**
This Class gets jobs from the server as sent from the client and invvoke them
 */

import java.io.*;
import java.util.List;

public class MatrixIHandler implements IHandler {
    private Matrix matrix = new Matrix();
    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        // ObjectInputStream is a wrapper (decorator) - wraps an InputStream and add functionality
        // treats data as primitives/objects
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        // ObjectInputStream is a wrapper (decorator) - wraps an OutputStream and add functionality
        // treats data as primitives/objects
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);


        /**
            Client can choose a job from the menu what he wish do to he send a objectInputStream using a Sting
            the matrix contain 1 and 0 except Task 4 which has random positive numbers as follow :
            1. matrix - build a matrix using in-client static Matrix
            2. buildRandomMatrix - build Client-Own random Matrix up to 1000X1000
            3. findIndices - Task 1 - find indices of a Matrix
            4. findPaths - Task 2 - find the shortest path of Matrix
            5. subGame - Task 3 - find a submarine in a matrix
            6. cheapest path - Task 4 - find Cheapest path on regular matrix that the cells contain a wight to travel
        */


        boolean doWork = true;
        while(doWork){
            switch (objectInputStream.readObject().toString()){
                case "matrix":{
                    int[][] tempArray = (int[][])objectInputStream.readObject();
                    this.matrix = new Matrix(tempArray);
                    objectOutputStream.writeObject(this.matrix);
                    break;
                }
                case "buildRandomMatrix":{
                    int n= (Integer)objectInputStream.readObject();
                    int m= (Integer)objectInputStream.readObject();
                    this.matrix = new Matrix(n,m);
                    objectOutputStream.writeObject(this.matrix);
                    break;
                }
                case "findIndices":{
                    //Need to inset index and a msg to say that there is not a path
                    matrix = (Matrix)objectInputStream.readObject();
                    matrix.printMatrix();
                    Index index1 = (Index) objectInputStream.readObject();
                    Index index2 = (Index) objectInputStream.readObject();
                    List<List<Index>> paths = matrix.findPaths(matrix,index1,index2);
                    System.out.println(paths);
                    objectOutputStream.writeObject(matrix.printPath(paths));
                    break;
                }
                case "findPaths":{
                    matrix = (Matrix)objectInputStream.readObject();
                    objectOutputStream.writeObject(matrix.findGroups(matrix));
                    break;
                }
                case "subGame":{
                    matrix = (Matrix)objectInputStream.readObject();
                    if (matrix!=null){
                        try {
                            SubMarineGame subMarineGame = new SubMarineGame(matrix);
                            int numOfShip = subMarineGame.getSubMarine();
                            objectOutputStream.writeObject(numOfShip);
                            subMarineGame.drian();
                        }catch (Exception e){
                            System.out.println("Class Exception Not good " + e);
                        }
                    }
                    break;
                }

                case "cheapest path":{
                    matrix = (Matrix)objectInputStream.readObject();
                    if (matrix!=null){
                        Index strIndex = (Index)objectInputStream.readObject();
                        Index endIndex = (Index)objectInputStream.readObject();

                        CheapestPathMatrix myPath = new CheapestPathMatrix(matrix);
                        myPath.setStartIndex(strIndex);
                        myPath.setDestIndex(endIndex);
                        Bellman doBellman = new Bellman();
                        List<Index> rList = (List<Index>) doBellman.lightPath(myPath);
                        System.out.println("cheapest path from: " + strIndex + "to: " + endIndex + " is: " + rList);
                        objectOutputStream.writeObject(rList);
                    }
                }
                break;
                case "stop":{
                    doWork = false;
                    break;
                }
            }





        }

    }
}
