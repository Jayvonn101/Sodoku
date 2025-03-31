package Game_Generator;
 
import java.util.Scanner;

public class Setup {

    private int [][] grid;
    
   // This is the set up for the grid
    public Setup(int row, int col){ 
        
        // Getting the size of the grid
        try (Scanner grid_input = new Scanner(System.in)) {
            System.out.println("Enter the size of the row: ");
            row = grid_input.nextInt();

            System.out.println("Enter the size of the column: ");
            col = grid_input.nextInt();
        } 
        
        // Making instince of the grid
        grid = new int[row][col];
    }
       
    //Print the grid
        public void print_grid(){
        for (int[] grid1 : grid) {
            for (int j = 0; j < grid1.length; j++) {
                System.out.print(grid1[j] + " ");
            }
            System.out.println();
        }
    }
        
    public static void main(String[] args) {
        Setup grid = new Setup(0, 0);
        grid.print_grid();
    }


}
    


    

