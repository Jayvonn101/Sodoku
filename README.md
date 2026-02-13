# Sudoku

A feature-rich Java-based Sudoku game with a stunning neon aesthetic, animated backgrounds, and both GUI and console interfaces.

**GUI assembled by cjRem44x**

## Project Structure

```
Sodoku/
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── sudoku/
│                   ├── Main.java              # Console entry point
│                   ├── Sudoku.java            # Sudoku logic and solver
│                   ├── SudokuGUI.java         # Swing GUI interface (heavily commented)
│                   ├── GameState.java         # Save/load functionality
│                   └── DotAnimationPanel.java # Animated background
├── scripts/
│   ├── build.bat       # Windows build script
│   ├── build.sh        # Unix build script
│   ├── run.bat         # Windows console runner
│   ├── run.sh          # Unix console runner
│   ├── run-gui.bat     # Windows GUI runner
│   └── run-gui.sh      # Unix GUI runner
├── target/
│   └── classes/        # Compiled class files
├── saves/              # Saved games directory (gitignored)
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

### Visual Design
- **Neon Aesthetic**: Cyberpunk-inspired glowing UI elements with cyan, magenta, green, yellow, pink, blue, orange, and red neon colors
- **Animated Background**: 300 floating monochrome dots with connecting lines and pulsing effects
- **Glow Effects**: Multi-layered glow borders on buttons, panels, and text
- **Pulsing Animations**: Breathing glow effects on titles and UI elements
- **Emoji Support**: Full Unicode emoji support using Arial Unicode MS font
- **Square Window**: 1000x1000 pixel window with scaled UI components

### Game Interface
- **Visual Grid**: 9x9 interactive grid with 3x3 box borders and glowing magenta frame
- **Cell Types**:
  - Fixed cells: Dark gray with cyan text (puzzle numbers, cannot be changed)
  - Editable cells: Semi-transparent with white text
  - Selected cell: Magenta highlight with yellow border
  - Valid move: Green text
  - Invalid move: Red background with white text
- **Control Panel**: Neon buttons for Save, Load, Hint, Solve, Clear, and New Game
- **Number Pad**: 1-9 buttons with blue neon glow for easy number input
- **Status Bar**: Real-time feedback on moves, hints, and game state
- **Timer**: Elapsed time display with green neon glow (persists across saves)

### Game Flow
1. **Main Menu**: Three options - New Game, Load Game, Quit
2. **Difficulty Selection**: Choose Easy (30 cells), Medium (45), Hard (55), or Custom
3. **Game Screen**: Play with full controls and animated background

### Features
- **Four Difficulty Levels**:
  - Easy: 30 empty cells
  - Medium: 45 empty cells
  - Hard: 55 empty cells
  - Custom: User-defined 1-80 empty cells
- **Save/Load System**: Save unlimited games and resume later
- **Hint System**: Shows next suggested move with cell highlighting
- **Auto-Solve**: Computer solves the puzzle with confirmation dialog
- **Input Methods**:
  - Click cells and type numbers 1-9
  - Use number pad buttons
  - Auto-advance to next cell after input
  - Backspace/Delete to clear cells
- **Win Detection**: Victory dialog with time and difficulty displayed
- **Navigation**: Back button to return to menu without losing progress

### Technical Highlights
- **CardLayout**: Single-window application with smooth screen transitions
- **JLayeredPane**: Animated background layer behind UI components
- **Custom NeonButton Class**: Animated glow effects with hover states
- **Transparent Panels**: Semi-transparent backgrounds showing animation
- **Comprehensive Comments**: Heavily documented code for reference
- **Error Handling**: Alpha value clamping prevents color overflow errors

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

## Core Features

- **Multiple Interfaces**: Both GUI and console versions
- **Difficulty Levels**: Easy, Medium, Hard, or Custom
- **Save/Load**: Persistent game state with elapsed time
- **Hint System**: AI-powered move suggestions
- **Auto-Solve**: Backtracking algorithm solves any puzzle
- **Validation**: Real-time conflict detection (rows, columns, 3x3 boxes)
- **Timer**: Tracks solving time with persistence
- **Backtracking Algorithm**: Efficient puzzle generation and solving
- **MVC Architecture**: Clean separation of logic and UI

## GUI Controls

### Mouse
- **Click a cell** to select it (shows magenta highlight)
- **Click number pad buttons** to insert numbers
- **Click control buttons** for actions

### Keyboard
- **1-9**: Enter numbers in selected cell
- **Backspace/Delete**: Clear selected cell
- **Tab**: Navigate between cells

### Difficulty Shortcuts
From the difficulty screen, click:
- **Easy (30)**: 30 empty cells
- **Medium (45)**: 45 empty cells  
- **Hard (55)**: 55 empty cells
- **Custom**: Enter any value 1-80

## Visual Effects

### Glow System
- **3-Layer Glow Borders**: Main color, semi-transparent, and faint outer glow
- **Pulsing Animation**: Smooth 50ms timer-based intensity oscillation
- **Hover Effects**: Buttons invert colors with strong glow on mouseover
- **Text Glow**: Titles and labels have multi-layer text shadows

### Animation
- **Dot Animation**: 300 floating dots with 4 behavior types:
  - Linear: Steady movement
  - Circular: Orbiting patterns
  - Chaotic: Random direction changes
  - Pulse: Size pulsing in place
- **Connecting Lines**: Dots within 100px connect with semi-transparent lines
- **Frame Rate**: 25ms updates for smooth 40fps animation

### Color Palette
- **Cyan**: Titles, primary borders, fixed cells
- **Magenta**: Selection highlights, secondary accents
- **Green**: Success states, timer, valid moves
- **Yellow**: Warnings, status messages
- **Pink**: Hints, accent elements
- **Blue**: Number pad, secondary buttons
- **Orange**: Hard difficulty, solve button
- **Red**: Errors, invalid moves, quit/cancel

## Author

**GUI assembled by cjRem44x**

Enjoy playing Sudoku with style!
