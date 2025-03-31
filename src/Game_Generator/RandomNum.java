package Game_Generator;
import java.util.Random;
import java.util.Scanner;
// This class is for generating random numbers

public class RandomNum {
    private int row;
    private int col;
    private int max;
    private int[][] grid;

    // Constructor to initialize the grid size
    public RandomNum(int row, int col) {
        this.row = row;
        this.col = col;
        this.max = max;
        this.grid = new int[row][col];
    }

    // Method to fill the grid with random numbers
    public void fillGrid() {
        Random random = new Random();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                grid[i][j] = random.nextInt(100); // Random number between 0 and 99
            }
        }
    }

    // Method to print the grid
    public void printGrid() {
        for (int[] row : grid) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //int ran_max = Random.nextInt(row - 1);
        
        System.out.print("Enter the number of rows: ");
        int rows = scanner.nextInt();
        
        System.out.print("Enter the number of columns: ");
        int cols = scanner.nextInt();
        
        RandomNum randomNum = new RandomNum(rows, cols);
        randomNum.fillGrid();
        randomNum.printGrid();
        
        scanner.close();
    }
}
