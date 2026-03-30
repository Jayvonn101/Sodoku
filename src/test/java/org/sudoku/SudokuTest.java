package org.sudoku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sudoku Tests")
class SudokuTest {

    private Sudoku sudoku;

    @BeforeEach
    void setUp() {
        sudoku = new Sudoku(9, 9);
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid 9x9 grid initializes with all zeros")
    void constructor_valid_zeroGrid() {
        int[][] grid = sudoku.getGrid();
        assertEquals(9, grid.length);
        for (int[] row : grid) {
            assertEquals(9, row.length);
            for (int val : row) assertEquals(0, val);
        }
    }

    @Test
    @DisplayName("Non-square dimensions throw IllegalArgumentException")
    void constructor_nonSquare_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Sudoku(9, 4));
    }

    @Test
    @DisplayName("Zero or negative dimensions throw IllegalArgumentException")
    void constructor_invalidDimensions_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Sudoku(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Sudoku(-3, -3));
    }

    // -------------------------------------------------------------------------
    // isValid
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Any valid number is accepted on an empty grid")
    void isValid_emptyGrid_returnsTrue() {
        assertTrue(sudoku.isValid(0, 0, 5));
        assertTrue(sudoku.isValid(4, 4, 9));
    }

    @Test
    @DisplayName("Number conflicting with its row is invalid")
    void isValid_rowConflict_returnsFalse() {
        sudoku.placeNum(0, 0, 7);
        assertFalse(sudoku.isValid(0, 8, 7));
    }

    @Test
    @DisplayName("Number conflicting with its column is invalid")
    void isValid_columnConflict_returnsFalse() {
        sudoku.placeNum(0, 0, 3);
        assertFalse(sudoku.isValid(8, 0, 3));
    }

    @Test
    @DisplayName("Number conflicting with its 3x3 box is invalid")
    void isValid_boxConflict_returnsFalse() {
        sudoku.placeNum(0, 0, 6);
        // [2][2] is in the same top-left 3x3 box
        assertFalse(sudoku.isValid(2, 2, 6));
    }

    // -------------------------------------------------------------------------
    // fillNums
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("fillNums produces a fully populated grid")
    void fillNums_allCellsNonZero() {
        sudoku.fillNums();
        for (int[] row : sudoku.getGrid())
            for (int val : row)
                assertNotEquals(0, val);
    }

    @Test
    @DisplayName("fillNums produces a valid Sudoku solution")
    void fillNums_validSolution() {
        sudoku.fillNums();
        int[][] grid = sudoku.getGrid();

        // Check rows
        for (int r = 0; r < 9; r++) {
            boolean[] seen = new boolean[10];
            for (int val : grid[r]) {
                assertFalse(seen[val], "Row " + r + " has duplicate " + val);
                seen[val] = true;
            }
        }

        // Check columns
        for (int c = 0; c < 9; c++) {
            boolean[] seen = new boolean[10];
            for (int r = 0; r < 9; r++) {
                int val = grid[r][c];
                assertFalse(seen[val], "Column " + c + " has duplicate " + val);
                seen[val] = true;
            }
        }

        // Check 3x3 boxes
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] seen = new boolean[10];
                for (int i = boxRow; i < boxRow + 3; i++)
                    for (int j = boxCol; j < boxCol + 3; j++) {
                        int val = grid[i][j];
                        assertFalse(seen[val], "Box (" + boxRow + "," + boxCol + ") has duplicate " + val);
                        seen[val] = true;
                    }
            }
        }
    }

    // -------------------------------------------------------------------------
    // placeNum
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid placement returns true and updates the grid")
    void placeNum_valid_placedOnGrid() {
        assertTrue(sudoku.placeNum(0, 0, 1));
        assertEquals(1, sudoku.getGrid()[0][0]);
    }

    @Test
    @DisplayName("Out-of-bounds coordinates are rejected")
    void placeNum_outOfBounds_returnsFalse() {
        assertFalse(sudoku.placeNum(-1, 0, 1));
        assertFalse(sudoku.placeNum(0, 9, 1));
        assertFalse(sudoku.placeNum(9, 9, 1));
    }

    @Test
    @DisplayName("Numbers outside 1-9 are rejected")
    void placeNum_invalidNumber_returnsFalse() {
        assertFalse(sudoku.placeNum(0, 0, 0));
        assertFalse(sudoku.placeNum(0, 0, 10));
        assertFalse(sudoku.placeNum(0, 0, -5));
    }

    @Test
    @DisplayName("Number conflicting with the row is rejected")
    void placeNum_conflictsWithRow_returnsFalse() {
        sudoku.placeNum(0, 0, 4);
        assertFalse(sudoku.placeNum(0, 5, 4));
    }

    // -------------------------------------------------------------------------
    // removeNum
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Removing a player-placed number succeeds")
    void removeNum_playerCell_returnsTrue() {
        sudoku.placeNum(3, 3, 5);
        assertTrue(sudoku.removeNum(3, 3));
        assertEquals(0, sudoku.getGrid()[3][3]);
    }

    @Test
    @DisplayName("Out-of-bounds remove is rejected")
    void removeNum_outOfBounds_returnsFalse() {
        assertFalse(sudoku.removeNum(-1, 0));
        assertFalse(sudoku.removeNum(0, 9));
    }

    // -------------------------------------------------------------------------
    // placeEmptyCells
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("placeEmptyCells creates exactly n empty cells")
    void placeEmptyCells_exactCount() {
        sudoku.fillNums();
        sudoku.placeEmptyCells(30);

        int zeros = 0;
        for (int[] row : sudoku.getGrid())
            for (int val : row)
                if (val == 0) zeros++;

        assertEquals(30, zeros);
    }

    @Test
    @DisplayName("Negative n throws IllegalArgumentException")
    void placeEmptyCells_negative_throws() {
        assertThrows(IllegalArgumentException.class, () -> sudoku.placeEmptyCells(-1));
    }

    @Test
    @DisplayName("n greater than 81 throws IllegalArgumentException")
    void placeEmptyCells_tooMany_throws() {
        assertThrows(IllegalArgumentException.class, () -> sudoku.placeEmptyCells(82));
    }

    // -------------------------------------------------------------------------
    // isSolved
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Empty grid is not solved")
    void isSolved_emptyGrid_returnsFalse() {
        assertFalse(sudoku.isSolved());
    }

    @Test
    @DisplayName("Fully filled grid is solved")
    void isSolved_filledGrid_returnsTrue() {
        sudoku.fillNums();
        assertTrue(sudoku.isSolved());
    }

    // -------------------------------------------------------------------------
    // solve
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("solve fills a fully empty grid")
    void solve_emptyGrid_solves() {
        assertTrue(sudoku.solve());
        assertTrue(sudoku.isSolved());
    }

    @Test
    @DisplayName("solve completes a partial puzzle")
    void solve_partialGrid_solves() {
        sudoku.fillNums();
        sudoku.placeEmptyCells(40);
        assertTrue(sudoku.solve());
        assertTrue(sudoku.isSolved());
    }

    // -------------------------------------------------------------------------
    // getHint
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getHint returns null on a solved grid")
    void getHint_solvedGrid_returnsNull() {
        sudoku.fillNums();
        assertNull(sudoku.getHint());
    }

    @Test
    @DisplayName("getHint returns a valid hint for a partial grid")
    void getHint_partialGrid_returnsValidHint() {
        sudoku.fillNums();
        sudoku.placeEmptyCells(1);

        Sudoku.Hint hint = sudoku.getHint();

        assertNotNull(hint);
        assertTrue(hint.row >= 0 && hint.row < 9);
        assertTrue(hint.col >= 0 && hint.col < 9);
        assertTrue(hint.value >= 1 && hint.value <= 9);
        assertEquals(0, sudoku.getGrid()[hint.row][hint.col], "Hint should point to an empty cell");
    }
}
