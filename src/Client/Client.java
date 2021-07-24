package Client;

import Server.Index;
import Server.Matrix;
import Server.SubMarineGame;

import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
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
        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
        //System.out.println("client: Socket was created");

        boolean stop = false;
        boolean showMenu = true;
        Matrix matrix = new Matrix();
        Scanner scannerMatrix;
        String readFromUser = "";

        // sending #1 matrix
        int[][] matrix1 = {
                {0, 0, 1},
                {1, 0, 1},
                {1, 0, 0}
        };

        int[][] matrix2 = {
                {1, 1, 1, 0, 1, 0, 1},
                {0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 1, 1, 0, 1}

        };
        int[][] matrix3 = {
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1},
                {1, 0, 0, 1, 1}

        };
        int[][] matrix4 = {
                {1, 1, 0, 1, 1},
                {0, 0, 0, 1, 1},
                {1, 1, 0, 1, 1}
        };
        int[][] matrix5NotGood = {
                {1, 1, 0, 1, 1},
                {0, 0, 0, 1, 1},
                {1, 0, 0, 1, 1}

        };


        Matrix buildMatrix1 = new Matrix(matrix1);
        Matrix buildMatrix2 = new Matrix(matrix2);
        Matrix buildMatrix3 = new Matrix(matrix3);
        Matrix buildMatrix4 = new Matrix(matrix4);
        Matrix buildMatrix5 = new Matrix(matrix5NotGood);


        while (!stop) {


            //System.out.println("Please enter one of the following: matrix,reachables,neighbors");

            if (showMenu) {
                System.out.println("Hello and welcome to Matrix-Graphs games\n" +
                        "First lets build matrix for our game:\n" +
                        "Select Matrix from the list or you can choose option 6 for a random one\n" +
                        "1.\n" + buildMatrix1 + "\n" +
                        "2.\n" + buildMatrix2 + "\n" +
                        "3.\n" + buildMatrix3 + "\n" +
                        "4.\n" + buildMatrix4 + "\n" +
                        "5.\n" + buildMatrix5 + "\n" +
                        "6.\nBuild your own Matrix\n");

                scannerMatrix = new Scanner(System.in);
                readFromUser = scannerMatrix.nextLine();
                //send "matrix" command then write 2d array to socket

                switch (readFromUser) {
                    case "1": {
                        toServer.writeObject("matrix");
                        toServer.writeObject(matrix1);
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("you Chose : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    case "2": {
                        toServer.writeObject("matrix");
                        toServer.writeObject(matrix2);
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("you Chose : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    case "3": {
                        toServer.writeObject("matrix");
                        toServer.writeObject(matrix3);
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("you Chose : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    case "4": {
                        toServer.writeObject("matrix");
                        toServer.writeObject(matrix4);
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("you Chose : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    case "5": {
                        toServer.writeObject("matrix");
                        toServer.writeObject(matrix5NotGood);
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("you Chose : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    case "6": {
                        boolean isNumberOK = true;
                        System.out.println("Build you own Matrix until 1000X1000");
                        toServer.writeObject("buildRandomMatrix");

                        while (isNumberOK) {
                            System.out.println("Please Enter Columns");
                            readFromUser = scannerMatrix.nextLine();
                            isNumberOK = catchNotANumber(readFromUser);
                            if (!isNumberOK) {
                                toServer.writeObject(Integer.parseInt(readFromUser));
                            } else continue;
                            System.out.println("Please Enter Rows");
                            readFromUser = scannerMatrix.nextLine();
                            isNumberOK = catchNotANumber(readFromUser);
                            if (!isNumberOK) {
                                toServer.writeObject(Integer.parseInt(readFromUser));
                            } else continue;
                        }
                        matrix = (Matrix) fromServer.readObject();
                        System.out.println("Your Random Matrix is : \n" + matrix);
                        showMenu = false;
                        break;
                    }
                    default:
                        showMenu = true;

                }
            }


            if (showMenu == false) {
                System.out.println("Please select What Game you want to play with the ");
                Scanner scannerOption = new Scanner(System.in);
                readFromUser = scannerOption.nextLine();

                switch (readFromUser) {
                    case "1": {
                        toServer.writeObject("findPaths");
                        toServer.writeObject(matrix);
                        Object paths = (List<HashSet<Index>>) fromServer.readObject();
                        System.out.println("The Indices of: ");
                        if (matrix != null)
                            matrix.printMatrix();
                        System.out.println("is " + paths);
                        break;
                    }
                    case "2": {
                        System.out.println();
                        System.out.println("For shortest path we will use spacial nXn Matrix up to 50x50");
                        if (matrix.getColNumber() != matrix.getRowNumber() || matrix.getRowNumber() > 50 || matrix.getColNumber() > 50) {
                            System.out.println("Sorry your Matrix is not nXn or above 50x50");
                            break;
                        }
                        System.out.println();
                        toServer.writeObject("findIndices");
                        toServer.writeObject(matrix);
                        Object integer = fromServer.readObject();
//                        System.out.println("Number of Ships in");
//                        if (matrix != null)
//                            matrix.printMatrix();
                        System.out.println("is " + integer);
                        break;
                    }

                    case "3": {
                        toServer.writeObject("subGame");
                        toServer.writeObject(matrix);
                        Object integer = fromServer.readObject();
                        System.out.println("Number of Ships in");
                        if (matrix != null)
                            matrix.printMatrix();
                        System.out.println("is " + integer);
                        break;
                    }
                    case "5": {
                        showMenu = true;
                        break;
                    }
                    case "stop": {
                        toServer.writeObject("stop");
                        fromServer.close();
                        toServer.close();
                        socket.close();
                        stop = true;
                        break;
                    }
                    default:
                        System.out.println("Please Select from the Menu or \"5\" to choose different Matrix or \"stop\" to exit");
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

                }

            }

            System.out.println("client: Closed operational socket");


        }
    }

    public static boolean catchNotANumber(String readFromUser){
        boolean isNumberOK = true;
        try {
            Integer intValue = Integer.parseInt(readFromUser);
            Exception exception = new Exception();
            if (intValue > 1000 || intValue < 2) throw exception;
            isNumberOK = false;
        } catch (NumberFormatException e) {
            System.out.println("Input is not a number");
        }catch (Exception e){
            System.out.println("Values need to be positive and from 2 to 1000");
        }

        return isNumberOK;
    }

}