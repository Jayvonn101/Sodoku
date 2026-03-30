# Sudoku Test Todo List

## Setup
- [ ] Add JUnit 5 to the project
- [ ] Create `tests/Game_Generator/` package structure

---

## Sodoku.java Tests

### Constructor
- [ ] Valid 9x9 dimensions initializes a zeroed grid
- [ ] Non-square dimensions throw an exception

### `isValid(r, c, num)`
- [ ] Returns `true` for a valid number placement
- [ ] Returns `false` if number already exists in the same row
- [ ] Returns `false` if number already exists in the same column
- [ ] Returns `false` if number already exists in the same 3x3 box
- [ ] Works correctly for corner cells (e.g., [0][0], [8][8])
- [ ] Works correctly for middle box (e.g., [4][4])

### `fillNums()`
- [ ] All cells are filled after calling (no zeros)
- [ ] No row contains duplicate numbers
- [ ] No column contains duplicate numbers
- [ ] No 3x3 box contains duplicate numbers

### `placeNum(r, c, num)`
- [ ] Valid move is placed on the grid
- [ ] Out-of-bounds row is rejected
- [ ] Out-of-bounds column is rejected
- [ ] Placing on an already-occupied cell is rejected
- [ ] Number below 1 is rejected
- [ ] Number above 9 is rejected
- [ ] Number violating row/col/box rules is rejected

### `placeEmptyCells(n)`
- [ ] Exactly `n` cells become 0 after calling
- [ ] `n = 0` leaves the grid unchanged
- [ ] `n = 81` empties the entire grid
- [ ] Negative `n` is rejected
- [ ] `n` greater than total cells is rejected

### `isSolved()`
- [ ] Returns `false` when any cell is empty (0)
- [ ] Returns `true` when all cells are filled

### `findEmptyCell()`
- [ ] Returns `null` on a fully filled grid
- [ ] Returns correct `[row, col]` for the first empty cell found

### `solve()`
- [ ] Solves a partially filled valid grid
- [ ] Solves a fully empty grid
- [ ] An already-solved grid remains unchanged
- [ ] Returns `false` for an unsolvable grid

---

## Setup.java Tests (Integration)
> Note: These require refactoring input methods or using piped Scanner input

- [ ] Valid row/col input is accepted
- [ ] Invalid (non-square) dimensions prompt re-entry
- [ ] Empty cell count within valid range is accepted
- [ ] Empty cell count exceeding total cells is rejected
- [ ] Game loop ends when `isSolved()` returns `true`
