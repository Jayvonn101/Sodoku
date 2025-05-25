package Game_Generator;

import java.util.Random;

public class Sodoku{
    int row;
    int col;
    int [][] grid;


    public Sodoku(int rows, int cols ) {

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
                this.grid[i][j] = rand.nextInt(row) + 1;
            }
        }
    }
    
    public void placeEmptyCells(int emptyCells){
        if (emptyCells < 0 || emptyCells > row * col) {
            throw new IllegalArgumentException("Number of empty cells must be between 0 and " + (row * col));
        }
        Random rand = new Random();
        int placed = 0;
        while (placed < emptyCells) {
            int r = rand.nextInt(row);
            int c = rand.nextInt(col);
            if (this.grid[r][c] != 0) { // Assuming 0 represents an empty cell
                this.grid[r][c] = 0; // Place an empty cell
                placed++;
            }
        }
    }
    
    
    public boolean  placeNums(int num){
       for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (this.grid[i][j] == 0) { // Assuming 0 represents an empty cell
                    this.grid[i][j] = num;
                    return true; // Return the number placed
                }
            }
        }
        return false; // No empty cell found
    }
    public void selectCell(int r, int c) {
        if (r < 0 || r >= row || c < 0 || c >= col) {
            throw new IllegalArgumentException("Selected cell is out of bounds.");
        }
        if (this.grid[r][c] != 0) {
            throw new IllegalArgumentException("Cell is already occupied.");
        }
    } 

     public void printNum(){
        for (int[] gridRow : grid){
            for (int num : gridRow){
                System.out.print((num == 0 ? "." : num) + " "); // Print '.' for empty cells
            }
         System.out.print("\n");
        }
    }


}