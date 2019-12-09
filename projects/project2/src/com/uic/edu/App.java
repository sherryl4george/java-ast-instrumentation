package com.uic.edu;
import java.util.*;

class MR{
    static List<String> ss = new ArrayList<>();
}

public class App {
    // An Inplace function to rotate a N x N matrix
    // by 90 degrees in anti-clockwise direction
    static void rotateMatrix(int N, int mat[][])
    {
        // Consider all squares one by one
        for (int x = 0; x < N / 2; x++)
        {
            // Consider elements in group of 4 in
            // current square
            for (int y = x; y < N-x-1; y++)
            {
                // store current cell in temp variable
                int temp = mat[x][y];

                // move values from right to top
                mat[x][y] = mat[y][N-1-x];

                // move values from bottom to right
                mat[y][N-1-x] = mat[N-1-x][N-1-y];

                // move values from left to bottom
                mat[N-1-x][N-1-y] = mat[N-1-y][x];

                // assign temp to left
                mat[N-1-y][x] = temp;
            }
        }
    }

    // Function to print the matrix
    static void displayMatrix(int N, int mat[][])
    {
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++) {
                System.out.print(" " + mat[i][j]);
            }

            System.out.print("\n");
        }
        System.out.print("\n");
    }

    /* Driver program to test above functions */
    public static void main (String[] args)
    {
        if(args.length < 1)
            System.out.println("No arguments passed! Exiting execution");

        int N = Integer.parseInt(args[0]);

        int mat[][] = new int[N][N];
        Random rand = new Random();
        for(int i=0; i<N; i++) {
            for (int j = 0; j < N; j++) {
                mat[i][j] = rand.nextInt(100);
            }
        }
        System.out.println("Starting Matrix: ");
        displayMatrix(N,mat);

        rotateMatrix(N,mat);

        // Print rotated matrix
        System.out.println("Rotated Matrix: ");
        displayMatrix(N,mat);
    }
}