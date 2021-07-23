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
                    objectOutputStream.writeObject(this.matrix);
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
                            System.out.println("Class Exception Not good");
                        }
                    }
                    break;
                }

                case "stop":{
                    doWork = false;
                    break;
                }
            }





        }

    }
}
