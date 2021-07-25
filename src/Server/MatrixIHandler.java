package Server;

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
                    matrix = (Matrix)objectInputStream.readObject();
                    matrix.printMatrix();
                    List<List<Index>> paths = matrix.findPaths(matrix,new Index(5,5),new Index(2,2));
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
                            int numOfShip = subMarineGame.checkIfSubMarine();
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
