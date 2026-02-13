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
    private JLabel titleLabel;
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
    private static final Color BLACK_BG = new Color(5, 5, 8);
    private static final Color DARK_BG = new Color(15, 15, 20);
    private static final Color PANEL_BG = new Color(10, 10, 15);
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_MAGENTA = new Color(255, 0, 255);
    private static final Color NEON_GREEN = new Color(57, 255, 20);
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_BLUE = new Color(0, 191, 255);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color NEON_RED = new Color(255, 50, 50);
    private static final Color NEON_PURPLE = new Color(148, 0, 211);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    
    // Panels for card layout
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private static final String MENU_PANEL = "MENU";
    private static final String DIFFICULTY_PANEL = "DIFFICULTY";
    private static final String GAME_PANEL = "GAME";
    
    public SudokuGUI() {
        setTitle("⚡ NEON SUDOKU ⚡");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        new File(SAVE_DIR).mkdirs();
        
        // Set look and feel to ensure black background everywhere
        setUndecorated(false);
        getContentPane().setBackground(BLACK_BG);
        
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BLACK_BG);
        
        // Create all panels
        JPanel menuPanel = createMainMenuPanel();
        JPanel difficultyPanel = createDifficultyPanel();
        JPanel gamePanel = createGamePanel();
        
        mainPanel.add(menuPanel, MENU_PANEL);
        mainPanel.add(difficultyPanel, DIFFICULTY_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        
        add(mainPanel);
        
        // Show menu first
        cardLayout.show(mainPanel, MENU_PANEL);
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Title
        JLabel title = new JLabel("⚡ NEON SUDOKU ⚡");
        title.setFont(new Font("Consolas", Font.BOLD, 48));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_CYAN, 3),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        // Subtitle
        JLabel subtitle = new JLabel("CYBERPUNK EDITION");
        subtitle.setFont(new Font("Consolas", Font.PLAIN, 20));
        subtitle.setForeground(NEON_MAGENTA);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Menu buttons
        JButton newGameBtn = createLargeNeonButton("🎮 NEW GAME", NEON_GREEN, e -> showDifficultyPanel());
        JButton loadGameBtn = createLargeNeonButton("📂 LOAD GAME", NEON_YELLOW, e -> showLoadGamePanel());
        JButton quitBtn = createLargeNeonButton("❌ QUIT", NEON_RED, e -> System.exit(0));
        
        panel.add(title, gbc);
        gbc.insets = new Insets(10, 0, 40, 0);
        panel.add(subtitle, gbc);
        gbc.insets = new Insets(15, 0, 15, 0);
        panel.add(newGameBtn, gbc);
        panel.add(loadGameBtn, gbc);
        panel.add(quitBtn, gbc);
        
        return panel;
    }
    
    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);
        
        // Title
        JLabel title = new JLabel("⚡ SELECT DIFFICULTY ⚡");
        title.setFont(new Font("Consolas", Font.BOLD, 36));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NEON_CYAN, 3),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        
        // Difficulty buttons
        JButton easyBtn = createLargeNeonButton("⭐ EASY (30 CELLS)", NEON_GREEN, e -> startNewGame(30, "EASY"));
        JButton mediumBtn = createLargeNeonButton("⭐⭐ MEDIUM (45 CELLS)", NEON_YELLOW, e -> startNewGame(45, "MEDIUM"));
        JButton hardBtn = createLargeNeonButton("⭐⭐⭐ HARD (55 CELLS)", NEON_ORANGE, e -> startNewGame(55, "HARD"));
        JButton customBtn = createLargeNeonButton("⚙ CUSTOM", NEON_PINK, e -> showCustomDifficultyDialog());
        JButton backBtn = createLargeNeonButton("← BACK TO MENU", NEON_BLUE, e -> cardLayout.show(mainPanel, MENU_PANEL));
        
        panel.add(title, gbc);
        gbc.insets = new Insets(20, 0, 20, 0);
        panel.add(easyBtn, gbc);
        panel.add(mediumBtn, gbc);
        panel.add(hardBtn, gbc);
        panel.add(customBtn, gbc);
        gbc.insets = new Insets(40, 0, 0, 0);
        panel.add(backBtn, gbc);
        
        return panel;
    }
    
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with title, timer, and back button
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BLACK_BG);
        
        titleLabel = new JLabel("⚡ NEON SUDOKU ⚡");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        titleLabel.setForeground(NEON_CYAN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        timerLabel = new JLabel("⏱ 00:00:00");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        timerLabel.setForeground(NEON_GREEN);
        
        JButton backBtn = createSmallNeonButton("← MENU", NEON_BLUE, e -> {
            isRunning = false;
            cardLayout.show(mainPanel, MENU_PANEL);
        });
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // Status label
        statusLabel = new JLabel("SELECT A CELL TO BEGIN");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        statusLabel.setForeground(NEON_YELLOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BLACK_BG);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Combine top
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(BLACK_BG);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Grid panel
        JPanel gridPanel = createGridPanel();
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
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
        cell.setSelectedTextColor(BLACK_BG);
        
        final int r = row;
        final int c = col;
        
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectedRow = r;
                selectedCol = c;
                cell.setBackground(NEON_MAGENTA);
                cell.setForeground(BLACK_BG);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        buttonPanel.setBackground(BLACK_BG);
        
        JButton saveBtn = createSmallNeonButton("💾 SAVE", NEON_GREEN, e -> saveGame());
        JButton loadBtn = createSmallNeonButton("📂 LOAD", NEON_YELLOW, e -> showLoadGamePanel());
        JButton hintBtn = createSmallNeonButton("💡 HINT", NEON_PINK, e -> showHint());
        JButton solveBtn = createSmallNeonButton("🤖 SOLVE", NEON_ORANGE, e -> solvePuzzle());
        JButton clearBtn = createSmallNeonButton("🗑 CLEAR", NEON_RED, e -> clearSelectedCell());
        JButton newBtn = createSmallNeonButton("🎮 NEW", NEON_CYAN, e -> cardLayout.show(mainPanel, DIFFICULTY_PANEL));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(hintBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(newBtn);
        
        // Number pad
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        numberPanel.setBackground(BLACK_BG);
        
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton numBtn = new JButton(String.valueOf(i));
            numBtn.setFont(new Font("Consolas", Font.BOLD, 18));
            numBtn.setBackground(DARK_BG);
            numBtn.setForeground(NEON_BLUE);
            numBtn.setBorder(BorderFactory.createLineBorder(NEON_BLUE, 2));
            numBtn.setFocusPainted(false);
            numBtn.setPreferredSize(new Dimension(45, 45));
            
            numBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    numBtn.setBackground(NEON_BLUE);
                    numBtn.setForeground(BLACK_BG);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    numBtn.setBackground(DARK_BG);
                    numBtn.setForeground(NEON_BLUE);
                }
            });
            
            numBtn.addActionListener(e -> insertNumber(num));
            numberPanel.add(numBtn);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(numberPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JButton createLargeNeonButton(String text, Color neonColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 24));
        btn.setBackground(BLACK_BG);
        btn.setForeground(neonColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(neonColor, 3),
            BorderFactory.createEmptyBorder(20, 50, 20, 50)
        ));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(400, 80));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(neonColor);
                btn.setForeground(BLACK_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 3),
                    BorderFactory.createEmptyBorder(20, 50, 20, 50)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BLACK_BG);
                btn.setForeground(neonColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(neonColor, 3),
                    BorderFactory.createEmptyBorder(20, 50, 20, 50)
                ));
            }
        });
        
        btn.addActionListener(listener);
        return btn;
    }
    
    private JButton createSmallNeonButton(String text, Color neonColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setBackground(DARK_BG);
        btn.setForeground(neonColor);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(neonColor, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
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
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(DARK_BG);
                btn.setForeground(neonColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(neonColor, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });
        
        btn.addActionListener(listener);
        return btn;
    }
    
    private void showDifficultyPanel() {
        cardLayout.show(mainPanel, DIFFICULTY_PANEL);
    }
    
    private void showLoadGamePanel() {
        // Create a custom dialog with black background
        JDialog dialog = new JDialog(this, "LOAD GAME", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel title = new JLabel("⚡ SELECT SAVE FILE ⚡");
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);
        
        // Get save files
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            JLabel noSaves = new JLabel("NO SAVED GAMES FOUND");
            noSaves.setFont(new Font("Consolas", Font.BOLD, 18));
            noSaves.setForeground(NEON_RED);
            noSaves.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noSaves, BorderLayout.CENTER);
        } else {
            String[] names = new String[saveFiles.length];
            for (int i = 0; i < saveFiles.length; i++) {
                names[i] = saveFiles[i].getName().replace(".sav", "");
            }
            
            JList<String> list = new JList<>(names);
            list.setFont(new Font("Consolas", Font.BOLD, 16));
            list.setBackground(DARK_BG);
            list.setForeground(NEON_YELLOW);
            list.setSelectionBackground(NEON_MAGENTA);
            list.setSelectionForeground(BLACK_BG);
            list.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
            
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setBackground(BLACK_BG);
            scrollPane.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
            scrollPane.getViewport().setBackground(BLACK_BG);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Buttons
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setBackground(BLACK_BG);
            
            JButton loadBtn = createSmallNeonButton("📂 LOAD", NEON_GREEN, e -> {
                String selected = list.getSelectedValue();
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
                        titleLabel.setText("⚡ " + difficulty + " ⚡");
                        statusLabel.setText("📂 LOADED: " + selected);
                        statusLabel.setForeground(NEON_YELLOW);
                        
                        dialog.dispose();
                        cardLayout.show(mainPanel, GAME_PANEL);
                    } catch (Exception ex) {
                        statusLabel.setText("✗ ERROR LOADING");
                        statusLabel.setForeground(NEON_RED);
                    }
                }
            });
            
            btnPanel.add(loadBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
        }
        
        // Cancel button
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelPanel.setBackground(BLACK_BG);
        JButton cancelBtn = createSmallNeonButton("❌ CANCEL", NEON_RED, e -> dialog.dispose());
        cancelPanel.add(cancelBtn);
        
        if (saveFiles == null || saveFiles.length == 0) {
            panel.add(cancelPanel, BorderLayout.SOUTH);
        } else {
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(BLACK_BG);
            bottomPanel.add(panel.getComponent(2), BorderLayout.NORTH);
            bottomPanel.add(cancelPanel, BorderLayout.SOUTH);
            panel.add(bottomPanel, BorderLayout.SOUTH);
        }
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showCustomDifficultyDialog() {
        JDialog dialog = new JDialog(this, "CUSTOM DIFFICULTY", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("⚙ CUSTOM DIFFICULTY ⚙");
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setForeground(NEON_PINK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("EMPTY CELLS (1-80):");
        label.setFont(new Font("Consolas", Font.BOLD, 16));
        label.setForeground(NEON_YELLOW);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("45", 10);
        field.setFont(new Font("Consolas", Font.BOLD, 24));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_CYAN);
        field.setCaretColor(NEON_CYAN);
        field.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
        field.setHorizontalAlignment(JTextField.CENTER);
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        JButton okBtn = createSmallNeonButton("✓ OK", NEON_GREEN, e -> {
            try {
                int empty = Integer.parseInt(field.getText().trim());
                if (empty >= 1 && empty <= 80) {
                    dialog.dispose();
                    startNewGame(empty, "CUSTOM");
                } else {
                    field.setBackground(NEON_RED);
                    field.setForeground(Color.WHITE);
                }
            } catch (NumberFormatException ex) {
                field.setBackground(NEON_RED);
                field.setForeground(Color.WHITE);
            }
        });
        
        JButton cancelBtn = createSmallNeonButton("✗ CANCEL", NEON_RED, e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
        titleLabel.setText("⚡ " + difficulty + " ⚡");
        statusLabel.setText("⚡ SELECT A CELL TO BEGIN ⚡");
        statusLabel.setForeground(NEON_GREEN);
        
        cardLayout.show(mainPanel, GAME_PANEL);
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
                    cell.setBackground(new Color(25, 25, 35));
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
                    statusLabel.setText("✗ INVALID MOVE!");
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
                timerLabel.setText("⏱ " + formatTime(total));
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
            showErrorDialog("NO GAME IN PROGRESS!");
            return;
        }
        
        JDialog dialog = new JDialog(this, "SAVE GAME", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("💾 SAVE GAME 💾");
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setForeground(NEON_GREEN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("SAVE NAME:");
        label.setFont(new Font("Consolas", Font.BOLD, 16));
        label.setForeground(NEON_CYAN);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("sudoku_save", 15);
        field.setFont(new Font("Consolas", Font.BOLD, 18));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_GREEN);
        field.setCaretColor(NEON_GREEN);
        field.setBorder(BorderFactory.createLineBorder(NEON_GREEN, 2));
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        JButton okBtn = createSmallNeonButton("✓ SAVE", NEON_GREEN, e -> {
            String filename = field.getText().trim();
            if (!filename.isEmpty()) {
                if (!filename.endsWith(".sav")) filename += ".sav";
                
                try {
                    long current = System.currentTimeMillis();
                    long total = elapsedTime + (current - startTime);
                    GameState state = new GameState(game, total / 1000, difficulty);
                    state.save(SAVE_DIR + filename);
                    statusLabel.setText("💾 SAVED: " + filename);
                    statusLabel.setForeground(NEON_GREEN);
                    dialog.dispose();
                } catch (Exception ex) {
                    showErrorDialog("ERROR SAVING: " + ex.getMessage());
                }
            }
        });
        
        JButton cancelBtn = createSmallNeonButton("✗ CANCEL", NEON_RED, e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog(this, "ERROR", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Consolas", Font.BOLD, 16));
        label.setForeground(NEON_RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JButton okBtn = createSmallNeonButton("✗ OK", NEON_RED, e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showHint() {
        if (game == null) return;
        
        Sudoku.Hint hint = game.getHint();
        if (hint != null) {
            cells[hint.row][hint.col].requestFocus();
            statusLabel.setText("💡 TRY " + hint.value + " AT [" + (hint.row + 1) + "," + (hint.col + 1) + "]");
            statusLabel.setForeground(NEON_PINK);
        } else {
            showInfoDialog("NO HINTS - PUZZLE COMPLETE!");
        }
    }
    
    private void showInfoDialog(String message) {
        JDialog dialog = new JDialog(this, "INFO", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Consolas", Font.BOLD, 16));
        label.setForeground(NEON_CYAN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JButton okBtn = createSmallNeonButton("✓ OK", NEON_GREEN, e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void solvePuzzle() {
        if (game == null) return;
        
        JDialog dialog = new JDialog(this, "SOLVE PUZZLE", true);
        dialog.setBackground(BLACK_BG);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel("AUTO-SOLVE THE PUZZLE?");
        label.setFont(new Font("Consolas", Font.BOLD, 18));
        label.setForeground(NEON_ORANGE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        JButton yesBtn = createSmallNeonButton("✓ YES", NEON_GREEN, e -> {
            game.solve();
            updateGridDisplay();
            isRunning = false;
            dialog.dispose();
            checkWin();
        });
        
        JButton noBtn = createSmallNeonButton("✗ NO", NEON_RED, e -> dialog.dispose());
        
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void checkWin() {
        if (game != null && game.isSolved()) {
            isRunning = false;
            long current = System.currentTimeMillis();
            long total = elapsedTime + (current - startTime);
            
            JDialog dialog = new JDialog(this, "⚡ VICTORY ⚡", true);
            dialog.setBackground(BLACK_BG);
            dialog.getContentPane().setBackground(BLACK_BG);
            
            JPanel panel = new JPanel(new BorderLayout(20, 20));
            panel.setBackground(BLACK_BG);
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            JTextArea text = new JTextArea(
                "⚡⚡⚡ CONGRATULATIONS! ⚡⚡⚡\n\n" +
                "YOU SOLVED THE PUZZLE!\n\n" +
                "TIME: " + formatTime(total) + "\n" +
                "DIFFICULTY: " + difficulty
            );
            text.setFont(new Font("Consolas", Font.BOLD, 18));
            text.setBackground(BLACK_BG);
            text.setForeground(NEON_GREEN);
            text.setEditable(false);
            text.setBorder(BorderFactory.createLineBorder(NEON_GREEN, 3));
            
            panel.add(text, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setBackground(BLACK_BG);
            
            JButton newBtn = createSmallNeonButton("🎮 NEW GAME", NEON_CYAN, e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, DIFFICULTY_PANEL);
            });
            
            JButton menuBtn = createSmallNeonButton("🏠 MENU", NEON_BLUE, e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, MENU_PANEL);
            });
            
            btnPanel.add(newBtn);
            btnPanel.add(menuBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            statusLabel.setText("⚡ PUZZLE SOLVED! ⚡");
            statusLabel.setForeground(NEON_GREEN);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default colors for all Swing components
            UIManager.put("Panel.background", new Color(5, 5, 8));
            UIManager.put("OptionPane.background", new Color(5, 5, 8));
            UIManager.put("OptionPane.messageForeground", new Color(0, 255, 255));
            UIManager.put("TextField.background", new Color(15, 15, 20));
            UIManager.put("TextField.foreground", new Color(0, 255, 255));
            UIManager.put("TextField.caretForeground", new Color(0, 255, 255));
            UIManager.put("Button.background", new Color(15, 15, 20));
            UIManager.put("Label.foreground", new Color(220, 220, 220));
            UIManager.put("List.background", new Color(15, 15, 20));
            UIManager.put("List.foreground", new Color(255, 255, 0));
            UIManager.put("ScrollPane.background", new Color(5, 5, 8));
            UIManager.put("Viewport.background", new Color(5, 5, 8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}
