# Sudoku

A Java-based Sudoku game generator and solver with both console and GUI interfaces.

## Project Structure

```
Sodoku/
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── sudoku/
│                   ├── Main.java       # Console entry point
│                   ├── Sudoku.java     # Sudoku logic and solver
│                   ├── SudokuGUI.java  # Swing GUI interface
│                   └── GameState.java  # Save/load functionality
├── scripts/
│   ├── build.bat       # Windows build script
│   ├── build.sh        # Unix build script
│   ├── run.bat         # Windows console runner
│   ├── run.sh          # Unix console runner
│   ├── run-gui.bat     # Windows GUI runner
│   └── run-gui.sh      # Unix GUI runner
├── target/
│   └── classes/        # Compiled class files
├── saves/              # Saved games directory
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

### GUI Version (Recommended)

#### Windows
```cmd
scripts\run-gui.bat
```

#### Unix/Linux/Mac
```bash
./scripts/run-gui.sh
```

### Console Version

#### Windows
```cmd
scripts\run.bat
```

#### Unix/Linux/Mac
```bash
./scripts/run.sh
```

## GUI Features

- **Visual Grid**: 9x9 interactive grid with 3x3 box borders
- **Menu Bar**: Game (New, Save, Load, Exit), Difficulty (Easy, Medium, Hard, Custom), Help
- **Control Panel**: Buttons for New Game, Save, Load, Hint, Solve, Clear
- **Number Pad**: Click numbers to insert into selected cell
- **Visual Feedback**:
  - Gray cells: Fixed puzzle numbers (cannot be changed)
  - White cells: Editable cells
  - Blue highlight: Currently selected cell
  - Red background: Invalid move (conflict detected)
- **Real-time Timer**: Shows elapsed time at top
- **Status Bar**: Displays hints and game messages
- **Keyboard Support**:
  - Type numbers 1-9 to fill cells
  - Backspace/Delete to clear cells
  - Tab to move between cells

## Console Version - How to Play

1. Run the application
2. Select difficulty or enter custom empty cell count
3. Use commands:
   - `p row col num` - Place number (e.g., `p 3 5 7`)
   - `r row col` - Remove number (e.g., `r 3 5`)
   - `h` - Get a hint
   - `s` - Auto-solve puzzle
   - `save` - Save current game
   - `quit` - Exit to menu

## Features

- **Multiple Interfaces**: Both GUI and console versions
- **Difficulty Levels**: Easy (30), Medium (45), Hard (55), or Custom
- **Save/Load**: Save games and resume later
- **Hint System**: Get suggestions for next moves
- **Auto-Solve**: Let the computer solve the puzzle
- **Validation**: Real-time conflict detection
- **Timer**: Track your solving time
- **Backtracking Algorithm**: Efficient puzzle generation and solving

## GUI Controls

- **Click a cell** to select it
- **Type 1-9** or **click number pad** to place numbers
- **Backspace/Delete** to clear a cell
- **Menu shortcuts**: Ctrl+N (New), Ctrl+S (Save), Ctrl+O (Load)

Enjoy playing Sudoku!
