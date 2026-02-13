package org.sudoku;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;

public class SudokuGUI extends JFrame {
    private Sudoku game;
    private JTextField[][] cells;
    private JLabel timerLabel;
    private JLabel statusLabel;
    private Timer swingTimer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private String difficulty;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private static final int GRID_SIZE = 9;
    private static final int CELL_SIZE = 50;
    private static final String SAVE_DIR = "saves/";
    
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color FIXED_CELL_COLOR = new Color(200, 200, 200);
    private static final Color SELECTED_CELL_COLOR = new Color(173, 216, 230);
    private static final Color CONFLICT_COLOR = new Color(255, 200, 200);
    private static final Color BOX_BORDER_COLOR = new Color(0, 0, 0);
    private static final Color CELL_BORDER_COLOR = new Color(150, 150, 150);
    
    public SudokuGUI() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        new File(SAVE_DIR).mkdirs();
        
        initComponents();
        setupMenuBar();
        
        pack();
        setLocationRelativeTo(null);
        
        showNewGameDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Top panel with timer and status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        timerLabel = new JLabel("Time: 00:00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(timerLabel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Select a cell and enter a number");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Grid panel
        JPanel gridPanel = createGridPanel();
        add(gridPanel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 1, 1));
        gridPanel.setBackground(BOX_BORDER_COLOR);
        gridPanel.setBorder(BorderFactory.createLineBorder(BOX_BORDER_COLOR, 3));
        
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = createCell(row, col);
                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }
        
        return gridPanel;
    }
    
    private JTextField createCell(int row, int col) {
        JTextField cell = new JTextField();
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial", Font.BOLD, 20));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        
        // Set borders to show 3x3 boxes
        int top = (row % 3 == 0) ? 2 : 1;
        int left = (col % 3 == 0) ? 2 : 1;
        int bottom = (row == GRID_SIZE - 1) ? 2 : 1;
        int right = (col == GRID_SIZE - 1) ? 2 : 1;
        
        cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, BOX_BORDER_COLOR));
        
        final int r = row;
        final int c = col;
        
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectedRow = r;
                selectedCol = c;
                updateCellSelection();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                validateAndUpdateCell(r, c);
            }
        });
        
        cell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch >= '1' && ch <= '9') {
                    cell.setText(String.valueOf(ch));
                    e.consume();
                    validateAndUpdateCell(r, c);
                    moveToNextCell(r, c);
                } else if (ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) {
                    cell.setText("");
                    e.consume();
                    clearCell(r, c);
                } else {
                    e.consume();
                }
            }
        });
        
        return cell;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton newGameBtn = createButton("New Game", e -> showNewGameDialog());
        JButton saveBtn = createButton("Save", e -> saveGame());
        JButton loadBtn = createButton("Load", e -> loadGame());
        JButton hintBtn = createButton("Hint", e -> showHint());
        JButton solveBtn = createButton("Solve", e -> solvePuzzle());
        JButton clearBtn = createButton("Clear", e -> clearSelectedCell());
        
        panel.add(newGameBtn);
        panel.add(saveBtn);
        panel.add(loadBtn);
        panel.add(hintBtn);
        panel.add(solveBtn);
        panel.add(clearBtn);
        
        // Number pad
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        numberPanel.setBackground(BACKGROUND_COLOR);
        numberPanel.setBorder(BorderFactory.createTitledBorder("Number Pad"));
        
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton numBtn = new JButton(String.valueOf(i));
            numBtn.setFont(new Font("Arial", Font.BOLD, 16));
            numBtn.setPreferredSize(new Dimension(40, 40));
            numBtn.addActionListener(e -> insertNumber(num));
            numberPanel.add(numBtn);
        }
        
        JPanel mainControlPanel = new JPanel(new BorderLayout());
        mainControlPanel.setBackground(BACKGROUND_COLOR);
        mainControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainControlPanel.add(panel, BorderLayout.CENTER);
        mainControlPanel.add(numberPanel, BorderLayout.SOUTH);
        
        return mainControlPanel;
    }
    
    private JButton createButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.addActionListener(listener);
        return btn;
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        
        JMenuItem newItem = new JMenuItem("New Game", KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> showNewGameDialog());
        
        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveGame());
        
        JMenuItem loadItem = new JMenuItem("Load", KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        loadItem.addActionListener(e -> loadGame());
        
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // Difficulty menu
        JMenu diffMenu = new JMenu("Difficulty");
        diffMenu.setMnemonic(KeyEvent.VK_D);
        
        ButtonGroup diffGroup = new ButtonGroup();
        
        JRadioButtonMenuItem easyItem = new JRadioButtonMenuItem("Easy");
        JRadioButtonMenuItem mediumItem = new JRadioButtonMenuItem("Medium", true);
        JRadioButtonMenuItem hardItem = new JRadioButtonMenuItem("Hard");
        JRadioButtonMenuItem customItem = new JRadioButtonMenuItem("Custom");
        
        diffGroup.add(easyItem);
        diffGroup.add(mediumItem);
        diffGroup.add(hardItem);
        diffGroup.add(customItem);
        
        easyItem.addActionListener(e -> startNewGame(30, "Easy"));
        mediumItem.addActionListener(e -> startNewGame(45, "Medium"));
        hardItem.addActionListener(e -> startNewGame(55, "Hard"));
        customItem.addActionListener(e -> showCustomDifficultyDialog());
        
        diffMenu.add(easyItem);
        diffMenu.add(mediumItem);
        diffMenu.add(hardItem);
        diffMenu.add(customItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(gameMenu);
        menuBar.add(diffMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showNewGameDialog() {
        String[] options = {"Easy (30)", "Medium (45)", "Hard (55)", "Custom", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select difficulty level:",
            "New Game",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]);
        
        switch (choice) {
            case 0: startNewGame(30, "Easy"); break;
            case 1: startNewGame(45, "Medium"); break;
            case 2: startNewGame(55, "Hard"); break;
            case 3: showCustomDifficultyDialog(); break;
        }
    }
    
    private void showCustomDifficultyDialog() {
        String input = JOptionPane.showInputDialog(this,
            "Enter number of empty cells (1-80):",
            "45");
        if (input != null) {
            try {
                int empty = Integer.parseInt(input.trim());
                if (empty >= 1 && empty <= 80) {
                    startNewGame(empty, "Custom");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a number between 1 and 80.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid number format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void startNewGame(int emptyCells, String diff) {
        game = new Sudoku(GRID_SIZE, GRID_SIZE);
        game.fillNums();
        game.placeEmptyCells(emptyCells);
        difficulty = diff;
        elapsedTime = 0;
        startTime = System.currentTimeMillis();
        isRunning = true;
        
        updateGridDisplay();
        startTimer();
        statusLabel.setText("Difficulty: " + difficulty + " | Select a cell to begin");
    }
    
    private void updateGridDisplay() {
        if (game == null) return;
        
        int[][] grid = game.getGrid();
        boolean[][] fixed = game.getFixedCells();
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = cells[row][col];
                int value = grid[row][col];
                
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                } else {
                    cell.setText("");
                }
                
                if (fixed[row][col] && value != 0) {
                    cell.setBackground(FIXED_CELL_COLOR);
                    cell.setEditable(false);
                    cell.setFont(new Font("Arial", Font.BOLD, 20));
                } else {
                    cell.setBackground(Color.WHITE);
                    cell.setEditable(true);
                    cell.setFont(new Font("Arial", Font.PLAIN, 20));
                }
            }
        }
        
        updateCellSelection();
    }
    
    private void updateCellSelection() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = cells[row][col];
                boolean isFixed = !cell.isEditable();
                
                if (row == selectedRow && col == selectedCol) {
                    cell.setBackground(SELECTED_CELL_COLOR);
                } else if (!isFixed) {
                    cell.setBackground(Color.WHITE);
                }
            }
        }
    }
    
    private void validateAndUpdateCell(int row, int col) {
        if (game == null) return;
        
        JTextField cell = cells[row][col];
        String text = cell.getText().trim();
        
        if (text.isEmpty()) {
            game.removeNum(row, col);
            cell.setBackground(Color.WHITE);
            return;
        }
        
        try {
            int num = Integer.parseInt(text);
            if (num >= 1 && num <= 9) {
                if (game.isValid(row, col, num)) {
                    game.placeNum(row, col, num);
                    cell.setBackground(Color.WHITE);
                    checkWin();
                } else {
                    cell.setBackground(CONFLICT_COLOR);
                    statusLabel.setText("Invalid move! " + num + " conflicts with row, column, or box.");
                }
            } else {
                cell.setText("");
                game.removeNum(row, col);
            }
        } catch (NumberFormatException e) {
            cell.setText("");
        }
    }
    
    private void clearCell(int row, int col) {
        if (game != null) {
            game.removeNum(row, col);
            cells[row][col].setBackground(Color.WHITE);
        }
    }
    
    private void moveToNextCell(int row, int col) {
        int nextCol = (col + 1) % GRID_SIZE;
        int nextRow = (col + 1) == GRID_SIZE ? row + 1 : row;
        
        if (nextRow < GRID_SIZE) {
            cells[nextRow][nextCol].requestFocus();
        }
    }
    
    private void insertNumber(int num) {
        if (selectedRow >= 0 && selectedCol >= 0 && game != null) {
            JTextField cell = cells[selectedRow][selectedCol];
            if (cell.isEditable()) {
                cell.setText(String.valueOf(num));
                validateAndUpdateCell(selectedRow, selectedCol);
            }
        }
    }
    
    private void clearSelectedCell() {
        if (selectedRow >= 0 && selectedCol >= 0 && game != null) {
            JTextField cell = cells[selectedRow][selectedCol];
            if (cell.isEditable()) {
                cell.setText("");
                clearCell(selectedRow, selectedCol);
            }
        }
    }
    
    private void startTimer() {
        if (swingTimer != null) {
            swingTimer.stop();
        }
        
        swingTimer = new Timer(1000, e -> {
            if (isRunning) {
                long current = System.currentTimeMillis();
                long total = elapsedTime + (current - startTime);
                timerLabel.setText("Time: " + formatTime(total));
            }
        });
        swingTimer.start();
    }
    
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private void saveGame() {
        if (game == null) {
            JOptionPane.showMessageDialog(this, "No game in progress!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String filename = JOptionPane.showInputDialog(this, "Enter save name:", "sudoku_save");
        if (filename != null && !filename.trim().isEmpty()) {
            if (!filename.endsWith(".sav")) {
                filename += ".sav";
            }
            
            try {
                long current = System.currentTimeMillis();
                long total = elapsedTime + (current - startTime);
                GameState state = new GameState(game, total / 1000, difficulty);
                state.save(SAVE_DIR + filename);
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadGame() {
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No saved games found.", "Load", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] names = new String[saveFiles.length];
        for (int i = 0; i < saveFiles.length; i++) {
            names[i] = saveFiles[i].getName().replace(".sav", "");
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select a saved game:",
            "Load Game",
            JOptionPane.QUESTION_MESSAGE,
            null,
            names,
            names[0]);
        
        if (selected != null) {
            try {
                GameState state = GameState.load(SAVE_DIR + selected + ".sav");
                game = state.getSudoku();
                difficulty = state.getDifficulty();
                elapsedTime = state.getElapsedTime() * 1000;
                startTime = System.currentTimeMillis();
                isRunning = true;
                
                updateGridDisplay();
                startTimer();
                statusLabel.setText("Loaded: " + selected + " | Difficulty: " + difficulty);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showHint() {
        if (game == null) return;
        
        Sudoku.Hint hint = game.getHint();
        if (hint != null) {
            cells[hint.row][hint.col].requestFocus();
            statusLabel.setText("Hint: Try placing " + hint.value + " at row " + (hint.row + 1) + ", column " + (hint.col + 1));
        } else {
            JOptionPane.showMessageDialog(this, "No hints available - puzzle is complete!", "Hint", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void solvePuzzle() {
        if (game == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to auto-solve the puzzle?",
            "Solve",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            game.solve();
            updateGridDisplay();
            isRunning = false;
            checkWin();
        }
    }
    
    private void checkWin() {
        if (game != null && game.isSolved()) {
            isRunning = false;
            long current = System.currentTimeMillis();
            long total = elapsedTime + (current - startTime);
            
            JOptionPane.showMessageDialog(this,
                "Congratulations! You solved the puzzle!\n" +
                "Time: " + formatTime(total) + "\n" +
                "Difficulty: " + difficulty,
                "You Win!",
                JOptionPane.INFORMATION_MESSAGE);
            
            statusLabel.setText("Puzzle solved! Start a new game.");
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Sudoku Game\n" +
            "Version 2.0\n\n" +
            "A Java-based Sudoku puzzle game with:\n" +
            "- Multiple difficulty levels\n" +
            "- Save/Load functionality\n" +
            "- Hint system\n" +
            "- Auto-solve feature\n\n" +
            "Enjoy playing!",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}
