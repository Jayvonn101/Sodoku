package Game_Generator;

import java.util.Random;

public class Sodoku{
    int row;
    int col;
    int [][] grid;

    public Sodoku(int rows, int cols ) {
        this.row = rows;
        this.col = cols;
        this.grid = new int[rows][cols];
    }
    
    public void validateGrid(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than zero.");
        }
        if (rows != cols) {
            throw new IllegalArgumentException("Sodoku grid must be square (same number of rows and columns).");
        }
        this.row = rows;
        this.col = cols;
        this.grid = new int[rows][cols];

    }

   public void fillNums(){
        Random rand = new Random();
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                this.grid[i][j] = rand.nextInt(row + 1);
            }
        }
    }
    public void printNums(){
        for (int[] gridRow : grid){
            for (int num : gridRow){
                System.out.print(num + " ");
            }
                System.out.print("\n");
        }
    }
}