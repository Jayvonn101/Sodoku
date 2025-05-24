package Game_Generator;
 
import java.util.Scanner;

public class Setup {

    public static void main(String[] args) {
        int row;
        int col;
        try (Scanner scanner = new Scanner(System.in)) {
            row = getMaxRow(scanner);
            col = getMaxCol(scanner);
            emptyCells(scanner, row, col);
            scanner.close();
        }
        Sodoku grid = new Sodoku(row, col);
        grid.fillNums();
        grid.printNums();
        grid.validateGrid(row, col);
        grid.placeEmptyCells(5); // Example: placing 5 empty cells
        System.out.println("Sodoku grid generated successfully with " + row + " rows and " + col + " columns.");
    }
    
    private static int getMaxRow(Scanner scanner) {
            System.out.println("Enter the size of the row: ");
            return scanner.nextInt();
    }

    private static int getMaxCol(Scanner scanner) {
            System.out.println("Enter the size of the column: ");
            return scanner.nextInt();
    }
    private static void emptyCells(Scanner scanner, int row, int col) {
        System.out.println("Enter the number of empty cells: ");
        int emptyCells = scanner.nextInt();
        if (emptyCells < 0){
            throw new IllegalArgumentException("Number of empty cells must be non-negative.");
            }
        if (emptyCells > row * col) {
            throw new IllegalArgumentException("Number of empty cells must not exceed total cells in the grid.");
            
        }
    }

}



    


    

