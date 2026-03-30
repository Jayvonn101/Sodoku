# 🎮 Sudoku ✨

A feature-rich Java-based Sudoku game with a stunning neon aesthetic, animated backgrounds, and both GUI and console interfaces.

👤 **Original Code by Jayvonn101**
🎨 **GUI assembled by cjRem44x**

---

## 📁 Project Structure

```
Sodoku/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/
│   │           └── sudoku/
│   │               ├── Main.java              # 🖥️ Console entry point
│   │               ├── Sudoku.java            # 🧩 Sudoku logic and solver
│   │               ├── SudokuGUI.java         # 🎨 Swing GUI interface (heavily commented)
│   │               └── GameState.java         # 💾 Save/load functionality
│   └── test/
│       └── java/
│           └── org/
│               └── sudoku/
│                   └── SudokuTest.java        # 🧪 JUnit 5 test suite
├── scripts/
│   ├── build.bat       # 🔨 Windows build script (legacy)
│   ├── build.sh        # 🔨 Unix build script (legacy)
│   ├── run.bat         # ▶️ Windows console runner
│   ├── run.sh          # ▶️ Unix console runner
│   ├── run-gui.bat     # 🎮 Windows GUI runner
│   └── run-gui.sh      # 🎮 Unix GUI runner
├── lib/
│   └── junit-platform-console-standalone-1.11.4.jar  # 🧪 Legacy test runner
├── target/             # 📦 Maven build output (gitignored)
├── saves/              # 💾 Saved games directory (gitignored)
├── pom.xml             # 📋 Maven project descriptor
└── README.md           # 📖 This file
```

---

## ⚙️ Prerequisites

