package org.sudoku;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class SudokuGUI extends JFrame {
    private Sudoku game;
    private JTextField[][] cells;
    private JLabel timerLabel;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private Timer swingTimer;
    private Timer glowTimer;
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private String difficulty;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private static final int GRID_SIZE = 9;
    private static final int CELL_SIZE = 55;
    private static final String SAVE_DIR = "saves/";
    
    // Enhanced neon color scheme
    private static final Color BLACK_BG = new Color(0, 0, 5);
    private static final Color DARK_BG = new Color(10, 10, 15);
    private static final Color PANEL_BG = new Color(5, 5, 10);
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_CYAN_DIM = new Color(0, 150, 150);
    private static final Color NEON_MAGENTA = new Color(255, 0, 255);
    private static final Color NEON_MAGENTA_DIM = new Color(150, 0, 150);
    private static final Color NEON_GREEN = new Color(57, 255, 20);
    private static final Color NEON_GREEN_DIM = new Color(30, 150, 10);
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_YELLOW_DIM = new Color(150, 150, 0);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_PINK_DIM = new Color(150, 10, 80);
    private static final Color NEON_BLUE = new Color(0, 191, 255);
    private static final Color NEON_BLUE_DIM = new Color(0, 100, 150);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color NEON_ORANGE_DIM = new Color(150, 100, 0);
    private static final Color NEON_RED = new Color(255, 50, 50);
    private static final Color NEON_PURPLE = new Color(148, 0, 211);
    private static final Color NEON_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(200, 200, 200);
    
    // Glow intensity for animation
    private float glowIntensity = 0.0f;
    private boolean glowIncreasing = true;
    
    // Panels for card layout
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private static final String MENU_PANEL = "MENU";
    private static final String DIFFICULTY_PANEL = "DIFFICULTY";
    private static final String GAME_PANEL = "GAME";
    
    // Background animation
    private DotAnimationPanel dotAnimation;
    
    public SudokuGUI() {
        setTitle("SUDOKU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        new File(SAVE_DIR).mkdirs();
        
        getContentPane().setBackground(BLACK_BG);
        
        initComponents();
        startGlowAnimation();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Create layered pane for background animation
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(700, 700));
        
        // Create dot animation background
        dotAnimation = new DotAnimationPanel();
        dotAnimation.setBounds(0, 0, 700, 700);
        layeredPane.add(dotAnimation, JLayeredPane.DEFAULT_LAYER);
        
        // Create main content panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBounds(0, 0, 700, 700);
        
        // Create all panels
        JPanel menuPanel = createMainMenuPanel();
        JPanel difficultyPanel = createDifficultyPanel();
        JPanel gamePanel = createGamePanel();
        
        mainPanel.add(menuPanel, MENU_PANEL);
        mainPanel.add(difficultyPanel, DIFFICULTY_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        
        layeredPane.add(mainPanel, JLayeredPane.PALETTE_LAYER);
        
        add(layeredPane);
        
        cardLayout.show(mainPanel, MENU_PANEL);
    }
    
    private void startGlowAnimation() {
        glowTimer = new Timer(50, e -> {
            if (glowIncreasing) {
                glowIntensity += 0.05f;
                if (glowIntensity >= 1.0f) {
                    glowIntensity = 1.0f;
                    glowIncreasing = false;
                }
            } else {
                glowIntensity -= 0.05f;
                if (glowIntensity <= 0.3f) {
                    glowIntensity = 0.3f;
                    glowIncreasing = true;
                }
            }
            repaint();
        });
        glowTimer.start();
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw glowing background grid
                g2d.setColor(new Color(0, 40, 40, 30));
                for (int i = 0; i < getWidth(); i += 40) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 40) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Animated glowing title
        JLabel title = new JLabel("SUDOKU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw glow effect
                int glowSize = (int) (10 * glowIntensity);
                for (int i = glowSize; i > 0; i -= 2) {
                    float alpha = 0.1f * (1 - (float) i / glowSize);
                    g2d.setColor(new Color(0, 255, 255, (int) (alpha * 255)));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                // Draw main text
                g2d.setColor(NEON_CYAN);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 52));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(500, 80));
        
        // Subtitle with glow
        JLabel subtitle = new JLabel("🧩 Train Your Brain! 🎯") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw glow
                for (int i = 8; i > 0; i -= 2) {
                    g2d.setColor(new Color(255, 0, 255, 30));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                g2d.setColor(NEON_MAGENTA);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        subtitle.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
        subtitle.setForeground(NEON_MAGENTA);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Menu buttons with enhanced glow
        NeonButton newGameBtn = new NeonButton("🎮 NEW GAME", NEON_GREEN, 24, new Dimension(400, 80));
        newGameBtn.addActionListener(e -> showDifficultyPanel());
        
        NeonButton loadGameBtn = new NeonButton("📂 LOAD GAME", NEON_YELLOW, 24, new Dimension(400, 80));
        loadGameBtn.addActionListener(e -> showLoadGameDialog());
        
        NeonButton quitBtn = new NeonButton("❌ QUIT", NEON_RED, 24, new Dimension(400, 80));
        quitBtn.addActionListener(e -> System.exit(0));
        
        panel.add(title, gbc);
        gbc.insets = new Insets(10, 0, 50, 0);
        panel.add(subtitle, gbc);
        gbc.insets = new Insets(15, 0, 15, 0);
        panel.add(newGameBtn, gbc);
        panel.add(loadGameBtn, gbc);
        panel.add(quitBtn, gbc);
        
        return panel;
    }
    
    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw glowing background pattern
                g2d.setColor(new Color(255, 0, 255, 20));
                for (int i = 0; i < getWidth(); i += 50) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
            }
        };
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);
        
        // Glowing title
        JLabel title = new JLabel("SELECT DIFFICULTY") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                for (int i = 10; i > 0; i -= 2) {
                    g2d.setColor(new Color(0, 255, 255, 40));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                g2d.setColor(NEON_CYAN);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 36));
        title.setPreferredSize(new Dimension(500, 60));
        
        // Difficulty buttons with glow
        NeonButton easyBtn = new NeonButton("⭐ EASY (30 CELLS)", NEON_GREEN, 20, new Dimension(400, 70));
        easyBtn.addActionListener(e -> startNewGame(30, "EASY"));
        
        NeonButton mediumBtn = new NeonButton("⭐⭐ MEDIUM (45 CELLS)", NEON_YELLOW, 20, new Dimension(400, 70));
        mediumBtn.addActionListener(e -> startNewGame(45, "MEDIUM"));
        
        NeonButton hardBtn = new NeonButton("⭐⭐⭐ HARD (55 CELLS)", NEON_ORANGE, 20, new Dimension(400, 70));
        hardBtn.addActionListener(e -> startNewGame(55, "HARD"));
        
        NeonButton customBtn = new NeonButton("⚙ CUSTOM", NEON_PINK, 20, new Dimension(400, 70));
        customBtn.addActionListener(e -> showCustomDifficultyDialog());
        
        NeonButton backBtn = new NeonButton("← BACK TO MENU", NEON_BLUE, 18, new Dimension(300, 60));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU_PANEL));
        
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
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with glowing border
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(0, 0, 0, 100));
        topPanel.setBorder(createGlowBorder(NEON_CYAN, 3));
        
        // Back button
        NeonButton backBtn = new NeonButton("← MENU", NEON_BLUE, 12, new Dimension(100, 35));
        backBtn.addActionListener(e -> {
            isRunning = false;
            cardLayout.show(mainPanel, MENU_PANEL);
        });
        
        // Animated title
        titleLabel = new JLabel("SUDOKU") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Pulsing glow
                int glowSize = (int) (8 * glowIntensity);
                for (int i = glowSize; i > 0; i--) {
                    float alpha = 0.15f * (1 - (float) i / glowSize);
                    g2d.setColor(new Color(0, 255, 255, (int) (alpha * 255)));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                g2d.setColor(NEON_CYAN);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 22));
        titleLabel.setPreferredSize(new Dimension(300, 40));
        
        // Timer with glow
        timerLabel = new JLabel("⏱ 00:00:00") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Green glow
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(57, 255, 20, 50));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = getWidth() - fm.stringWidth(getText()) - 10;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                g2d.setColor(NEON_GREEN);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = getWidth() - fm.stringWidth(getText()) - 10;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        timerLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        timerLabel.setPreferredSize(new Dimension(150, 40));
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // Status label with border
        statusLabel = new JLabel("SELECT A CELL TO BEGIN");
        statusLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 14));
        statusLabel.setForeground(NEON_YELLOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(createGlowBorder(NEON_YELLOW, 2));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(0, 0, 0, 100));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Combine top
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Grid panel with glow border
        JPanel gridPanel = createGridPanel();
        gridPanel.setBorder(createGlowBorder(NEON_MAGENTA, 4));
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private Border createGlowBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, thickness),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), 2),
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50), 1)
            )
        );
    }
    
    private JPanel createGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2));
        gridPanel.setBackground(new Color(255, 0, 255, 80));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
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
        JTextField cell = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw glow if selected
                if (getBackground().equals(NEON_MAGENTA)) {
                    for (int i = 8; i > 0; i--) {
                        g2d.setColor(new Color(255, 0, 255, 30));
                        g2d.drawRoundRect(i/2, i/2, getWidth()-i, getHeight()-i, 5, 5);
                    }
                }
                
                // Draw text with glow
                String text = getText();
                if (!text.isEmpty()) {
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    
                    // Text glow
                    if (getForeground().equals(NEON_GREEN) || getForeground().equals(NEON_CYAN)) {
                        for (int i = 4; i > 0; i--) {
                            g2d.setColor(new Color(getForeground().getRed(), getForeground().getGreen(), getForeground().getBlue(), 80));
                            g2d.drawString(text, x, y);
                        }
                    }
                    
                    g2d.setColor(getForeground());
                    g2d.drawString(text, x, y);
                }
                
                g2d.dispose();
            }
        };
        
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBackground(new Color(10, 10, 15, 200));
        cell.setForeground(TEXT_COLOR);
        cell.setCaretColor(NEON_CYAN);
        cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE_DIM, 1));
        cell.setSelectionColor(NEON_MAGENTA);
        cell.setSelectedTextColor(BLACK_BG);
        cell.setOpaque(false);
        
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
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Button panel with glow border
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        buttonPanel.setBackground(new Color(0, 0, 0, 100));
        buttonPanel.setBorder(createGlowBorder(NEON_BLUE, 2));
        
        NeonButton saveBtn = new NeonButton("💾 SAVE", NEON_GREEN, 12, new Dimension(100, 40));
        saveBtn.addActionListener(e -> saveGame());
        
        NeonButton loadBtn = new NeonButton("📂 LOAD", NEON_YELLOW, 12, new Dimension(100, 40));
        loadBtn.addActionListener(e -> showLoadGameDialog());
        
        NeonButton hintBtn = new NeonButton("💡 HINT", NEON_PINK, 12, new Dimension(100, 40));
        hintBtn.addActionListener(e -> showHint());
        
        NeonButton solveBtn = new NeonButton("🤖 SOLVE", NEON_ORANGE, 12, new Dimension(100, 40));
        solveBtn.addActionListener(e -> solvePuzzle());
        
        NeonButton clearBtn = new NeonButton("🗑 CLEAR", NEON_RED, 12, new Dimension(100, 40));
        clearBtn.addActionListener(e -> clearSelectedCell());
        
        NeonButton newBtn = new NeonButton("🎮 NEW", NEON_CYAN, 12, new Dimension(100, 40));
        newBtn.addActionListener(e -> cardLayout.show(mainPanel, DIFFICULTY_PANEL));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(hintBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(newBtn);
        
        // Number pad with glow
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        numberPanel.setBackground(new Color(0, 0, 0, 100));
        numberPanel.setBorder(createGlowBorder(NEON_PURPLE, 2));
        
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            NeonButton numBtn = new NeonButton(String.valueOf(i), NEON_BLUE, 16, new Dimension(45, 45));
            numBtn.addActionListener(e -> insertNumber(num));
            numberPanel.add(numBtn);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(numberPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private void showDifficultyPanel() {
        cardLayout.show(mainPanel, DIFFICULTY_PANEL);
    }
    
    private void showLoadGameDialog() {
        JDialog dialog = new JDialog(this, "LOAD GAME", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Glowing title
        JLabel title = new JLabel("SELECT SAVE FILE");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_CYAN, 2));
        panel.add(title, BorderLayout.NORTH);
        
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            JLabel noSaves = new JLabel("NO SAVED GAMES FOUND");
            noSaves.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
            noSaves.setForeground(NEON_RED);
            noSaves.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noSaves, BorderLayout.CENTER);
            
            NeonButton okBtn = new NeonButton("✗ OK", NEON_RED, 14, new Dimension(120, 45));
            okBtn.addActionListener(e -> dialog.dispose());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setBackground(BLACK_BG);
            btnPanel.add(okBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
        } else {
            String[] names = new String[saveFiles.length];
            for (int i = 0; i < saveFiles.length; i++) {
                names[i] = saveFiles[i].getName().replace(".sav", "");
            }
            
            JList<String> list = new JList<>(names);
            list.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
            list.setBackground(DARK_BG);
            list.setForeground(NEON_YELLOW);
            list.setSelectionBackground(NEON_MAGENTA);
            list.setSelectionForeground(BLACK_BG);
            list.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
            
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setBackground(BLACK_BG);
            scrollPane.setBorder(createGlowBorder(NEON_CYAN, 3));
            scrollPane.getViewport().setBackground(BLACK_BG);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setBackground(BLACK_BG);
            
            NeonButton loadBtn = new NeonButton("📂 LOAD", NEON_GREEN, 14, new Dimension(120, 45));
            loadBtn.addActionListener(e -> {
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
            
            NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 14, new Dimension(120, 45));
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            btnPanel.add(loadBtn);
            btnPanel.add(cancelBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
        }
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showCustomDifficultyDialog() {
        JDialog dialog = new JDialog(this, "CUSTOM DIFFICULTY", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("⚙ CUSTOM DIFFICULTY ⚙");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        title.setForeground(NEON_PINK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_PINK, 2));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("EMPTY CELLS (1-80):");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        label.setForeground(NEON_YELLOW);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("45", 10);
        field.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_CYAN);
        field.setCaretColor(NEON_CYAN);
        field.setBorder(createGlowBorder(NEON_CYAN, 2));
        field.setHorizontalAlignment(JTextField.CENTER);
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        NeonButton okBtn = new NeonButton("✓ OK", NEON_GREEN, 14, new Dimension(100, 45));
        okBtn.addActionListener(e -> {
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
        
        NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 14, new Dimension(100, 45));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 280);
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
                    cell.setBackground(new Color(20, 20, 30));
                    cell.setForeground(NEON_CYAN);
                    cell.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
                    cell.setEditable(false);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
                } else {
                    cell.setBackground(DARK_BG);
                    cell.setForeground(TEXT_COLOR);
                    cell.setFont(new Font("Arial Unicode MS", Font.PLAIN, 24));
                    cell.setEditable(true);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE_DIM, 1));
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
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("💾 SAVE GAME 💾");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 24));
        title.setForeground(NEON_GREEN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_GREEN, 2));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("SAVE NAME:");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        label.setForeground(NEON_CYAN);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("sudoku_save", 15);
        field.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_GREEN);
        field.setCaretColor(NEON_GREEN);
        field.setBorder(createGlowBorder(NEON_GREEN, 2));
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        NeonButton okBtn = new NeonButton("✓ SAVE", NEON_GREEN, 14, new Dimension(100, 45));
        okBtn.addActionListener(e -> {
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
        
        NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 14, new Dimension(100, 45));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog(this, "ERROR", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        label.setForeground(NEON_RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        NeonButton okBtn = new NeonButton("✗ OK", NEON_RED, 14, new Dimension(100, 45));
        okBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 200);
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
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        label.setForeground(NEON_CYAN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        NeonButton okBtn = new NeonButton("✓ OK", NEON_GREEN, 14, new Dimension(100, 45));
        okBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void solvePuzzle() {
        if (game == null) return;
        
        JDialog dialog = new JDialog(this, "SOLVE PUZZLE", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel("AUTO-SOLVE THE PUZZLE?");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
        label.setForeground(NEON_ORANGE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(BLACK_BG);
        
        NeonButton yesBtn = new NeonButton("✓ YES", NEON_GREEN, 14, new Dimension(100, 45));
        yesBtn.addActionListener(e -> {
            game.solve();
            updateGridDisplay();
            isRunning = false;
            dialog.dispose();
            checkWin();
        });
        
        NeonButton noBtn = new NeonButton("✗ NO", NEON_RED, 14, new Dimension(100, 45));
        noBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void checkWin() {
        if (game != null && game.isSolved()) {
            isRunning = false;
            long current = System.currentTimeMillis();
            long total = elapsedTime + (current - startTime);
            
            JDialog dialog = new JDialog(this, "VICTORY!", true);
            dialog.getContentPane().setBackground(BLACK_BG);
            
            JPanel panel = new JPanel(new BorderLayout(20, 20));
            panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            JTextArea text = new JTextArea(
                "🎉🎉🎉 CONGRATULATIONS! 🎉🎉🎉\n\n" +
                "YOU SOLVED THE PUZZLE!\n\n" +
                "TIME: " + formatTime(total) + "\n" +
                "DIFFICULTY: " + difficulty
            );
            text.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
            text.setBackground(BLACK_BG);
            text.setForeground(NEON_GREEN);
            text.setEditable(false);
            text.setBorder(createGlowBorder(NEON_GREEN, 3));
            
            panel.add(text, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setBackground(BLACK_BG);
            
            NeonButton newBtn = new NeonButton("🎮 NEW GAME", NEON_CYAN, 14, new Dimension(150, 45));
            newBtn.addActionListener(e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, DIFFICULTY_PANEL);
            });
            
            NeonButton menuBtn = new NeonButton("🏠 MENU", NEON_BLUE, 14, new Dimension(120, 45));
            menuBtn.addActionListener(e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, MENU_PANEL);
            });
            
            btnPanel.add(newBtn);
            btnPanel.add(menuBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.pack();
            dialog.setSize(500, 350);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            statusLabel.setText("⚡ PUZZLE SOLVED! ⚡");
            statusLabel.setForeground(NEON_GREEN);
        }
    }
    
    // Custom Neon Button class with glow effects
    private class NeonButton extends JButton {
        private Color neonColor;
        private boolean isHovered = false;
        
        public NeonButton(String text, Color neonColor, int fontSize, Dimension size) {
            super(text);
            this.neonColor = neonColor;
            
            setFont(new Font("Arial Unicode MS", Font.BOLD, fontSize));
            setBackground(BLACK_BG);
            setForeground(neonColor);
            setBorder(createGlowBorder(neonColor, 2));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setPreferredSize(size);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setBackground(neonColor);
                    setForeground(BLACK_BG);
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setBackground(BLACK_BG);
                    setForeground(neonColor);
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw glow effect
            if (isHovered) {
                // Strong glow when hovered
                for (int i = 15; i > 0; i -= 2) {
                    float alpha = 0.3f * (1 - (float) i / 15);
                    g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), (int) (alpha * 255)));
                    g2d.fillRoundRect(i/2, i/2, getWidth() - i, getHeight() - i, 10, 10);
                }
                g2d.setColor(neonColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                // Subtle glow when not hovered
                for (int i = 8; i > 0; i -= 2) {
                    float alpha = 0.15f * (1 - (float) i / 8);
                    g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), (int) (alpha * 255)));
                    g2d.drawRoundRect(i/2, i/2, getWidth() - i, getHeight() - i, 10, 10);
                }
                g2d.setColor(BLACK_BG);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
            }
            
            // Draw text with glow
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            
            if (!isHovered) {
                // Text glow effect
                for (int i = 3; i > 0; i--) {
                    g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 100));
                    g2d.drawString(text, x, y);
                }
            }
            
            g2d.setColor(getForeground());
            g2d.drawString(text, x, y);
            
            // Draw border
            if (!isHovered) {
                g2d.setColor(neonColor);
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
            }
            
            g2d.dispose();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default colors
            UIManager.put("Panel.background", new Color(0, 0, 5));
            UIManager.put("OptionPane.background", new Color(0, 0, 5));
            UIManager.put("TextField.background", new Color(10, 10, 15));
            UIManager.put("TextField.foreground", new Color(0, 255, 255));
            UIManager.put("Button.background", new Color(0, 0, 5));
            UIManager.put("Label.foreground", new Color(200, 200, 200));
            UIManager.put("List.background", new Color(10, 10, 15));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}
