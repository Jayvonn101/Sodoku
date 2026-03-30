package Game_Generator;

import java.util.Scanner;

public class Setup {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int row = getMaxRow(scanner);
        int col = getMaxCol(scanner);
        int empty = emptyCells(scanner, row, col);

        Sodoku grid = new Sodoku(row, col);
        grid.fillNums();
        grid.placeEmptyCells(empty);
        System.out.println("Puzzle:");
        grid.printGrid();

        // Game loop: let user fill in the board
        while (!grid.isSolved()) {
            System.out.println("\nEnter row (1-" + row + "), column (1-" + col + "), and number (1-" + row + "):");
            System.out.print("Row: ");
            int r = scanner.nextInt() - 1;
            System.out.print("Column: ");
            int c = scanner.nextInt() - 1;
            System.out.print("Number: ");
            int num = scanner.nextInt();

            if (grid.placeNum(r, c, num)) {
                System.out.println();
                grid.printGrid();
            }
        }

        System.out.println("\nCongratulations! You solved the puzzle!");
        scanner.close();
    }

    // Making the board
    private static int getMaxRow(Scanner scanner) {
        System.out.println("Enter the size of the row: ");
        return scanner.nextInt();
    }

    private static int getMaxCol(Scanner scanner) {
        System.out.println("Enter the size of the column: ");
        return scanner.nextInt();
    }

    private static int emptyCells(Scanner scanner, int row, int col) {
        System.out.println("Enter the number of empty cells: ");
        int cells = scanner.nextInt();
        if (cells < 0) {
            throw new IllegalArgumentException("Number of empty cells must be non-negative.");
        }
        if (cells > row * col) {
            throw new IllegalArgumentException("Number of empty cells must not exceed total cells in the grid.");
        }
        return cells;
    }
}
