package Game_Generator;
 
import java.util.Scanner;

public class Setup {

    public static void main(String[] args) {
        int row;
        int col;
        try (Scanner scanner = new Scanner(System.in)) {
            row = getMaxRow(scanner);
            col = getMaxCol(scanner);
            scanner.close();
        }
        Sodoku grid = new Sodoku(row, col);
        grid.fillNums();
        grid.printNums();
        grid.validateGrid(row, col);
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
}


    


    

