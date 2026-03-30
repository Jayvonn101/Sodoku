package org.sudoku;

// =============================================================================
// SUDOKU GUI APPLICATION
// =============================================================================
// A visually stunning Sudoku game with neon aesthetics, animated backgrounds,
// and comprehensive game features including save/load, hints, and auto-solve.
//
// AUTHOR: GUI assembled by cjRem44x
// VERSION: 2.0
// =============================================================================

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * Main GUI class for the Sudoku application.
 * Handles all UI components, game logic integration, and visual effects.
 */
public class SudokuGUI extends JFrame {
    
    // =========================================================================
    // GAME STATE VARIABLES
    // =========================================================================
    
    /** The current Sudoku game instance containing the grid and logic */
    private Sudoku game;
    
    /** 2D array of text fields representing the 9x9 Sudoku grid cells */
    private JTextField[][] cells;
    
    /** Label displaying the elapsed game time */
    private JLabel timerLabel;
    
    /** Label displaying status messages and hints */
    private JLabel statusLabel;
    
    /** Label displaying the game title with glow effects */
    private JLabel titleLabel;
    
    /** Timer for updating the game clock every second */
    private Timer swingTimer;
    
    /** Timer for animating the pulsing glow effect on UI elements */
    private Timer glowTimer;
    
    /** Timestamp when the current game started (for calculating elapsed time) */
    private long startTime;
    
    /** Total elapsed time (persists across saves/loads) */
    private long elapsedTime;
    
    /** Flag indicating if a game is currently in progress */
    private boolean isRunning;
    
    /** Current difficulty level (EASY, MEDIUM, HARD, CUSTOM) */
    private String difficulty;
    
    /** Currently selected grid row (-1 if none selected) */
    private int selectedRow = -1;
    
    /** Currently selected grid column (-1 if none selected) */
    private int selectedCol = -1;
    
    // =========================================================================
    // CONSTANTS
    // =========================================================================
    
    /** Size of the Sudoku grid (9x9) */
    private static final int GRID_SIZE = 9;
    
    /** Size of each grid cell in pixels */
    private static final int CELL_SIZE = 70;
    
    /** Directory for save files */
    private static final String SAVE_DIR = "saves/";
    
    // Window dimensions - making it a square 1000x1000
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;
    
    // =========================================================================
    // COLOR PALETTE - Neon Theme
    // =========================================================================
    // All colors follow a neon aesthetic with cyan, magenta, green, yellow, 
    // pink, blue, orange, and red as the primary neon colors.
    // Each color has a "dim" variant for subtle effects.
    
    /** Pure black background color (RGB: 0, 0, 5) */
    private static final Color BLACK_BG = new Color(0, 0, 5);
    
    /** Dark background for panels (RGB: 10, 10, 15) */
    private static final Color DARK_BG = new Color(10, 10, 15);
    
    /** Panel background color (RGB: 5, 5, 10) */
    private static final Color PANEL_BG = new Color(5, 5, 10);
    
    // Neon Cyan - Used for titles, borders, and primary highlights
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_CYAN_DIM = new Color(0, 150, 150);
    
    // Neon Magenta - Used for selection highlights and secondary accents
    private static final Color NEON_MAGENTA = new Color(255, 0, 255);
    private static final Color NEON_MAGENTA_DIM = new Color(150, 0, 150);
    
    // Neon Green - Used for success states, valid moves, and timer
    private static final Color NEON_GREEN = new Color(57, 255, 20);
    private static final Color NEON_GREEN_DIM = new Color(30, 150, 10);
    
    // Neon Yellow - Used for warnings, status messages, and medium difficulty
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_YELLOW_DIM = new Color(150, 150, 0);
    
    // Neon Pink - Used for hints and accent elements
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_PINK_DIM = new Color(150, 10, 80);
    
    // Neon Blue - Used for number pad and secondary buttons
    private static final Color NEON_BLUE = new Color(0, 191, 255);
    private static final Color NEON_BLUE_DIM = new Color(0, 100, 150);
    
    // Neon Orange - Used for hard difficulty and solve button
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color NEON_ORANGE_DIM = new Color(150, 100, 0);
    
    // Neon Red - Used for errors, invalid moves, and quit/cancel
    private static final Color NEON_RED = new Color(255, 50, 50);
    
    // Neon Purple - Used for number pad borders
    private static final Color NEON_PURPLE = new Color(148, 0, 211);
    
    /** White neon color for bright highlights */
    private static final Color NEON_WHITE = new Color(255, 255, 255);
    
    /** Default text color (light gray) */
    private static final Color TEXT_COLOR = new Color(200, 200, 200);
    
    // =========================================================================
    // ANIMATION VARIABLES
    // =========================================================================
    
    /** Current glow intensity (0.0 to 1.0) for pulsing effects */
    private float glowIntensity = 0.0f;
    