- **Java 21+** — [Download JDK](https://adoptium.net/)
- **Apache Maven 3.9+** — see installation instructions below

---

## 📦 Installing Maven

### 🐧 Linux

**Debian / Ubuntu:**
```bash
sudo apt update && sudo apt install maven
```

**Fedora / RHEL / CentOS:**
```bash
sudo dnf install maven
```

**Arch Linux:**
```bash
sudo pacman -S maven
```

### 🍎 macOS

Using [Homebrew](https://brew.sh/):
```bash
brew install maven
```

### 🪟 Windows

**Option 1 — winget (built into Windows 10/11):**
```powershell
winget install Apache.Maven
```

**Option 2 — Chocolatey:**
```powershell
choco install maven
```

**Option 3 — Manual install:**
1. Download the binary zip from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract to a folder (e.g. `C:\Program Files\Maven`)
3. Add `<install-dir>\bin` to your `PATH` environment variable

**Verify the installation:**
```bash
mvn --version
```

---

## 🔨 Building with Maven

From the project root:

```bash
# Compile source code only
mvn compile

# Compile + run all tests
mvn test

# Compile, test, and package into a JAR
mvn package

# Remove all build artifacts
mvn clean

# Clean, then compile and test from scratch
mvn clean test
```

### 📦 Building the JAR

To produce a runnable JAR, run:

```bash
mvn clean package
```

This will:
1. Delete any previous build output in `target/`
2. Compile all source files
3. Run the test suite
4. Package the compiled classes into `target/sudoku-1.0-SNAPSHOT.jar`

To skip tests and build faster:

```bash
mvn clean package -DskipTests
```

The JAR is a standard classpath JAR — it contains only the project classes. Run it by specifying the entry point explicitly (see the **Running** section below).

---

## 🧪 Testing with Maven

```bash
mvn test
```

Maven will discover and run all JUnit 5 tests in `src/test/java/` and report results in the terminal. Test reports are also written to `target/surefire-reports/`.

---

## 🎮 Running

### 🌟 GUI Version (Recommended)

After building with `mvn package`:

#### 🐧 Linux / 🍎 macOS
```bash
java -cp target/sudoku-1.0-SNAPSHOT.jar org.sudoku.SudokuGUI
# or use the provided script
./scripts/run-gui.sh
```

#### 🪟 Windows
```cmd
java -cp target\sudoku-1.0-SNAPSHOT.jar org.sudoku.SudokuGUI
:: or use the provided script
scripts\run-gui.bat
```

### 🖥️ Console Version

#### 🐧 Linux / 🍎 macOS
```bash
java -cp target/sudoku-1.0-SNAPSHOT.jar org.sudoku.Main
# or use the provided script
./scripts/run.sh
```

#### 🪟 Windows
```cmd
java -cp target\sudoku-1.0-SNAPSHOT.jar org.sudoku.Main
:: or use the provided script
scripts\run.bat
```

---

## ✨ GUI Features

### 🎨 Visual Design
- **💡 Neon Aesthetic**: Cyberpunk-inspired glowing UI elements with cyan, magenta, green, yellow, pink, blue, orange, and red neon colors
- **✨ Animated Background**: 300 floating monochrome dots with connecting lines and pulsing effects
- **🌟 Glow Effects**: Multi-layered glow borders on buttons, panels, and text
- **💓 Pulsing Animations**: Breathing glow effects on titles and UI elements
- **😊 Emoji Support**: Full Unicode emoji support using Arial Unicode MS font
- **📐 Square Window**: 1000x1000 pixel window with scaled UI components

### 🎲 Game Interface
- **🔢 Visual Grid**: 9x9 interactive grid with 3x3 box borders and glowing magenta frame
- **🟦 Cell Types**:
  - 🔒 Fixed cells: Dark gray with cyan text (puzzle numbers, cannot be changed)
  - ✏️ Editable cells: Semi-transparent with white text
  - 🎯 Selected cell: Magenta highlight with yellow border
  - ✅ Valid move: Green text
  - ❌ Invalid move: Red background with white text
- **🎛️ Control Panel**: Neon buttons for Save, Load, Hint, Solve, Clear, and New Game
- **🔢 Number Pad**: 1-9 buttons with blue neon glow for easy number input
- **📊 Status Bar**: Real-time feedback on moves, hints, and game state
- **⏱️ Timer**: Elapsed time display with green neon glow (persists across saves)

### 🎯 Game Flow
1. **🏠 Main Menu**: Three options - New Game, Load Game, Quit
2. **⚙️ Difficulty Selection**: Choose Easy (30 cells), Medium (45), Hard (55), or Custom
3. **🎮 Game Screen**: Play with full controls and animated background

### 🚀 Features
- **📊 Four Difficulty Levels**:
  - 🟢 Easy: 30 empty cells
  - 🟡 Medium: 45 empty cells
  - 🟠 Hard: 55 empty cells
  - 🔧 Custom: User-defined 1-80 empty cells
- **💾 Save/Load System**: Save unlimited games and resume later
- **💡 Hint System**: Shows next suggested move with cell highlighting
- **🤖 Auto-Solve**: Computer solves the puzzle with confirmation dialog
- **⌨️ Input Methods**:
  - 🖱️ Click cells and type numbers 1-9
  - 🔢 Use number pad buttons
  - ⏭️ Auto-advance to next cell after input
  - 🗑️ Backspace/Delete to clear cells
- **🏆 Win Detection**: Victory dialog with time and difficulty displayed
- **🔙 Navigation**: Back button to return to menu without losing progress

### ⚙️ Technical Highlights
- **🎴 CardLayout**: Single-window application with smooth screen transitions
- **📚 JLayeredPane**: Animated background layer behind UI components
- **🔘 Custom NeonButton Class**: Animated glow effects with hover states
- **🔍 Transparent Panels**: Semi-transparent backgrounds showing animation
- **📝 Comprehensive Comments**: Heavily documented code for reference
- **🛡️ Error Handling**: Alpha value clamping prevents color overflow errors

---

## 🖥️ Console Version - How to Play

1. 🚀 Run the application
2. ⚙️ Select difficulty or enter custom empty cell count
3. ⌨️ Use commands:
   - `p row col num` - ➕ Place number (e.g., `p 3 5 7`)
   - `r row col` - ➖ Remove number (e.g., `r 3 5`)
   - `h` - 💡 Get a hint
   - `s` - 🤖 Auto-solve puzzle
   - `save` - 💾 Save current game
   - `quit` - 🚪 Exit to menu

---

## ⭐ Core Features

- **🎭 Multiple Interfaces**: Both GUI and console versions
- **📊 Difficulty Levels**: Easy, Medium, Hard, or Custom
- **💾 Save/Load**: Persistent game state with elapsed time
- **💡 Hint System**: AI-powered move suggestions
- **🤖 Auto-Solve**: Backtracking algorithm solves any puzzle
- **✅ Validation**: Real-time conflict detection (rows, columns, 3x3 boxes)
- **⏱️ Timer**: Tracks solving time with persistence
- **🧮 Backtracking Algorithm**: Efficient puzzle generation and solving
- **🏗️ MVC Architecture**: Clean separation of logic and UI

---

## 🎮 GUI Controls

### 🖱️ Mouse
- **🎯 Click a cell** to select it (shows magenta highlight)
- **🔢 Click number pad buttons** to insert numbers
- **🎛️ Click control buttons** for actions

### ⌨️ Keyboard
- **1-9**: 🔢 Enter numbers in selected cell
- **⌫ Backspace/Delete**: 🗑️ Clear selected cell
- **↹ Tab**: ⏭️ Navigate between cells

### ⚡ Difficulty Shortcuts
From the difficulty screen, click:
- **🟢 Easy (30)**: 30 empty cells
- **🟡 Medium (45)**: 45 empty cells
- **🟠 Hard (55)**: 55 empty cells
- **🔧 Custom**: Enter any value 1-80

---

## ✨ Visual Effects

### 🌟 Glow System
- **3️⃣-Layer Glow Borders**: Main color, semi-transparent, and faint outer glow
- **💓 Pulsing Animation**: Smooth 50ms timer-based intensity oscillation
- **👆 Hover Effects**: Buttons invert colors with strong glow on mouseover
- **✨ Text Glow**: Titles and labels have multi-layer text shadows

### 🎬 Animation
- **⚪ Dot Animation**: 300 floating dots with 4 behavior types:
  - ➡️ Linear: Steady movement
  - 🔄 Circular: Orbiting patterns
  - 🌪️ Chaotic: Random direction changes
  - 💓 Pulse: Size pulsing in place
- **📎 Connecting Lines**: Dots within 100px connect with semi-transparent lines
- **🎞️ Frame Rate**: 25ms updates for smooth 40fps animation

### 🎨 Color Palette
- **🔵 Cyan**: Titles, primary borders, fixed cells
- **🟣 Magenta**: Selection highlights, secondary accents
- **🟢 Green**: Success states, timer, valid moves
- **🟡 Yellow**: Warnings, status messages
- **🩷 Pink**: Hints, accent elements
- **🔵 Blue**: Number pad, secondary buttons
- **🟠 Orange**: Hard difficulty, solve button
- **🔴 Red**: Errors, invalid moves, quit/cancel

---

## 👥 Authors

👤 **Original Sudoku Logic & Solver by Jayvonn101**
🎨 **Neon GUI, Animations & Enhancements by cjRem44x**

---

🎉 **Enjoy playing Sudoku with style!** 🎉
