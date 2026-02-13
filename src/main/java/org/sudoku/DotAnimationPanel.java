package org.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class DotAnimationPanel extends JPanel implements ActionListener {
    private Timer animationTimer;
    private Random random = new Random();
    private int frameCount = 0;
    
    private static final int NUM_DOTS = 300;
    private static final int MAX_DOT_SIZE = 6;
    private static final int CONNECTION_DISTANCE = 100;
    
    // Dot properties
    private float[] dotX;
    private float[] dotY;
    private float[] dotSize;
    private float[] dotOpacity;
    private float[] dotSpeedX;
    private float[] dotSpeedY;
    private float[] dotPulse;
    private float[] dotPulseSpeed;
    private int[] dotBehavior; // 0=linear, 1=circular, 2=chaotic, 3=pulse
    private float[] dotAngle;
    private float[] dotOrbitRadius;
    
    // Colors - brighter for visibility
    private static final Color DOT_COLOR = new Color(120, 120, 130);
    private static final Color DOT_BRIGHT = new Color(160, 160, 170);
    private static final Color DOT_DIM = new Color(80, 80, 90);
    private static final Color LINE_COLOR = new Color(100, 100, 110);
    
    public DotAnimationPanel() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        
        initDots();
        
        animationTimer = new Timer(25, this);
        animationTimer.start();
    }
    
    private void initDots() {
        dotX = new float[NUM_DOTS];
        dotY = new float[NUM_DOTS];
        dotSize = new float[NUM_DOTS];
        dotOpacity = new float[NUM_DOTS];
        dotSpeedX = new float[NUM_DOTS];
        dotSpeedY = new float[NUM_DOTS];
        dotPulse = new float[NUM_DOTS];
        dotPulseSpeed = new float[NUM_DOTS];
        dotBehavior = new int[NUM_DOTS];
        dotAngle = new float[NUM_DOTS];
        dotOrbitRadius = new float[NUM_DOTS];
        
        for (int i = 0; i < NUM_DOTS; i++) {
            resetDot(i);
        }
    }
    
    private void resetDot(int i) {
        dotX[i] = random.nextFloat() * 1200;
        dotY[i] = random.nextFloat() * 900;
        dotSize[i] = 2 + random.nextFloat() * (MAX_DOT_SIZE - 2);
        dotOpacity[i] = 0.3f + random.nextFloat() * 0.5f;
        dotBehavior[i] = random.nextInt(4);
        dotAngle[i] = random.nextFloat() * (float) Math.PI * 2;
        dotOrbitRadius[i] = 20 + random.nextFloat() * 100;
        
        switch (dotBehavior[i]) {
            case 0: // Linear movement
                dotSpeedX[i] = (random.nextFloat() - 0.5f) * 1.5f;
                dotSpeedY[i] = (random.nextFloat() - 0.5f) * 1.5f;
                break;
            case 1: // Circular orbit
                dotSpeedX[i] = 0.5f + random.nextFloat() * 1.5f;
                dotSpeedY[i] = 0;
                break;
            case 2: // Chaotic/random
                dotSpeedX[i] = (random.nextFloat() - 0.5f) * 2f;
                dotSpeedY[i] = (random.nextFloat() - 0.5f) * 2f;
                break;
            case 3: // Pulse in place
                dotSpeedX[i] = 0;
                dotSpeedY[i] = 0;
                break;
        }
        
        dotPulse[i] = random.nextFloat() * (float) Math.PI * 2;
        dotPulseSpeed[i] = 0.03f + random.nextFloat() * 0.04f;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw subtle gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(5, 5, 8),
            width, height, new Color(10, 10, 15)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw connection lines with varying opacity
        g2d.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i < NUM_DOTS; i++) {
            for (int j = i + 1; j < NUM_DOTS; j++) {
                float dx = dotX[i] - dotX[j];
                float dy = dotY[i] - dotY[j];
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (distance < CONNECTION_DISTANCE) {
                    float lineOpacity = (1 - distance / CONNECTION_DISTANCE) * 0.25f;
                    int alpha = Math.min(255, Math.max(0, (int) (lineOpacity * 255)));
                    
                    // Randomly vary line color slightly
                    int colorVar = random.nextInt(20);
                    g2d.setColor(new Color(
                        Math.min(255, LINE_COLOR.getRed() + colorVar),
                        Math.min(255, LINE_COLOR.getGreen() + colorVar),
                        Math.min(255, LINE_COLOR.getBlue() + colorVar),
                        alpha
                    ));
                    
                    g2d.drawLine(
                        (int) dotX[i], (int) dotY[i],
                        (int) dotX[j], (int) dotY[j]
                    );
                }
            }
        }
        
        // Draw dots with glow and varying styles
        for (int i = 0; i < NUM_DOTS; i++) {
            // Update pulse
            dotPulse[i] += dotPulseSpeed[i];
            float pulseFactor = (float) Math.sin(dotPulse[i]) * 0.4f + 0.6f;
            
            // Calculate current size and opacity with pulse
            float currentSize = dotSize[i] * pulseFactor;
            float currentOpacity = dotOpacity[i] * pulseFactor;
            
            // Random flicker effect for some dots
            if (random.nextInt(50) == 0) {
                currentOpacity *= 1.5f;
            }
            
            int alpha = Math.min(255, Math.max(0, (int) (currentOpacity * 255)));
            
            // Draw outer glow (larger, more transparent)
            for (int r = (int) (currentSize * 3); r > currentSize; r -= 2) {
                float glowOpacity = (1 - (float) (r - currentSize) / (currentSize * 2)) * 0.15f;
                int glowAlpha = Math.min(255, Math.max(0, (int) (glowOpacity * alpha * 0.5f)));
                g2d.setColor(new Color(140, 140, 150, glowAlpha));
                g2d.fillOval(
                    (int) (dotX[i] - r), (int) (dotY[i] - r),
                    r * 2, r * 2
                );
            }
            
            // Draw medium glow
            for (int r = (int) (currentSize * 1.5f); r > currentSize; r--) {
                float glowOpacity = (1 - (float) (r - currentSize) / (currentSize * 0.5f)) * 0.3f;
                int glowAlpha = Math.min(255, Math.max(0, (int) (glowOpacity * alpha)));
                g2d.setColor(new Color(130, 130, 140, glowAlpha));
                g2d.fillOval(
                    (int) (dotX[i] - r), (int) (dotY[i] - r),
                    r * 2, r * 2
                );
            }
            
            // Draw main dot
            int dotAlpha = Math.min(255, Math.max(0, (int) (currentOpacity * 230)));
            if (pulseFactor > 0.85f || currentOpacity > 0.7f) {
                // Bright dot
                g2d.setColor(new Color(DOT_BRIGHT.getRed(), DOT_BRIGHT.getGreen(), DOT_BRIGHT.getBlue(), dotAlpha));
            } else {
                // Normal dot
                g2d.setColor(new Color(DOT_COLOR.getRed(), DOT_COLOR.getGreen(), DOT_COLOR.getBlue(), dotAlpha));
            }
            
            g2d.fillOval(
                (int) (dotX[i] - currentSize), (int) (dotY[i] - currentSize),
                (int) currentSize * 2, (int) currentSize * 2
            );
            
            // Draw bright center for larger dots
            if (currentSize > 3) {
                g2d.setColor(new Color(180, 180, 190, dotAlpha));
                g2d.fillOval(
                    (int) (dotX[i] - currentSize * 0.4f), (int) (dotY[i] - currentSize * 0.4f),
                    (int) (currentSize * 0.8f), (int) (currentSize * 0.8f)
                );
            }
        }
        
        // Draw some random geometric shapes occasionally
        if (frameCount % 60 == 0) {
            drawRandomShapes(g2d);
        }
        
        g2d.dispose();
    }
    
    private void drawRandomShapes(Graphics2D g2d) {
        int numShapes = random.nextInt(3) + 1;
        for (int i = 0; i < numShapes; i++) {
            int x = random.nextInt(getWidth());
            int y = random.nextInt(getHeight());
            int size = 30 + random.nextInt(50);
            
            g2d.setColor(new Color(100, 100, 110, 20));
            
            int shapeType = random.nextInt(3);
            switch (shapeType) {
                case 0: // Circle
                    g2d.drawOval(x, y, size, size);
                    break;
                case 1: // Square
                    g2d.drawRect(x, y, size, size);
                    break;
                case 2: // Triangle
                    int[] xPoints = {x, x + size/2, x + size};
                    int[] yPoints = {y + size, y, y + size};
                    g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        frameCount++;
        
        int width = getWidth();
        int height = getHeight();
        
        // Update dot positions based on behavior
        for (int i = 0; i < NUM_DOTS; i++) {
            switch (dotBehavior[i]) {
                case 0: // Linear
                    dotX[i] += dotSpeedX[i];
                    dotY[i] += dotSpeedY[i];
                    break;
                    
                case 1: // Circular orbit
                    dotAngle[i] += dotSpeedX[i] * 0.02f;
                    dotX[i] += Math.cos(dotAngle[i]) * dotOrbitRadius[i] * 0.02f;
                    dotY[i] += Math.sin(dotAngle[i]) * dotOrbitRadius[i] * 0.02f;
                    break;
                    
                case 2: // Chaotic - random direction changes
                    if (random.nextInt(30) == 0) {
                        dotSpeedX[i] += (random.nextFloat() - 0.5f) * 0.5f;
                        dotSpeedY[i] += (random.nextFloat() - 0.5f) * 0.5f;
                        // Clamp speeds
                        dotSpeedX[i] = Math.max(-2f, Math.min(2f, dotSpeedX[i]));
                        dotSpeedY[i] = Math.max(-2f, Math.min(2f, dotSpeedY[i]));
                    }
                    dotX[i] += dotSpeedX[i];
                    dotY[i] += dotSpeedY[i];
                    break;
                    
                case 3: // Pulse - slight drift
                    dotX[i] += (random.nextFloat() - 0.5f) * 0.3f;
                    dotY[i] += (random.nextFloat() - 0.5f) * 0.3f;
                    break;
            }
            
            // Randomly change behavior occasionally
            if (random.nextInt(1000) == 0) {
                int oldBehavior = dotBehavior[i];
                dotBehavior[i] = random.nextInt(4);
                if (oldBehavior != dotBehavior[i]) {
                    // Reinitialize speeds for new behavior
                    switch (dotBehavior[i]) {
                        case 0:
                            dotSpeedX[i] = (random.nextFloat() - 0.5f) * 1.5f;
                            dotSpeedY[i] = (random.nextFloat() - 0.5f) * 1.5f;
                            break;
                        case 1:
                            dotSpeedX[i] = 0.5f + random.nextFloat() * 1.5f;
                            break;
                        case 2:
                            dotSpeedX[i] = (random.nextFloat() - 0.5f) * 2f;
                            dotSpeedY[i] = (random.nextFloat() - 0.5f) * 2f;
                            break;
                        case 3:
                            dotSpeedX[i] = 0;
                            dotSpeedY[i] = 0;
                            break;
                    }
                }
            }
            
            // Wrap around screen with buffer
            float buffer = 50;
            if (dotX[i] < -buffer) dotX[i] = width + buffer;
            if (dotX[i] > width + buffer) dotX[i] = -buffer;
            if (dotY[i] < -buffer) dotY[i] = height + buffer;
            if (dotY[i] > height + buffer) dotY[i] = -buffer;
        }
        
        repaint();
    }
    
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    public void startAnimation() {
        if (animationTimer != null) {
            animationTimer.start();
        }
    }
}
