package org.sudoku;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

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
    private static final int CELL_SIZE = 55;
    private static final String SAVE_DIR = "saves/";
    
    // Neon color scheme
    private static final Color BLACK_BG = new Color(10, 10, 15);
    private static final Color DARK_BG = new Color(20, 20, 30);
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_MAGENTA = new Color(255, 0, 255);
    private static final Color NEON_GREEN = new Color(57, 255, 20);
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_BLUE = new Color(0, 191, 255);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color NEON_RED = new Color(255, 50, 50);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    
    public SudokuGUI() {
        setTitle("⚡ NEON SUDOKU ⚡");
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
        getContentPane().setBackground(BLACK_BG);
        
        // Top panel with timer and status
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Grid panel
        JPanel gridPanel = createGridPanel();
        add(gridPanel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title label
        JLabel titleLabel = new JLabel("⚡ NEON SUDOKU ⚡");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 28));
        titleLabel.setForeground(NEON_CYAN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_CYAN, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        // Timer
        timerLabel = new JLabel("⏱ TIME: 00:00:00");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        timerLabel.setForeground(NEON_GREEN);
        
        // Status
        statusLabel = new JLabel("SELECT DIFFICULTY TO BEGIN");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        statusLabel.setForeground(NEON_YELLOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(timerLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBackground(NEON_MAGENTA);
        gridPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_CYAN, 4),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        
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
        cell.setFont(new Font("Consolas", Font.BOLD, 24));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBackground(DARK_BG);
        cell.setForeground(TEXT_COLOR);
        cell.setCaretColor(NEON_CYAN);
        cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE, 1));
        cell.setSelectionColor(NEON_MAGENTA);
        cell.setSelectedTextColor(Color.BLACK);
        
        final int r = row;
        final int c = col;
        
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectedRow = r;
                selectedCol = c;
                cell.setBackground(NEON_MAGENTA);
                cell.setForeground(Color.BLACK);
                cell.setBorder(BorderFactory.createLineBorder(NEON_YELLOW, 3));
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BLACK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(BLACK_BG);
        
        JButton newGameBtn = createNeonButton("🎮 NEW GAME", NEON_CYAN, e -> showNewGameDialog());
        JButton saveBtn = createNeonButton("💾 SAVE", NEON_GREEN, e -> saveGame());
        JButton loadBtn = createNeonButton("📂 LOAD", NEON_YELLOW, e -> loadGame());
        JButton hintBtn = createNeonButton("💡 HINT", NEON_PINK, e -> showHint());
        JButton solveBtn = createNeonButton("🤖 SOLVE", NEON_ORANGE, e -> solvePuzzle());
        JButton clearBtn = createNeonButton("🗑 CLEAR", NEON_RED, e -> clearSelectedCell());
        
        buttonPanel.add(newGameBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(hintBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(clearBtn);
        
        // Number pad
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        numberPanel.setBackground(BLACK_BG);
        numberPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(NEON_CYAN, 2),
            " NUMBER PAD ",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Consolas", Font.BOLD, 14),
            NEON_CYAN
        ));
        
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton numBtn = createNeonButton(String.valueOf(i), NEON_BLUE, e -> insertNumber(num));
            numBtn.setPreferredSize(new Dimension(50, 50));
            numBtn.setFont(new Font("Consolas", Font.BOLD, 20));
            numberPanel.add(numBtn);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(numberPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JButton createNeonButton(String text, Color neonColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setBackground(DARK_BG);
        btn.setForeground(neonColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(neonColor, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(neonColor);
                btn.setForeground(BLACK_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 3),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(DARK_BG);
                btn.setForeground(neonColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(neonColor, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        btn.addActionListener(listener);
        return btn;
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BLACK_BG);
        menuBar.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
        
        // Game menu
        JMenu gameMenu = createNeonMenu("GAME", KeyEvent.VK_G);
        
        JMenuItem newItem = createNeonMenuItem("🎮 NEW GAME", KeyEvent.VK_N, NEON_CYAN);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> showNewGameDialog());
        
        JMenuItem saveItem = createNeonMenuItem("💾 SAVE", KeyEvent.VK_S, NEON_GREEN);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveGame());
        
        JMenuItem loadItem = createNeonMenuItem("📂 LOAD", KeyEvent.VK_L, NEON_YELLOW);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        loadItem.addActionListener(e -> loadGame());
        
        JMenuItem exitItem = createNeonMenuItem("❌ EXIT", KeyEvent.VK_X, NEON_RED);
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // Difficulty menu
        JMenu diffMenu = createNeonMenu("DIFFICULTY", KeyEvent.VK_D);
        
        JMenuItem easyItem = createNeonMenuItem("⭐ EASY", KeyEvent.VK_1, NEON_GREEN);
        JMenuItem mediumItem = createNeonMenuItem("⭐⭐ MEDIUM", KeyEvent.VK_2, NEON_YELLOW);
        JMenuItem hardItem = createNeonMenuItem("⭐⭐⭐ HARD", KeyEvent.VK_3, NEON_ORANGE);
        JMenuItem customItem = createNeonMenuItem("⚙ CUSTOM", KeyEvent.VK_4, NEON_PINK);
        
        easyItem.addActionListener(e -> startNewGame(30, "EASY"));
        mediumItem.addActionListener(e -> startNewGame(45, "MEDIUM"));
        hardItem.addActionListener(e -> startNewGame(55, "HARD"));
        customItem.addActionListener(e -> showCustomDifficultyDialog());
        
        diffMenu.add(easyItem);
        diffMenu.add(mediumItem);
        diffMenu.add(hardItem);
        diffMenu.add(customItem);
        
        // Actions menu
        JMenu actionMenu = createNeonMenu("ACTIONS", KeyEvent.VK_A);
        
        JMenuItem hintItem = createNeonMenuItem("💡 HINT", KeyEvent.VK_H, NEON_PINK);
        hintItem.addActionListener(e -> showHint());
        
        JMenuItem solveItem = createNeonMenuItem("🤖 SOLVE", KeyEvent.VK_O, NEON_ORANGE);
        solveItem.addActionListener(e -> solvePuzzle());
        
        JMenuItem clearItem = createNeonMenuItem("🗑 CLEAR CELL", KeyEvent.VK_C, NEON_RED);
        clearItem.addActionListener(e -> clearSelectedCell());
        
        actionMenu.add(hintItem);
        actionMenu.add(solveItem);
        actionMenu.addSeparator();
        actionMenu.add(clearItem);
        
        // Help menu
        JMenu helpMenu = createNeonMenu("HELP", KeyEvent.VK_H);
        
        JMenuItem aboutItem = createNeonMenuItem("ℹ ABOUT", KeyEvent.VK_A, NEON_CYAN);
        aboutItem.addActionListener(e -> showAboutDialog());
        
        JMenuItem instructionsItem = createNeonMenuItem("📖 INSTRUCTIONS", KeyEvent.VK_I, NEON_BLUE);
        instructionsItem.addActionListener(e -> showInstructionsDialog());
        
        helpMenu.add(instructionsItem);
        helpMenu.add(aboutItem);
        
        menuBar.add(gameMenu);
        menuBar.add(diffMenu);
        menuBar.add(actionMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenu createNeonMenu(String text, int mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        menu.setFont(new Font("Consolas", Font.BOLD, 14));
        menu.setForeground(NEON_CYAN);
        menu.setBackground(BLACK_BG);
        menu.setOpaque(true);
        menu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menu.setForeground(NEON_YELLOW);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                menu.setForeground(NEON_CYAN);
            }
        });
        
        return menu;
    }
    
    private JMenuItem createNeonMenuItem(String text, int mnemonic, Color color) {
        JMenuItem item = new JMenuItem(text, mnemonic);
        item.setFont(new Font("Consolas", Font.PLAIN, 12));
        item.setForeground(color);
        item.setBackground(DARK_BG);
        item.setOpaque(true);
        
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(color);
                item.setForeground(BLACK_BG);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(DARK_BG);
                item.setForeground(color);
            }
        });
        
        return item;
    }
    
    private void showNewGameDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("⚡ SELECT DIFFICULTY ⚡");
        title.setFont(new Font("Consolas", Font.BOLD, 20));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title);
        
        String[] options = {"⭐ EASY (30)", "⭐⭐ MEDIUM (45)", "⭐⭐⭐ HARD (55)", "⚙ CUSTOM", "❌ CANCEL"};
        int choice = JOptionPane.showOptionDialog(this, panel, "NEW GAME",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        
        switch (choice) {
            case 0: startNewGame(30, "EASY"); break;
            case 1: startNewGame(45, "MEDIUM"); break;
            case 2: startNewGame(55, "HARD"); break;
            case 3: showCustomDifficultyDialog(); break;
        }
    }
    
    private void showCustomDifficultyDialog() {
        JTextField inputField = new JTextField("45", 10);
        inputField.setBackground(DARK_BG);
        inputField.setForeground(NEON_CYAN);
        inputField.setFont(new Font("Consolas", Font.BOLD, 18));
        inputField.setCaretColor(NEON_CYAN);
        inputField.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("ENTER EMPTY CELLS (1-80):");
        label.setFont(new Font("Consolas", Font.BOLD, 14));
        label.setForeground(NEON_YELLOW);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        int result = JOptionPane.showConfirmDialog(this, panel, "CUSTOM DIFFICULTY",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int empty = Integer.parseInt(inputField.getText().trim());
                if (empty >= 1 && empty <= 80) {
                    startNewGame(empty, "CUSTOM");
                } else {
                    showError("PLEASE ENTER A NUMBER BETWEEN 1 AND 80");
                }
            } catch (NumberFormatException e) {
                showError("INVALID NUMBER FORMAT");
            }
        }
    }
    
    private void showInstructionsDialog() {
        JTextArea textArea = new JTextArea(
            "⚡ HOW TO PLAY ⚡\n\n" +
            "1. Fill the 9x9 grid so each row, column, and 3x3 box\n" +
            "   contains digits 1-9.\n\n" +
            "2. GRAY cells are fixed puzzle numbers.\n\n" +
            "3. Click a WHITE cell and type 1-9 to place numbers.\n\n" +
            "4. Use the NUMBER PAD or keyboard to input.\n\n" +
            "5. Backspace/Delete clears a cell.\n\n" +
            "6. RED background means invalid move!\n\n" +
            "7. Use MENU or BUTTONS for all actions.\n\n" +
            "8. Save anytime and resume later!\n\n" +
            "💡 PRO TIP: Use HINT if you're stuck!"
        );
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setBackground(DARK_BG);
        textArea.setForeground(TEXT_COLOR);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
        scrollPane.setPreferredSize(new Dimension(450, 350));
        
        JOptionPane.showMessageDialog(this, scrollPane, "INSTRUCTIONS", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE);
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
        statusLabel.setText("⚡ DIFFICULTY: " + difficulty + " | SELECT CELL TO BEGIN ⚡");
        statusLabel.setForeground(NEON_GREEN);
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
                    cell.setBackground(new Color(40, 40, 50));
                    cell.setForeground(NEON_CYAN);
                    cell.setFont(new Font("Consolas", Font.BOLD, 24));
                    cell.setEditable(false);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
                } else {
                    cell.setBackground(DARK_BG);
                    cell.setForeground(TEXT_COLOR);
                    cell.setFont(new Font("Consolas", Font.PLAIN, 24));
                    cell.setEditable(true);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE, 1));
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
            cell.setBackground(DARK_BG);
            return;
        }
        
        try {
            int num = Integer.parseInt(text);
            if (num >= 1 && num <= 9) {
                if (game.isValid(row, col, num)) {
                    game.placeNum(row, col, num);
                    cell.setBackground(DARK_BG);
                    cell.setForeground(NEON_GREEN);
                    statusLabel.setText("✓ VALID MOVE");
                    statusLabel.setForeground(NEON_GREEN);
                    checkWin();
                } else {
                    cell.setBackground(NEON_RED);
                    cell.setForeground(Color.WHITE);
                    statusLabel.setText("✗ INVALID MOVE! CONFLICT DETECTED");
                    statusLabel.setForeground(NEON_RED);
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
            cells[row][col].setBackground(DARK_BG);
            cells[row][col].setForeground(TEXT_COLOR);
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
                statusLabel.setText("✓ CELL CLEARED");
                statusLabel.setForeground(NEON_YELLOW);
            }
        }
    }
    
    private void startTimer() {
        if (swingTimer != null) swingTimer.stop();
        
        swingTimer = new Timer(1000, e -> {
            if (isRunning) {
                long current = System.currentTimeMillis();
                long total = elapsedTime + (current - startTime);
                timerLabel.setText("⏱ TIME: " + formatTime(total));
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
            showError("NO GAME IN PROGRESS!");
            return;
        }
        
        JTextField nameField = new JTextField("sudoku_save", 15);
        nameField.setBackground(DARK_BG);
        nameField.setForeground(NEON_GREEN);
        nameField.setFont(new Font("Consolas", Font.BOLD, 14));
        nameField.setCaretColor(NEON_GREEN);
        nameField.setBorder(BorderFactory.createLineBorder(NEON_GREEN, 2));
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("ENTER SAVE NAME:");
        label.setFont(new Font("Consolas", Font.BOLD, 14));
        label.setForeground(NEON_CYAN);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(nameField, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        int result = JOptionPane.showConfirmDialog(this, panel, "SAVE GAME",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String filename = nameField.getText().trim();
            if (!filename.isEmpty()) {
                if (!filename.endsWith(".sav")) filename += ".sav";
                
                try {
                    long current = System.currentTimeMillis();
                    long total = elapsedTime + (current - startTime);
                    GameState state = new GameState(game, total / 1000, difficulty);
                    state.save(SAVE_DIR + filename);
                    statusLabel.setText("💾 GAME SAVED: " + filename);
                    statusLabel.setForeground(NEON_GREEN);
                } catch (Exception e) {
                    showError("ERROR SAVING: " + e.getMessage());
                }
            }
        }
    }
    
    private void loadGame() {
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            showError("NO SAVED GAMES FOUND");
            return;
        }
        
        String[] names = new String[saveFiles.length];
        for (int i = 0; i < saveFiles.length; i++) {
            names[i] = saveFiles[i].getName().replace(".sav", "");
        }
        
        JComboBox<String> comboBox = new JComboBox<>(names);
        comboBox.setBackground(DARK_BG);
        comboBox.setForeground(NEON_YELLOW);
        comboBox.setFont(new Font("Consolas", Font.BOLD, 14));
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("SELECT SAVE FILE:");
        label.setFont(new Font("Consolas", Font.BOLD, 14));
        label.setForeground(NEON_CYAN);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        int result = JOptionPane.showConfirmDialog(this, panel, "LOAD GAME",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) comboBox.getSelectedItem();
            try {
                GameState state = GameState.load(SAVE_DIR + selected + ".sav");
                game = state.getSudoku();
                difficulty = state.getDifficulty();
                elapsedTime = state.getElapsedTime() * 1000;
                startTime = System.currentTimeMillis();
                isRunning = true;
                
                updateGridDisplay();
                startTimer();
                statusLabel.setText("📂 LOADED: " + selected + " | DIFFICULTY: " + difficulty);
                statusLabel.setForeground(NEON_YELLOW);
            } catch (Exception e) {
                showError("ERROR LOADING: " + e.getMessage());
            }
        }
    }
    
    private void showHint() {
        if (game == null) return;
        
        Sudoku.Hint hint = game.getHint();
        if (hint != null) {
            cells[hint.row][hint.col].requestFocus();
            statusLabel.setText("💡 HINT: PLACE " + hint.value + " AT ROW " + (hint.row + 1) + ", COL " + (hint.col + 1));
            statusLabel.setForeground(NEON_PINK);
        } else {
            JOptionPane.showMessageDialog(this, "NO HINTS AVAILABLE - PUZZLE COMPLETE!", "HINT", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void solvePuzzle() {
        if (game == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "ARE YOU SURE YOU WANT TO AUTO-SOLVE?",
            "SOLVE",
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
            
            JPanel winPanel = new JPanel(new BorderLayout(10, 10));
            winPanel.setBackground(BLACK_BG);
            
            JTextArea text = new JTextArea(
                "⚡⚡⚡ CONGRATULATIONS! ⚡⚡⚡\n\n" +
                "YOU SOLVED THE PUZZLE!\n\n" +
                "TIME: " + formatTime(total) + "\n" +
                "DIFFICULTY: " + difficulty + "\n\n" +
                "START A NEW GAME?"
            );
            text.setFont(new Font("Consolas", Font.BOLD, 16));
            text.setBackground(BLACK_BG);
            text.setForeground(NEON_GREEN);
            text.setEditable(false);
            text.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            winPanel.add(text, BorderLayout.CENTER);
            winPanel.setBorder(BorderFactory.createLineBorder(NEON_GREEN, 3));
            
            int choice = JOptionPane.showConfirmDialog(this, winPanel, "YOU WIN!",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                showNewGameDialog();
            } else {
                statusLabel.setText("⚡ PUZZLE SOLVED! START NEW GAME ⚡");
                statusLabel.setForeground(NEON_GREEN);
            }
        }
    }
    
    private void showAboutDialog() {
        JTextArea text = new JTextArea(
            "⚡ NEON SUDOKU v2.0 ⚡\n\n" +
            "A CYBERPUNK SUDOKU EXPERIENCE\n\n" +
            "FEATURES:\n" +
            "• NEON VISUAL THEME\n" +
            "• MULTIPLE DIFFICULTIES\n" +
            "• SAVE/LOAD GAMES\n" +
            "• HINT SYSTEM\n" +
            "• AUTO-SOLVE\n\n" +
            "ENJOY THE GAME!"
        );
        text.setFont(new Font("Consolas", Font.PLAIN, 14));
        text.setBackground(BLACK_BG);
        text.setForeground(NEON_CYAN);
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JOptionPane.showMessageDialog(this, text, "ABOUT", JOptionPane.PLAIN_MESSAGE);
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
