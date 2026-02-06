package Game_Generator;

import java.util.Random;

public class Sodoku {
    private int row;
    private int col;
    private int[][] grid;

    public Sodoku(int rows, int cols) {
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

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void fillNums() {
        fillWithBacktracking();
    }

    private boolean fillWithBacktracking() {
        int[] cell = findEmptyCell();
        if (cell == null) {
            return true;
        }
        int i = cell[0];
        int j = cell[1];

        int[] nums = shuffledNumbers();
        for (int num : nums) {
            if (isValid(i, j, num)) {
                grid[i][j] = num;
                if (fillWithBacktracking()) {
                    return true;
                }
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
                placed++;
            }
        }
    }

    public boolean placeNum(int r, int c, int num) {
        if (r < 0 || r >= row || c < 0 || c >= col) {
            System.out.println("Cell is out of bounds.");
            return false;
        }
        if (grid[r][c] != 0) {
            System.out.println("Cell is already occupied.");
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

    public boolean isSolved() {
        return findEmptyCell() == null;
    }

    public boolean solve() {
        int[] cell = findEmptyCell();
        if (cell == null) {
            return true;
        }
        int i = cell[0];
        int j = cell[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(i, j, num)) {
                grid[i][j] = num;
                if (solve()) {
                    return true;
                }
                grid[i][j] = 0;
            }
        }
        return false;
    }

    private int[] findEmptyCell() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (grid[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private boolean isValid(int r, int c, int num) {
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
        for (int[] gridRow : grid) {
            for (int num : gridRow) {
                System.out.print((num == 0 ? "." : num) + " ");
            }
            System.out.println();
        }
    }
}