    /** Direction of glow animation (true = increasing, false = decreasing) */
    private boolean glowIncreasing = true;
    
    // =========================================================================
    // UI PANEL REFERENCES
    // =========================================================================
    
    /** Main panel using CardLayout to switch between screens */
    private JPanel mainPanel;

    /** Layered pane holding background and content layers; kept as field for resize handling */
    private JLayeredPane layeredPane;
    
    /** CardLayout manager for switching between menu, difficulty, and game screens */
    private CardLayout cardLayout;
    
    // Card layout identifiers for each screen
    private static final String MENU_PANEL = "MENU";
    private static final String DIFFICULTY_PANEL = "DIFFICULTY";
    private static final String GAME_PANEL = "GAME";
    
    /** Background animation panel with floating dots */
    private DotAnimationPanel dotAnimation;
    
    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================
    
    /**
     * Constructs the Sudoku GUI application.
     * Sets up the window, initializes all components, and starts animations.
     */
    public SudokuGUI() {
        // Set window title
        setTitle("SUDOKU");
        
        // Exit application when window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Allow window resizing and fullscreen
        setResizable(true);

        // Enable macOS native fullscreen (green traffic-light button)
        getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.TRUE);

        // Resize layered-pane children whenever the window size changes (e.g. entering fullscreen)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();
                if (layeredPane != null) {
                    layeredPane.setPreferredSize(new Dimension(w, h));
                    layeredPane.setBounds(0, 0, w, h);
                    if (dotAnimation != null) dotAnimation.setBounds(0, 0, w, h);
                    if (mainPanel != null)   mainPanel.setBounds(0, 0, w, h);
                    layeredPane.revalidate();
                    layeredPane.repaint();
                }
            }
        });

        // Toggle macOS native fullscreen with Ctrl+Cmd+F (standard Mac shortcut)
        AbstractAction toggleFullscreen = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Class<?> appClass = Class.forName("com.apple.eawt.Application");
                    Object app = appClass.getMethod("getApplication").invoke(null);
                    appClass.getMethod("requestToggleFullScreen", java.awt.Window.class).invoke(app, SudokuGUI.this);
                } catch (Exception ex) {
                    // Fallback for non-Mac: toggle exclusive fullscreen
                    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    if (device.getFullScreenWindow() == SudokuGUI.this) {
                        device.setFullScreenWindow(null);
                    } else {
                        device.setFullScreenWindow(SudokuGUI.this);
                    }
                }
            }
        };
        // Ctrl+Cmd+F (Mac standard) and F11 (cross-platform fallback)
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "toggleFullscreen");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullscreen");
        getRootPane().getActionMap().put("toggleFullscreen", toggleFullscreen);
        
        // Create save directory if it doesn't exist
        new File(SAVE_DIR).mkdirs();
        
        // Set main background color
        getContentPane().setBackground(BLACK_BG);
        
        // Initialize all UI components
        initComponents();
        
        // Start the pulsing glow animation
        startGlowAnimation();
        
        // Pack components and center window on screen
        pack();
        setLocationRelativeTo(null);
    }
    
    // =========================================================================
    // INITIALIZATION METHODS
    // =========================================================================
    
    /**
     * Initializes all UI components and sets up the layered pane structure.
     * Creates the background animation layer and the main content layer.
     */
    private void initComponents() {
        // Create layered pane to hold background animation and UI layers
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        
        // Create and configure the dot animation background
        dotAnimation = new DotAnimationPanel();
        dotAnimation.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        layeredPane.add(dotAnimation, JLayeredPane.DEFAULT_LAYER);
        
        // Create main content panel with card layout for screen switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent
        mainPanel.setOpaque(false); // Allow background to show through
        mainPanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Create the three main screens
        JPanel menuPanel = createMainMenuPanel();           // Main menu screen
        JPanel difficultyPanel = createDifficultyPanel();   // Difficulty selection screen
        JPanel gamePanel = createGamePanel();               // Game play screen
        
        // Add panels to card layout with identifiers
        mainPanel.add(menuPanel, MENU_PANEL);
        mainPanel.add(difficultyPanel, DIFFICULTY_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        
        // Add main panel to layered pane (above background)
        layeredPane.add(mainPanel, JLayeredPane.PALETTE_LAYER);
        
        // Add layered pane to frame
        add(layeredPane);
        
        // Show main menu initially
        cardLayout.show(mainPanel, MENU_PANEL);
    }
    
    /**
     * Starts the pulsing glow animation timer.
     * This creates a breathing effect on glowing UI elements.
     */
    private void startGlowAnimation() {
        glowTimer = new Timer(50, e -> {
            // Oscillate glow intensity between 0.3 and 1.0
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
            // Trigger repaint to update glow effects
            repaint();
        });
        glowTimer.start();
    }
    
    // =========================================================================
    // PANEL CREATION METHODS
    // =========================================================================
    
    /**
     * Creates the main menu panel with title, subtitle, and menu buttons.
     * This is the first screen users see when launching the application.
     * 
     * @return JPanel containing the main menu UI
     */
    private JPanel createMainMenuPanel() {
        // Create panel with GridBagLayout for centered positioning
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw subtle background grid pattern for visual interest
                g2d.setColor(new Color(0, 40, 40, 30));
                for (int i = 0; i < getWidth(); i += 40) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 40) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        
        // Make panel transparent to show animated background
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(65, 130, 65, 130));
        
        // GridBagConstraints for positioning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(26, 0, 26, 0);
        
        // -------------------------------------------------------------------
        // TITLE LABEL - "SUDOKU" with animated pulsing glow effect
        // -------------------------------------------------------------------
        JLabel title = new JLabel("🔢 SUDOKU 🤓") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw animated glow effect around text
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
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 68));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(650, 100));
        
        // -------------------------------------------------------------------
        // SUBTITLE LABEL - Fun tagline with glow
        // -------------------------------------------------------------------
        JLabel subtitle = new JLabel("🔫 Train Your Brain! 🎯") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw glow effect
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
        subtitle.setFont(new Font("Arial Unicode MS", Font.PLAIN, 26));
        subtitle.setForeground(NEON_MAGENTA);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        // -------------------------------------------------------------------
        // MENU BUTTONS - Large neon buttons for navigation
        // -------------------------------------------------------------------
        
        // NEW GAME button - Green neon, navigates to difficulty selection
        NeonButton newGameBtn = new NeonButton("🎮 NEW GAME", NEON_GREEN, 31, new Dimension(520, 100));
        newGameBtn.addActionListener(e -> showDifficultyPanel());
        
        // LOAD GAME button - Yellow neon, opens load dialog
        NeonButton loadGameBtn = new NeonButton("📂 LOAD GAME", NEON_YELLOW, 31, new Dimension(520, 100));
        loadGameBtn.addActionListener(e -> showLoadGameDialog());
        
        // QUIT button - Red neon, exits application
        NeonButton quitBtn = new NeonButton("❌ QUIT", NEON_RED, 31, new Dimension(520, 100));
        quitBtn.addActionListener(e -> System.exit(0));
        
        // -------------------------------------------------------------------
        // CREDIT LABEL - Author attribution
        // -------------------------------------------------------------------
        JLabel creditLabel = new JLabel("GUI assembled by cjRem44x (°□°) ☝️");
        creditLabel.setFont(new Font("Arial Unicode MS", Font.ITALIC, 16));
        creditLabel.setForeground(new Color(100, 100, 110));
        creditLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // -------------------------------------------------------------------
        // ADD COMPONENTS TO PANEL
        // -------------------------------------------------------------------
        panel.add(title, gbc);
        gbc.insets = new Insets(13, 0, 65, 0);
        panel.add(subtitle, gbc);
        gbc.insets = new Insets(20, 0, 20, 0);
        panel.add(newGameBtn, gbc);
        panel.add(loadGameBtn, gbc);
        panel.add(quitBtn, gbc);
        
        // Add credit label with spacing
        gbc.insets = new Insets(39, 0, 0, 0);
        panel.add(creditLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the difficulty selection panel.
     * Shown after clicking NEW GAME on the main menu.
     * 
     * @return JPanel containing difficulty options
     */
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
        
        // Transparent background
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(65, 130, 65, 130));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // -------------------------------------------------------------------
        // TITLE - "SELECT DIFFICULTY"
        // -------------------------------------------------------------------
        JLabel title = new JLabel("SELECT DIFFICULTY") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Pulsing glow effect
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
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 47));
        title.setPreferredSize(new Dimension(650, 80));
        
        // -------------------------------------------------------------------
        // DIFFICULTY BUTTONS
        // -------------------------------------------------------------------
        
        // EASY - 30 empty cells (Green)
        NeonButton easyBtn = new NeonButton("⭐ EASY (30 CELLS)", NEON_GREEN, 26, new Dimension(520, 90));
        easyBtn.addActionListener(e -> startNewGame(30, "EASY"));
        
        // MEDIUM - 45 empty cells (Yellow)
        NeonButton mediumBtn = new NeonButton("⭐⭐ MEDIUM (45 CELLS)", NEON_YELLOW, 26, new Dimension(520, 90));
        mediumBtn.addActionListener(e -> startNewGame(45, "MEDIUM"));
        
        // HARD - 55 empty cells (Orange)
        NeonButton hardBtn = new NeonButton("⭐⭐⭐ HARD (55 CELLS)", NEON_ORANGE, 26, new Dimension(520, 90));
        hardBtn.addActionListener(e -> startNewGame(55, "HARD"));
        
        // CUSTOM - User-defined empty cells (Pink)
        NeonButton customBtn = new NeonButton("⚙ CUSTOM", NEON_PINK, 26, new Dimension(520, 90));
        customBtn.addActionListener(e -> showCustomDifficultyDialog());
        
        // BACK button - Returns to main menu (Blue)
        NeonButton backBtn = new NeonButton("← BACK TO MENU", NEON_BLUE, 23, new Dimension(390, 80));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, MENU_PANEL));
        
        // -------------------------------------------------------------------
        // ADD COMPONENTS TO PANEL
        // -------------------------------------------------------------------
        panel.add(title, gbc);
        gbc.insets = new Insets(26, 0, 26, 0);
        panel.add(easyBtn, gbc);
        panel.add(mediumBtn, gbc);
        panel.add(hardBtn, gbc);
        panel.add(customBtn, gbc);
        gbc.insets = new Insets(52, 0, 0, 0);
        panel.add(backBtn, gbc);
        
        return panel;
    }
    
    /**
     * Creates the main game panel containing the grid, timer, status, and controls.
     * This is where the actual Sudoku gameplay happens.
     * 
     * @return JPanel containing the game interface
     */
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(13, 13));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // -------------------------------------------------------------------
        // TOP PANEL - Contains back button, title, and timer
        // -------------------------------------------------------------------
        JPanel topPanel = new JPanel(new BorderLayout(13, 13));
        topPanel.setBackground(new Color(0, 0, 0, 100)); // Semi-transparent
        topPanel.setBorder(createGlowBorder(NEON_CYAN, 3));
        
        // BACK button - Returns to main menu
        NeonButton backBtn = new NeonButton("← MENU", NEON_BLUE, 16, new Dimension(130, 45));
        backBtn.addActionListener(e -> {
            isRunning = false; // Stop timer
            cardLayout.show(mainPanel, MENU_PANEL);
        });
        
        // TITLE label - Shows game title or difficulty
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
        titleLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 29));
        titleLabel.setPreferredSize(new Dimension(390, 52));
        
        // TIMER label - Shows elapsed time with green glow
        timerLabel = new JLabel("⏱ 00:00:00") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Green glow effect
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(57, 255, 20, 50));
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = getWidth() - fm.stringWidth(getText()) - 13;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
                
                g2d.setColor(NEON_GREEN);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = getWidth() - fm.stringWidth(getText()) - 13;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        timerLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
        timerLabel.setPreferredSize(new Dimension(195, 52));
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);
        
        // -------------------------------------------------------------------
        // STATUS PANEL - Shows game status and messages
        // -------------------------------------------------------------------
        statusLabel = new JLabel("SELECT A CELL TO BEGIN");
        statusLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 18));
        statusLabel.setForeground(NEON_YELLOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(createGlowBorder(NEON_YELLOW, 2));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(0, 0, 0, 100));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Combine top panels
        JPanel headerPanel = new JPanel(new BorderLayout(7, 7));
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // -------------------------------------------------------------------
        // GRID PANEL - The 9x9 Sudoku grid
        // -------------------------------------------------------------------
        JPanel gridPanel = createGridPanel();
        gridPanel.setBorder(createGlowBorder(NEON_MAGENTA, 4));
        
        // -------------------------------------------------------------------
        // CONTROL PANEL - Buttons and number pad
        // -------------------------------------------------------------------
        JPanel controlPanel = createControlPanel();
        
        // -------------------------------------------------------------------
        // ADD ALL PANELS TO MAIN PANEL
        // -------------------------------------------------------------------
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates a multi-layered glow border with the specified color and thickness.
     * Creates depth by using multiple semi-transparent border layers.
     * 
     * @param color The neon color for the border
     * @param thickness Main border thickness in pixels
     * @return Border with glow effect
     */
    private Border createGlowBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, thickness),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), 2),
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50), 1)
            )
        );
    }
    
    /**
     * Creates the Sudoku grid panel containing 81 cells (9x9).
     * Each cell is a custom JTextField with special rendering.
     * 
     * @return JPanel containing the grid
     */
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
    
    /**
     * Creates a single grid cell with custom rendering and input handling.
     * 
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @return JTextField configured as a Sudoku cell
     */
    private JTextField createCell(int row, int col) {
        JTextField cell = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw cell background
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw glow effect if cell is selected (magenta background)
                if (getBackground().equals(NEON_MAGENTA)) {
                    for (int i = 8; i > 0; i--) {
                        g2d.setColor(new Color(255, 0, 255, 30));
                        g2d.drawRoundRect(i/2, i/2, getWidth()-i, getHeight()-i, 5, 5);
                    }
                }
                
                // Draw text with glow effect
                String text = getText();
                if (!text.isEmpty()) {
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    
                    // Text glow for green and cyan text
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
        
        // Configure cell appearance
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBackground(new Color(10, 10, 15, 200)); // Semi-transparent dark
        cell.setForeground(TEXT_COLOR);
        cell.setCaretColor(NEON_CYAN);
        cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE_DIM, 1));
        cell.setSelectionColor(NEON_MAGENTA);
        cell.setSelectedTextColor(BLACK_BG);
        cell.setOpaque(false);
        
        final int r = row;
        final int c = col;
        
        // Focus listener - handles cell selection highlighting
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectedRow = r;
                selectedCol = c;
                cell.setBackground(NEON_MAGENTA); // Highlight selected cell
                cell.setForeground(BLACK_BG);
                cell.setBorder(BorderFactory.createLineBorder(NEON_YELLOW, 3));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                validateAndUpdateCell(r, c); // Validate input when leaving cell
            }
        });
        
        // Key listener - handles number input
        cell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch >= '1' && ch <= '9') {
                    // Valid number input
                    cell.setText(String.valueOf(ch));
                    e.consume();
                    validateAndUpdateCell(r, c);
                    moveToNextCell(r, c); // Auto-advance to next cell
                } else if (ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) {
                    // Clear cell on backspace/delete
                    cell.setText("");
                    e.consume();
                    clearCell(r, c);
                } else {
                    // Ignore invalid input
                    e.consume();
                }
            }
        });
        
        return cell;
    }
    
    /**
     * Creates the control panel with action buttons and number pad.
     * 
     * @return JPanel containing controls
     */
    private JPanel createControlPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(13, 13));
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
        
        // -------------------------------------------------------------------
        // BUTTON PANEL - Save, Load, Hint, Solve, Clear, New
        // -------------------------------------------------------------------
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(new Color(0, 0, 0, 100));
        buttonPanel.setBorder(createGlowBorder(NEON_BLUE, 2));
        
        // SAVE button - Green
        NeonButton saveBtn = new NeonButton("💾 SAVE", NEON_GREEN, 16, new Dimension(130, 50));
        saveBtn.addActionListener(e -> saveGame());
        
        // LOAD button - Yellow
        NeonButton loadBtn = new NeonButton("📂 LOAD", NEON_YELLOW, 16, new Dimension(130, 50));
        loadBtn.addActionListener(e -> showLoadGameDialog());
        
        // HINT button - Pink
        NeonButton hintBtn = new NeonButton("💡 HINT", NEON_PINK, 16, new Dimension(130, 50));
        hintBtn.addActionListener(e -> showHint());
        
        // SOLVE button - Orange
        NeonButton solveBtn = new NeonButton("🤖 SOLVE", NEON_ORANGE, 16, new Dimension(130, 50));
        solveBtn.addActionListener(e -> solvePuzzle());
        
        // CLEAR button - Red
        NeonButton clearBtn = new NeonButton("🗑 CLEAR", NEON_RED, 16, new Dimension(130, 50));
        clearBtn.addActionListener(e -> clearSelectedCell());
        
        // NEW button - Cyan (goes to difficulty selection)
        NeonButton newBtn = new NeonButton("🎮 NEW", NEON_CYAN, 16, new Dimension(130, 50));
        newBtn.addActionListener(e -> cardLayout.show(mainPanel, DIFFICULTY_PANEL));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(hintBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(newBtn);
        
        // -------------------------------------------------------------------
        // NUMBER PAD - Buttons 1-9 for number input
        // -------------------------------------------------------------------
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 7, 7));
        numberPanel.setBackground(new Color(0, 0, 0, 100));
        numberPanel.setBorder(createGlowBorder(NEON_PURPLE, 2));
        
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            NeonButton numBtn = new NeonButton(String.valueOf(i), NEON_BLUE, 21, new Dimension(58, 58));
            numBtn.addActionListener(e -> insertNumber(num));
            numberPanel.add(numBtn);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(numberPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    // =========================================================================
    // NAVIGATION METHODS
    // =========================================================================
    
    /**
     * Shows the difficulty selection panel.
     */
    private void showDifficultyPanel() {
        cardLayout.show(mainPanel, DIFFICULTY_PANEL);
    }
    
    // =========================================================================
    // DIALOG METHODS
    // =========================================================================
    
    /**
     * Shows the load game dialog with a list of saved games.
     */
    private void showLoadGameDialog() {
        JDialog dialog = new JDialog(this, "LOAD GAME", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        // Title
        JLabel title = new JLabel("SELECT SAVE FILE");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
        title.setForeground(NEON_CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_CYAN, 2));
        panel.add(title, BorderLayout.NORTH);
        
        // Get save files
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            // No saves found
            JLabel noSaves = new JLabel("NO SAVED GAMES FOUND");
            noSaves.setFont(new Font("Arial Unicode MS", Font.BOLD, 23));
            noSaves.setForeground(NEON_RED);
            noSaves.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noSaves, BorderLayout.CENTER);
            
            NeonButton okBtn = new NeonButton("✗ OK", NEON_RED, 18, new Dimension(156, 60));
            okBtn.addActionListener(e -> dialog.dispose());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setBackground(BLACK_BG);
            btnPanel.add(okBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
        } else {
            // List of save files
            String[] names = new String[saveFiles.length];
            for (int i = 0; i < saveFiles.length; i++) {
                names[i] = saveFiles[i].getName().replace(".sav", "");
            }
            
            JList<String> list = new JList<>(names);
            list.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
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
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
            btnPanel.setBackground(BLACK_BG);
            
            // LOAD button
            NeonButton loadBtn = new NeonButton("📂 LOAD", NEON_GREEN, 18, new Dimension(156, 60));
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
                        titleLabel.setText(difficulty);
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
            
            // CANCEL button
            NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 18, new Dimension(156, 60));
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            btnPanel.add(loadBtn);
            btnPanel.add(cancelBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
        }
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(585, 585);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Shows the custom difficulty dialog for specifying empty cell count.
     */
    private void showCustomDifficultyDialog() {
        JDialog dialog = new JDialog(this, "CUSTOM DIFFICULTY", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        JLabel title = new JLabel("⚙ CUSTOM DIFFICULTY ⚙");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
        title.setForeground(NEON_PINK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_PINK, 2));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(13, 13));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("EMPTY CELLS (1-80):");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
        label.setForeground(NEON_YELLOW);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("45", 10);
        field.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_CYAN);
        field.setCaretColor(NEON_CYAN);
        field.setBorder(createGlowBorder(NEON_CYAN, 2));
        field.setHorizontalAlignment(JTextField.CENTER);
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
        btnPanel.setBackground(BLACK_BG);
        
        // OK button
        NeonButton okBtn = new NeonButton("✓ OK", NEON_GREEN, 18, new Dimension(130, 60));
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
        
        // CANCEL button
        NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 18, new Dimension(130, 60));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(520, 364);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // =========================================================================
    // GAME METHODS
    // =========================================================================
    
    /**
     * Starts a new game with the specified difficulty.
     * 
     * @param emptyCells Number of empty cells in the puzzle
     * @param diff Difficulty level name
     */
    private void startNewGame(int emptyCells, String diff) {
        // Create new Sudoku game
        game = new Sudoku(GRID_SIZE, GRID_SIZE);
        game.fillNums(); // Fill with valid solution
        game.placeEmptyCells(emptyCells); // Remove cells to create puzzle
        
        difficulty = diff;
        elapsedTime = 0;
        startTime = System.currentTimeMillis();
        isRunning = true;
        
        // Update UI
        updateGridDisplay();
        startTimer();
        titleLabel.setText(difficulty);
        statusLabel.setText("SELECT A CELL TO BEGIN");
        statusLabel.setForeground(NEON_GREEN);
        
        // Show game panel
        cardLayout.show(mainPanel, GAME_PANEL);
    }
    
    /**
     * Updates the grid display with current game state.
     * Sets appropriate colors for fixed vs editable cells.
     */
    private void updateGridDisplay() {
        if (game == null) return;
        
        int[][] grid = game.getGrid();
        boolean[][] fixed = game.getFixedCells();
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = cells[row][col];
                int value = grid[row][col];
                
                // Set cell text
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                } else {
                    cell.setText("");
                }
                
                // Style based on whether cell is fixed (part of puzzle)
                if (fixed[row][col] && value != 0) {
                    // Fixed cell - dark background, cyan text, not editable
                    cell.setBackground(new Color(20, 20, 30));
                    cell.setForeground(NEON_CYAN);
                    cell.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
                    cell.setEditable(false);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_CYAN, 2));
                } else {
                    // Editable cell - semi-transparent, white text
                    cell.setBackground(new Color(10, 10, 15, 200));
                    cell.setForeground(TEXT_COLOR);
                    cell.setFont(new Font("Arial Unicode MS", Font.PLAIN, 31));
                    cell.setEditable(true);
                    cell.setBorder(BorderFactory.createLineBorder(NEON_BLUE_DIM, 1));
                }
            }
        }
    }
    
    /**
     * Validates and updates a cell when input changes.
     * 
     * @param row Cell row index
     * @param col Cell column index
     */
    private void validateAndUpdateCell(int row, int col) {
        if (game == null) return;
        
        JTextField cell = cells[row][col];
        String text = cell.getText().trim();
        
        if (text.isEmpty()) {
            // Cell cleared
            game.removeNum(row, col);
            cell.setBackground(new Color(10, 10, 15, 200));
            return;
        }
        
        try {
            int num = Integer.parseInt(text);
            if (num >= 1 && num <= 9) {
                if (game.isValid(row, col, num)) {
                    // Valid move
                    game.placeNum(row, col, num);
                    cell.setBackground(new Color(10, 10, 15, 200));
                    cell.setForeground(NEON_GREEN);
                    statusLabel.setText("✓ VALID MOVE");
                    statusLabel.setForeground(NEON_GREEN);
                    checkWin();
                } else {
                    // Invalid move - conflicts with Sudoku rules
                    cell.setBackground(NEON_RED);
                    cell.setForeground(Color.WHITE);
                    statusLabel.setText("✗ INVALID MOVE!");
                    statusLabel.setForeground(NEON_RED);
                }
            } else {
                // Invalid number range
                cell.setText("");
                game.removeNum(row, col);
            }
        } catch (NumberFormatException e) {
            // Not a number
            cell.setText("");
        }
    }
    
    /**
     * Clears a cell (removes number).
     * 
     * @param row Cell row index
     * @param col Cell column index
     */
    private void clearCell(int row, int col) {
        if (game != null) {
            game.removeNum(row, col);
            cells[row][col].setBackground(new Color(10, 10, 15, 200));
            cells[row][col].setForeground(TEXT_COLOR);
        }
    }
    
    /**
     * Moves focus to the next cell (right, wrapping to next row).
     * 
     * @param row Current row
     * @param col Current column
     */
    private void moveToNextCell(int row, int col) {
        int nextCol = (col + 1) % GRID_SIZE;
        int nextRow = (col + 1) == GRID_SIZE ? row + 1 : row;
        
        if (nextRow < GRID_SIZE) {
            cells[nextRow][nextCol].requestFocus();
        }
    }
    
    /**
     * Inserts a number into the currently selected cell.
     * 
     * @param num Number to insert (1-9)
     */
    private void insertNumber(int num) {
        if (selectedRow >= 0 && selectedCol >= 0 && game != null) {
            JTextField cell = cells[selectedRow][selectedCol];
            if (cell.isEditable()) {
                cell.setText(String.valueOf(num));
                validateAndUpdateCell(selectedRow, selectedCol);
            }
        }
    }
    
    /**
     * Clears the currently selected cell.
     */
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
    
    /**
     * Starts the game timer.
     */
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
    
    /**
     * Formats milliseconds as HH:MM:SS.
     * 
     * @param millis Time in milliseconds
     * @return Formatted time string
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    /**
     * Saves the current game state.
     */
    private void saveGame() {
        if (game == null) {
            showErrorDialog("NO GAME IN PROGRESS!");
            return;
        }
        
        JDialog dialog = new JDialog(this, "SAVE GAME", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        JLabel title = new JLabel("💾 SAVE GAME 💾");
        title.setFont(new Font("Arial Unicode MS", Font.BOLD, 31));
        title.setForeground(NEON_GREEN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(createGlowBorder(NEON_GREEN, 2));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(13, 13));
        inputPanel.setBackground(BLACK_BG);
        
        JLabel label = new JLabel("SAVE NAME:");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
        label.setForeground(NEON_CYAN);
        inputPanel.add(label, BorderLayout.NORTH);
        
        JTextField field = new JTextField("sudoku_save", 15);
        field.setFont(new Font("Arial Unicode MS", Font.BOLD, 23));
        field.setBackground(DARK_BG);
        field.setForeground(NEON_GREEN);
        field.setCaretColor(NEON_GREEN);
        field.setBorder(createGlowBorder(NEON_GREEN, 2));
        inputPanel.add(field, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
        btnPanel.setBackground(BLACK_BG);
        
        // SAVE button
        NeonButton okBtn = new NeonButton("✓ SAVE", NEON_GREEN, 18, new Dimension(130, 60));
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
        
        // CANCEL button
        NeonButton cancelBtn = new NeonButton("✗ CANCEL", NEON_RED, 18, new Dimension(130, 60));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(520, 364);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Shows an error dialog with the specified message.
     * 
     * @param message Error message to display
     */
    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog(this, "ERROR", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
        label.setForeground(NEON_RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        NeonButton okBtn = new NeonButton("✗ OK", NEON_RED, 18, new Dimension(130, 60));
        okBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a hint by highlighting a cell that needs a number.
     */
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
    
    /**
     * Shows an information dialog with the specified message.
     * 
     * @param message Info message to display
     */
    private void showInfoDialog(String message) {
        JDialog dialog = new JDialog(this, "INFO", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 21));
        label.setForeground(NEON_CYAN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        NeonButton okBtn = new NeonButton("✓ OK", NEON_GREEN, 18, new Dimension(130, 60));
        okBtn.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BLACK_BG);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Shows the solve confirmation dialog and auto-solves the puzzle if confirmed.
     */
    private void solvePuzzle() {
        if (game == null) return;
        
        JDialog dialog = new JDialog(this, "SOLVE PUZZLE", true);
        dialog.getContentPane().setBackground(BLACK_BG);
        
        JPanel panel = new JPanel(new BorderLayout(26, 26));
        panel.setBackground(BLACK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(39, 39, 39, 39));
        
        JLabel label = new JLabel("AUTO-SOLVE THE PUZZLE?");
        label.setFont(new Font("Arial Unicode MS", Font.BOLD, 23));
        label.setForeground(NEON_ORANGE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
        btnPanel.setBackground(BLACK_BG);
        
        // YES button
        NeonButton yesBtn = new NeonButton("✓ YES", NEON_GREEN, 18, new Dimension(130, 60));
        yesBtn.addActionListener(e -> {
            game.solve();
            updateGridDisplay();
            isRunning = false;
            dialog.dispose();
            checkWin();
        });
        
        // NO button
        NeonButton noBtn = new NeonButton("✗ NO", NEON_RED, 18, new Dimension(130, 60));
        noBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Checks if the puzzle is solved and shows victory dialog.
     */
    private void checkWin() {
        if (game != null && game.isSolved()) {
            isRunning = false;
            long current = System.currentTimeMillis();
            long total = elapsedTime + (current - startTime);
            
            JDialog dialog = new JDialog(this, "VICTORY!", true);
            dialog.getContentPane().setBackground(BLACK_BG);
            
            JPanel panel = new JPanel(new BorderLayout(26, 26));
            panel.setBackground(BLACK_BG);
            panel.setBorder(BorderFactory.createEmptyBorder(52, 52, 52, 52));
            
            JTextArea text = new JTextArea(
                "🎉🎉🎉 CONGRATULATIONS! 🎉🎉🎉\n\n" +
                "YOU SOLVED THE PUZZLE!\n\n" +
                "TIME: " + formatTime(total) + "\n" +
                "DIFFICULTY: " + difficulty
            );
            text.setFont(new Font("Arial Unicode MS", Font.BOLD, 23));
            text.setBackground(BLACK_BG);
            text.setForeground(NEON_GREEN);
            text.setEditable(false);
            text.setBorder(createGlowBorder(NEON_GREEN, 3));
            
            panel.add(text, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
            btnPanel.setBackground(BLACK_BG);
            
            // NEW GAME button
            NeonButton newBtn = new NeonButton("🎮 NEW GAME", NEON_CYAN, 18, new Dimension(195, 60));
            newBtn.addActionListener(e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, DIFFICULTY_PANEL);
            });
            
            // MENU button
            NeonButton menuBtn = new NeonButton("🏠 MENU", NEON_BLUE, 18, new Dimension(156, 60));
            menuBtn.addActionListener(e -> {
                dialog.dispose();
                cardLayout.show(mainPanel, MENU_PANEL);
            });
            
            btnPanel.add(newBtn);
            btnPanel.add(menuBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.pack();
            dialog.setSize(650, 455);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            statusLabel.setText("🎉 PUZZLE SOLVED! 🎉");
            statusLabel.setForeground(NEON_GREEN);
        }
    }
    
    // =========================================================================
    // NEON BUTTON CLASS
    // =========================================================================
    
    /**
     * Custom JButton with neon glow effects and hover animations.
     * Features multi-layered glow, smooth hover transitions, and rounded corners.
     */
    private class NeonButton extends JButton {
        /** Neon color for this button */
        private Color neonColor;
        
        /** Whether mouse is currently hovering over button */
        private boolean isHovered = false;
        
        /**
         * Creates a new NeonButton.
         * 
         * @param text Button text/label
         * @param neonColor Neon color for the button
         * @param fontSize Font size in points
         * @param size Preferred button size
         */
        public NeonButton(String text, Color neonColor, int fontSize, Dimension size) {
            super(text);
            this.neonColor = neonColor;
            
            // Set font and base appearance
            setFont(new Font("Arial Unicode MS", Font.BOLD, fontSize));
            setBackground(BLACK_BG);
            setForeground(neonColor);
            setBorder(createGlowBorder(neonColor, 2));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setPreferredSize(size);
            
            // Hover effect listeners
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
                // Strong glow when hovered - button fills with neon color
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
                // Text glow effect when not hovered
                for (int i = 3; i > 0; i--) {
                    g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 100));
                    g2d.drawString(text, x, y);
                }
            }
            
            g2d.setColor(getForeground());
            g2d.drawString(text, x, y);
            
            // Draw border outline
            if (!isHovered) {
                g2d.setColor(neonColor);
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
            }
            
            g2d.dispose();
        }
    }
    
    // =========================================================================
    // MAIN ENTRY POINT
    // =========================================================================
    
    /**
     * Main entry point for the Sudoku application.
     * Sets up the look and feel and launches the GUI.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Use system look and feel for native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default colors for Swing components
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
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}
