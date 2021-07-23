package Server;

import java.io.*;
import java.util.List;

public class MatrixIHandler implements IHandler {
    private Matrix matrix;
    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        // ObjectInputStream is a wrapper (decorator) - wraps an InputStream and add functionality
        // treats data as primitives/objects
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        // ObjectInputStream is a wrapper (decorator) - wraps an OutputStream and add functionality
        // treats data as primitives/objects
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);

        boolean doWork = true;
        while(doWork){
            /*
            Use-cases:
            - client sends a 2d array. handler converts to a Server.Matrix object. command name: "matrix"
            - client sends an Server.Index and wishes to get its neighbors. command: "neighbors"
            - client sends an Server.Index and wishes to get its reachable indices command: "reachables"
            - client sends a start and end Server.Index and wishes to get all possible routes between them
            ....

            primitives: byte,short,int,long,float,double,char,boolean

             */
            switch (objectInputStream.readObject().toString()){
                case "matrix":{
                     int[][] tempArray = (int[][])objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client");
                    this.matrix = new Matrix(tempArray);
                    this.matrix.printMatrix();
                    break;
                }
                case "neighbors":{
                    if (this.matrix!=null){
                        Index tempIndex = (Index)objectInputStream.readObject();
                        List<Index> neighbors = this.matrix.getNeighbors(tempIndex);
                        System.out.println("Neighbors of " + tempIndex + ": " + neighbors);
                        objectOutputStream.writeObject(neighbors);
                    }
                    break;
                }

                case "reachables":{
                    if (matrix!=null){
                        Index tempIndex = (Index)objectInputStream.readObject();
                        List<Index> reachables = matrix.getReachables(tempIndex);
                        System.out.println("Reachables indices of " + tempIndex + ": " + reachables);
                        objectOutputStream.writeObject(reachables);
                    }
                    break;
                }

                case "subGame":{
                    if (matrix!=null){
                        try {
                            SubMarineGame subMarineGame = new SubMarineGame(matrix);
                            int numOfShip = subMarineGame.checkIfSubMarine();
                            objectOutputStream.writeObject(numOfShip);
                        }catch (Exception e){
                            System.out.println("Not good");
                        }

                    }
                    break;
                }

                case "cheapest path":{
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
