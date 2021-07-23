package Client;

import Server.Index;
import Server.Matrix;
import Server.SubMarineGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        /**
         * TODO:
         * make sure to get a matrix before getting reachables,neighbors commands
         * prompt the user to enter one of the following commands: matrix,reachables,neighbors
         * if token is not valid, prompt the user to enter a valid command
         * if the user entered either reachables or neighbors command: get index according
         * to the following pattern: (rowNumber,columnNumber).
         * Hint: use split() method to split the input and create an Server.Index object
         * use  Integer.parseInt() to convert a string into an integer number
         * validation of number (either floating point number or int is not mandatory)
         *
         * EXTRA:
         * get input arrays from file  using FileInputStream
         * https://docs.oracle.com/javase/7/docs/api/java/io/FileInputStream.html#:~:text=A%20FileInputStream%20obtains%20input%20bytes%20from%20a%20file%20in%20a%20file%20system.&text=FileInputStream%20is%20meant%20for%20reading,%2C%20FileDescriptor%20%2C%20FileOutputStream%20%2C%20Files.
         */


        Socket socket = new Socket("127.0.0.1", 8010);
        System.out.println("client: Socket was created");
        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
        boolean stop = false;


        while (!stop) {



            // sending #1 matrix
            int[][] source = {
                    {0, 0, 1},
                    {1, 0, 1},
                    {1, 0, 0}
            };

            Matrix matrix = new Matrix(source);
            System.out.println("Please enter one of the following: matrix,reachables,neighbors");
            Scanner scanner = new Scanner(System.in);
            String readFromUser = scanner.nextLine();
            //send "matrix" command then write 2d array to socket

            switch (readFromUser) {

                case "matrix" : {
                    toServer.writeObject("matrix");
                    toServer.writeObject(source);
                    break;
                }
                case "reachables" :{
                    //send "reachables" command then write an index to socket
                    toServer.writeObject("reachables");
                    toServer.writeObject(new Index(1, 1));

                    // get reachable indices as list
                    List<Index> reachables =
                            new ArrayList<Index>((List<Index>) fromServer.readObject());
                    System.out.println("from client - Reachable Indices are:  " + reachables);
                    break;
                }
                case "neighbors" :{
                    //send "neighbors" command then write an index to socket
                    toServer.writeObject("neighbors");
                    toServer.writeObject(new Index(1, 1));

                    // get neighboring indices as list
                    List<Index> AdjacentIndices =
                            new ArrayList<Index>((List<Index>) fromServer.readObject());
                    System.out.println("from client - Neighboring Indices are: " + AdjacentIndices);
                    break;
                }

                case "3" :{
                    //send "neighbors" command then write an index to socket
                    toServer.writeObject("subGame");
                    //toServer.writeObject(new Matrix(source));

                    // get neighboring indices as list
//                    List<Index> AdjacentIndices =
//                            new ArrayList<Index>((List<Index>) fromServer.readObject());
//                    System.out.println("from client - Neighboring Indices are: " + AdjacentIndices);
                    Object integer = fromServer.readObject();
                    System.out.println("Number of Ships in");
                    matrix.printMatrix();
                    System.out.println("is " + integer);
                    break;
                }
                case "cheapest path": {
                    System.out.println("type source and destination index:");
                    toServer.writeObject("cheapest path");

                    //send start and dest index that scanned from user
//                    toServer.writeObject(readIndex());
//                    toServer.writeObject(readIndex());
                    toServer.writeObject(new Index(1, 0));
                    toServer.writeObject(new Index(2, 2));

                    // get cheapest path as list
                    List<Index> cheapest =
                            new ArrayList<Index>((List<Index>) fromServer.readObject());
                    System.out.println("from client - Cheapest Path is: " + cheapest);
                    break;
                }

                case "stop" :{
                    toServer.writeObject("stop");
                    fromServer.close();
                    toServer.close();
                    socket.close();
                    stop = true;
                    break;
                }

            }

        }

        System.out.println("client: Closed operational socket");


    }
}
