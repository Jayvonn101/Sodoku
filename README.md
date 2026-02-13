# Sudoku

A Java-based Sudoku game generator and solver.

## Project Structure

```
Sodoku/
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── sudoku/
│                   ├── Main.java       # Entry point and game loop
│                   └── Sudoku.java     # Sudoku logic and solver
├── scripts/
│   ├── build.bat       # Windows build script
│   ├── build.sh        # Unix build script
│   ├── run.bat         # Windows run script
│   └── run.sh          # Unix run script
├── target/
│   └── classes/        # Compiled class files
└── README.md
```

## Building

### Windows
```cmd
scripts\build.bat
```

### Unix/Linux/Mac
```bash
./scripts/build.sh
```

## Running

### Windows
```cmd
scripts\run.bat
```

### Unix/Linux/Mac
```bash
./scripts/run.sh
```

## How to Play

1. Run the application
2. Enter grid size (typically 9 for standard Sudoku)
3. Enter number of empty cells (difficulty level)
4. Fill in the puzzle by entering:
   - Row number (1-9)
   - Column number (1-9)
   - Number to place (1-9)
5. Continue until the puzzle is solved!

## Features

- Generates random valid Sudoku puzzles
- Supports custom grid sizes
- Configurable difficulty (number of empty cells)
- Real-time validation of moves
- Backtracking solver algorithm
