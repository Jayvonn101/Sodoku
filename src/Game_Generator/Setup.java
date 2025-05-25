package Game_Generator;
 
import java.util.Scanner;

public class Setup {

    public static void main(String[] args) {
        int row;
        int col;
        Scanner scanner = new Scanner(System.in);
    
        row = getMaxRow(scanner);
        col = getMaxCol(scanner);
        int empty = emptyCells(scanner, row, col);
        scanner.close();
        
        Sodoku grid = new Sodoku(row, col);
        grid.fillNums();
        grid.placeEmptyCells(empty); // Example: placing 5 empty cells
        grid.printNum();
        
      
   
    }

    public static void printGrid(Sodoku grid) {
        grid.printNum();
    }
    

    //  making the boar
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
        if (cells < 0){
            throw new IllegalArgumentException("Number of empty cells must be non-negative.");
            }
        if (cells > row * col) {
            throw new IllegalArgumentException("Number of empty cells must not exceed total cells in the grid.");
            
        }
        
    return cells;
    }


}



    


    

