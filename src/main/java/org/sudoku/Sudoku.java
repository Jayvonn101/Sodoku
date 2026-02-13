package org.sudoku;

import java.util.Random;
import java.io.Serializable;

public class Sudoku implements Serializable {
    private static final long serialVersionUID = 1L;
    private int row;
    private int col;
    private int[][] grid;
    private int[][] solution;
    private boolean[][] fixedCells;

    public Sudoku(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be greater than zero.");
        }
        if (rows != cols) {
            throw new IllegalArgumentException("Sudoku grid must be square (same number of rows and columns).");
        }
        this.row = rows;
        this.col = cols;
        this.grid = new int[rows][cols];
        this.solution = new int[rows][cols];
        this.fixedCells = new boolean[rows][cols];
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int[][] getGrid() { return grid; }
    public void setGrid(int[][] grid) { this.grid = grid; }
    public boolean[][] getFixedCells() { return fixedCells; }
    public void setFixedCells(boolean[][] fixed) { this.fixedCells = fixed; }

    public void fillNums() {
        fillWithBacktracking();
        for (int i = 0; i < row; i++) {
            System.arraycopy(grid[i], 0, solution[i], 0, col);
        }
    }

    private boolean fillWithBacktracking() {
        int[] cell = findEmptyCell();
        if (cell == null) return true;
        int i = cell[0], j = cell[1];

        int[] nums = shuffledNumbers();
        for (int num : nums) {
            if (isValid(i, j, num)) {
                grid[i][j] = num;
                if (fillWithBacktracking()) return true;
                grid[i][j] = 0;
            }
        }
        return false;
    }

    private int[] shuffledNumbers() {
        Random rand = new Random();
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int k = nums.length - 1; k > 0; k--) {
            int swap = rand.nextInt(k + 1);
            int temp = nums[k];
            nums[k] = nums[swap];
            nums[swap] = temp;
        }
        return nums;
    }

    public void placeEmptyCells(int emptyCells) {
        if (emptyCells < 0 || emptyCells > row * col) {
            throw new IllegalArgumentException("Number of empty cells must be between 0 and " + (row * col));
        }
        Random rand = new Random();
        int placed = 0;
        while (placed < emptyCells) {
            int r = rand.nextInt(row);
            int c = rand.nextInt(col);
            if (grid[r][c] != 0) {
                grid[r][c] = 0;
                fixedCells[r][c] = true;
                placed++;
            }
        }
    }

    public boolean placeNum(int r, int c, int num) {
        if (r < 0 || r >= row || c < 0 || c >= col) {
            System.out.println("Cell is out of bounds.");
            return false;
        }
        if (fixedCells[r][c]) {
            System.out.println("Cannot modify this cell - it's part of the puzzle.");
            return false;
        }
        if (num < 1 || num > 9) {
            System.out.println("Number must be between 1 and 9.");
            return false;
        }
        if (!isValid(r, c, num)) {
            System.out.println("Invalid move! " + num + " conflicts with row, column, or box.");
            return false;
        }
        grid[r][c] = num;
        return true;
    }

    public boolean removeNum(int r, int c) {
        if (r < 0 || r >= row || c < 0 || c >= col) {
            System.out.println("Cell is out of bounds.");
            return false;
        }
        if (fixedCells[r][c]) {
            System.out.println("Cannot remove this cell - it's part of the puzzle.");
            return false;
        }
        grid[r][c] = 0;
        return true;
    }

    public Hint getHint() {
        int[] cell = findEmptyCell();
        if (cell == null) return null;
        int r = cell[0], c = cell[1];
        return new Hint(r, c, solution[r][c]);
    }

    public boolean isSolved() {
        return findEmptyCell() == null;
    }

    public boolean solve() {
        int[] cell = findEmptyCell();
        if (cell == null) return true;
        int i = cell[0], j = cell[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(i, j, num)) {
                grid[i][j] = num;
                if (solve()) return true;
                grid[i][j] = 0;
            }
        }
        return false;
    }

    private int[] findEmptyCell() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j] == 0) return new int[]{i, j};
            }
        }
        return null;
    }

    public boolean isValid(int r, int c, int num) {
        for (int j = 0; j < col; j++) {
            if (grid[r][j] == num) return false;
        }
        for (int i = 0; i < row; i++) {
            if (grid[i][c] == num) return false;
        }
        int boxRow = (r / 3) * 3;
        int boxCol = (c / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (grid[i][j] == num) return false;
            }
        }
        return true;
    }

    public void printGrid() {
        System.out.println();
        System.out.print("    ");
        for (int j = 0; j < col; j++) {
            System.out.print((j + 1) + " ");
            if ((j + 1) % 3 == 0 && j < col - 1) System.out.print("| ");
        }
        System.out.println();
        System.out.print("   ");
        for (int j = 0; j < col * 2 + 5; j++) System.out.print("-");
        System.out.println();

        for (int i = 0; i < row; i++) {
            System.out.print((i + 1) + " | ");
            for (int j = 0; j < col; j++) {
                String cell = grid[i][j] == 0 ? "." : String.valueOf(grid[i][j]);
                if (fixedCells[i][j] && grid[i][j] != 0) {
                    System.out.print("[" + cell + "]");
                } else {
                    System.out.print(" " + cell + " ");
                }
                if ((j + 1) % 3 == 0 && j < col - 1) System.out.print("|");
            }
            System.out.println();
            if ((i + 1) % 3 == 0 && i < row - 1) {
                System.out.print("   ");
                for (int j = 0; j < col * 2 + 5; j++) System.out.print("-");
                System.out.println();
            }
        }
        System.out.println();
    }

    public static class Hint {
        public final int row;
        public final int col;
        public final int value;
        
        public Hint(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
}
