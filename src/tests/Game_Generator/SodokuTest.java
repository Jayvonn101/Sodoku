package tests.Game_Generator;

import Game_Generator.Sodoku;

// JUnit 5 annotations that mark setup methods and test methods
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

// Java reflection — lets us read private fields from Sodoku that we can't access directly
import java.lang.reflect.Method;
import java.lang.reflect.Field;

// Static import so we can write assertEquals(...) instead of Assertions.assertEquals(...)
import static org.junit.jupiter.api.Assertions.*;

// @DisplayName gives this test class a readable label in test reports
@DisplayName("Sodoku Tests")
public class SodokuTest {

    // The Sodoku object we'll test — shared across all tests in this class
    private Sodoku sodoku;

    // @BeforeEach runs this method before EVERY single test
    // This gives each test a fresh, empty 9x9 grid so tests don't affect each other
    @BeforeEach
    void setUp() {
        sodoku = new Sodoku(9, 9);
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid 9x9 dimensions initializes a zeroed grid")
    void constructor_validDimensions_zeroedGrid() throws Exception {
        // 'grid' is a private field in Sodoku, so we use reflection to access it
        // getDeclaredField gets a private field by name
        Field gridField = Sodoku.class.getDeclaredField("grid");
        // setAccessible(true) overrides the private modifier so we can read it
        gridField.setAccessible(true);
        // .get(sodoku) retrieves the value of that field from our sodoku instance
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Check the grid has 9 rows
        assertEquals(9, grid.length);
        for (int[] row : grid) {
            // Check each row has 9 columns
            assertEquals(9, row.length);
            for (int val : row) {
                // Every cell should start as 0 (empty)
                assertEquals(0, val);
            }
        }
    }

    @Test
    @DisplayName("Non-square dimensions throw an exception")
    void constructor_nonSquareDimensions_throwsException() {
        // assertThrows checks that creating a Sodoku with unequal rows/cols
        // throws an IllegalArgumentException — if it doesn't, the test fails
        assertThrows(IllegalArgumentException.class, () -> new Sodoku(9, 4));
    }

    @Test
    @DisplayName("Zero or negative dimensions throw an exception")
    void constructor_zeroDimensions_throwsException() {
        // Both 0 and negative values should be rejected by the constructor
        assertThrows(IllegalArgumentException.class, () -> new Sodoku(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Sodoku(-1, -1));
    }

    // -------------------------------------------------------------------------
    // isValid — tested indirectly via placeNum
    //
    // isValid() is a private method, so we can't call it directly.
    // Instead we test it through placeNum(), which calls isValid() internally.
    // If placeNum returns true, isValid returned true. If false, isValid returned false.
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isValid returns true for a valid placement")
    void isValid_validPlacement_returnsTrue() {
        // On a completely empty grid, placing any valid number (1-9) anywhere should work
        // placeNum returns true only if isValid also returned true
        assertTrue(sodoku.placeNum(0, 0, 5));
    }

    @Test
    @DisplayName("isValid returns false if number already exists in the same row")
    void isValid_duplicateInRow_returnsFalse() {
        // Place 5 in row 0, column 0
        sodoku.placeNum(0, 0, 5);
        // Trying to place 5 again in the same row (row 0, column 8) should fail
        // because 5 already exists in row 0
        assertFalse(sodoku.placeNum(0, 8, 5));
    }

    @Test
    @DisplayName("isValid returns false if number already exists in the same column")
    void isValid_duplicateInColumn_returnsFalse() {
        // Place 5 in row 0, column 0
        sodoku.placeNum(0, 0, 5);
        // Trying to place 5 in row 8, column 0 should fail
        // because 5 already exists in column 0
        assertFalse(sodoku.placeNum(8, 0, 5));
    }

    @Test
    @DisplayName("isValid returns false if number already exists in the same 3x3 box")
    void isValid_duplicateInBox_returnsFalse() {
        // Place 5 at [0][0] — this is inside the top-left 3x3 box (rows 0-2, cols 0-2)
        sodoku.placeNum(0, 0, 5);
        // [2][2] is also inside the same top-left box, so placing 5 there should fail
        assertFalse(sodoku.placeNum(2, 2, 5));
    }

    @Test
    @DisplayName("isValid works correctly for corner cells")
    void isValid_cornerCells_worksCorrectly() {
        // Test the top-left corner [0][0] and bottom-right corner [8][8]
        // Different numbers, different rows/cols/boxes — both should succeed
        assertTrue(sodoku.placeNum(0, 0, 1));
        assertTrue(sodoku.placeNum(8, 8, 2));
    }

    @Test
    @DisplayName("isValid works correctly for middle box")
    void isValid_middleBox_worksCorrectly() {
        // Place 7 in the center of the grid [4][4] — sits in the middle 3x3 box (rows 3-5, cols 3-5)
        assertTrue(sodoku.placeNum(4, 4, 7));
        // [3][5] is also in the middle box, so placing 7 there should fail
        assertFalse(sodoku.placeNum(3, 5, 7));
    }

    // -------------------------------------------------------------------------
    // fillNums
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All cells are filled after calling fillNums")
    void fillNums_allCellsFilled() throws Exception {
        sodoku.fillNums();

        // Access the private grid to inspect every cell
        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        for (int[] row : grid) {
            for (int val : row) {
                // 0 means empty — after fillNums() there should be no zeros
                assertNotEquals(0, val, "Expected no zeros after fillNums");
            }
        }
    }

    @Test
    @DisplayName("No row contains duplicate numbers after fillNums")
    void fillNums_noRowDuplicates() throws Exception {
        sodoku.fillNums();

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        for (int[] row : grid) {
            // boolean[10] — index 1-9 maps to each Sudoku digit
            // seen[n] = true means we've already seen the number n in this row
            boolean[] seen = new boolean[10];
            for (int val : row) {
                // If seen[val] is already true, this number is a duplicate
                assertFalse(seen[val], "Duplicate " + val + " found in row");
                seen[val] = true;
            }
        }
    }

    @Test
    @DisplayName("No column contains duplicate numbers after fillNums")
    void fillNums_noColumnDuplicates() throws Exception {
        sodoku.fillNums();

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Outer loop iterates over columns (not rows)
        for (int col = 0; col < 9; col++) {
            boolean[] seen = new boolean[10];
            // Inner loop walks down each row at this column index
            for (int row = 0; row < 9; row++) {
                int val = grid[row][col];
                assertFalse(seen[val], "Duplicate " + val + " found in column " + col);
                seen[val] = true;
            }
        }
    }

    @Test
    @DisplayName("No 3x3 box contains duplicate numbers after fillNums")
    void fillNums_noBoxDuplicates() throws Exception {
        sodoku.fillNums();

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Step by 3 to visit the top-left corner of each 3x3 box
        // boxRow = 0, 3, 6 — boxCol = 0, 3, 6 — that's all 9 boxes
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] seen = new boolean[10];
                // i and j walk through the 3x3 cells within this box
                for (int i = boxRow; i < boxRow + 3; i++) {
                    for (int j = boxCol; j < boxCol + 3; j++) {
                        int val = grid[i][j];
                        assertFalse(seen[val], "Duplicate " + val + " found in box at (" + boxRow + "," + boxCol + ")");
                        seen[val] = true;
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // placeNum
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid move is placed on the grid")
    void placeNum_validMove_placedOnGrid() throws Exception {
        // placeNum should return true when the move is valid
        assertTrue(sodoku.placeNum(0, 0, 1));

        // Then verify the value actually got written into the grid
        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        assertEquals(1, grid[0][0]);
    }

    @Test
    @DisplayName("Out-of-bounds row is rejected")
    void placeNum_outOfBoundsRow_rejected() {
        // Row -1 is below the valid range (0-8)
        assertFalse(sodoku.placeNum(-1, 0, 1));
        // Row 9 is above the valid range (0-8)
        assertFalse(sodoku.placeNum(9, 0, 1));
    }

    @Test
    @DisplayName("Out-of-bounds column is rejected")
    void placeNum_outOfBoundsColumn_rejected() {
        // Same idea as row bounds — col must be 0-8
        assertFalse(sodoku.placeNum(0, -1, 1));
        assertFalse(sodoku.placeNum(0, 9, 1));
    }

    @Test
    @DisplayName("Placing on an already-occupied cell is rejected")
    void placeNum_occupiedCell_rejected() {
        // Fill cell [0][0] with 1
        sodoku.placeNum(0, 0, 1);
        // Trying to place a different number (2) in the same cell should fail
        // because the cell is already occupied
        assertFalse(sodoku.placeNum(0, 0, 2));
    }

    @Test
    @DisplayName("Number below 1 is rejected")
    void placeNum_numberBelowOne_rejected() {
        // Valid Sudoku numbers are 1-9 — 0 and negative values should be rejected
        assertFalse(sodoku.placeNum(0, 0, 0));
        assertFalse(sodoku.placeNum(0, 0, -1));
    }

    @Test
    @DisplayName("Number above 9 is rejected")
    void placeNum_numberAboveNine_rejected() {
        // 10 is out of the 1-9 Sudoku range
        assertFalse(sodoku.placeNum(0, 0, 10));
    }

    // -------------------------------------------------------------------------
    // placeEmptyCells
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Exactly n cells become 0 after calling placeEmptyCells")
    void placeEmptyCells_exactlyNCellsEmpty() throws Exception {
        // Start with a fully filled grid
        sodoku.fillNums();
        // Remove exactly 20 cells
        sodoku.placeEmptyCells(20);

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Count how many zeros (empty cells) are in the grid
        int zeros = 0;
        for (int[] row : grid) {
            for (int val : row) {
                if (val == 0) zeros++;
            }
        }
        // There should be exactly 20 zeros
        assertEquals(20, zeros);
    }

    @Test
    @DisplayName("n = 0 leaves the grid unchanged")
    void placeEmptyCells_zeroLeavesGridUnchanged() throws Exception {
        sodoku.fillNums();
        // Removing 0 cells should change nothing
        sodoku.placeEmptyCells(0);

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Every cell should still be non-zero
        for (int[] row : grid) {
            for (int val : row) {
                assertNotEquals(0, val);
            }
        }
    }

    @Test
    @DisplayName("n = 81 empties the entire grid")
    void placeEmptyCells_81EmptiesEntireGrid() throws Exception {
        sodoku.fillNums();
        // 81 is the total number of cells in a 9x9 grid — removing all of them
        sodoku.placeEmptyCells(81);

        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        int[][] grid = (int[][]) gridField.get(sodoku);

        // Every cell should now be 0
        for (int[] row : grid) {
            for (int val : row) {
                assertEquals(0, val);
            }
        }
    }

    @Test
    @DisplayName("Negative n is rejected")
    void placeEmptyCells_negativeN_rejected() {
        // Can't remove a negative number of cells — should throw
        assertThrows(IllegalArgumentException.class, () -> sodoku.placeEmptyCells(-1));
    }

    @Test
    @DisplayName("n greater than total cells is rejected")
    void placeEmptyCells_nGreaterThanTotal_rejected() {
        // 82 exceeds the 81-cell maximum — should throw
        assertThrows(IllegalArgumentException.class, () -> sodoku.placeEmptyCells(82));
    }

    // -------------------------------------------------------------------------
    // isSolved
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isSolved returns false when any cell is empty")
    void isSolved_emptyCellPresent_returnsFalse() {
        // A brand new grid is all zeros, so it is definitely not solved
        assertFalse(sodoku.isSolved());
    }

    @Test
    @DisplayName("isSolved returns true when all cells are filled")
    void isSolved_allCellsFilled_returnsTrue() {
        // Fill the entire grid, then isSolved should return true
        sodoku.fillNums();
        assertTrue(sodoku.isSolved());
    }

    // -------------------------------------------------------------------------
    // solve
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Solves a partially filled valid grid")
    void solve_partialGrid_solves() {
        // Create a valid filled grid, then punch 40 holes in it
        sodoku.fillNums();
        sodoku.placeEmptyCells(40);
        // solve() should return true (success) and the grid should be complete
        assertTrue(sodoku.solve());
        assertTrue(sodoku.isSolved());
    }

    @Test
    @DisplayName("Solves a fully empty grid")
    void solve_emptyGrid_solves() {
        // Even from a completely blank grid, the backtracking solver should find a solution
        assertTrue(sodoku.solve());
        assertTrue(sodoku.isSolved());
    }

    @Test
    @DisplayName("An already-solved grid remains unchanged after solve")
    void solve_alreadySolved_remainsUnchanged() throws Exception {
        sodoku.fillNums();

        // Take a snapshot of the grid before calling solve
        Field gridField = Sodoku.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        // deepCopy creates an independent copy so we can compare before vs after
        int[][] before = deepCopy((int[][]) gridField.get(sodoku));

        assertTrue(sodoku.solve());

        // The grid after solve should be identical to the snapshot
        int[][] after = (int[][]) gridField.get(sodoku);
        assertArrayEquals(before, after);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    // Creates a completely independent copy of a 2D int array
    // Needed because Java arrays are objects — without this, 'before' and 'after'
    // would point to the same array and the comparison would always pass
    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
