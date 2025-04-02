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
    // todo: Implement algorithm to generate valid sodoku grid (look up)
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