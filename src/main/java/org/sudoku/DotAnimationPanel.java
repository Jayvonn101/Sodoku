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
    
    private static final int NUM_DOTS = 150;
    private static final int MAX_DOT_SIZE = 4;
    
    // Dot properties
    private float[] dotX;
    private float[] dotY;
    private float[] dotSize;
    private float[] dotOpacity;
    private float[] dotSpeedX;
    private float[] dotSpeedY;
    private float[] dotPulse;
    private float[] dotPulseSpeed;
    
    // Monochrome colors
    private static final Color DOT_COLOR = new Color(80, 80, 85);
    private static final Color DOT_BRIGHT = new Color(120, 120, 125);
    private static final Color DOT_DIM = new Color(50, 50, 55);
    
    public DotAnimationPanel() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        
        initDots();
        
        animationTimer = new Timer(30, this);
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
        
        for (int i = 0; i < NUM_DOTS; i++) {
            resetDot(i);
        }
    }
    
    private void resetDot(int i) {
        dotX[i] = random.nextFloat() * 1000;
        dotY[i] = random.nextFloat() * 800;
        dotSize[i] = 1 + random.nextFloat() * MAX_DOT_SIZE;
        dotOpacity[i] = 0.1f + random.nextFloat() * 0.4f;
        dotSpeedX[i] = (random.nextFloat() - 0.5f) * 0.5f;
        dotSpeedY[i] = (random.nextFloat() - 0.5f) * 0.5f;
        dotPulse[i] = random.nextFloat() * (float) Math.PI * 2;
        dotPulseSpeed[i] = 0.02f + random.nextFloat() * 0.03f;
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
            0, height, new Color(8, 8, 12)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw connecting lines between nearby dots
        g2d.setStroke(new BasicStroke(0.5f));
        for (int i = 0; i < NUM_DOTS; i++) {
            for (int j = i + 1; j < NUM_DOTS; j++) {
                float dx = dotX[i] - dotX[j];
                float dy = dotY[i] - dotY[j];
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (distance < 80) {
                    float lineOpacity = (1 - distance / 80) * 0.15f;
                    int alpha = (int) (lineOpacity * 255);
                    g2d.setColor(new Color(60, 60, 65, alpha));
                    g2d.drawLine(
                        (int) dotX[i], (int) dotY[i],
                        (int) dotX[j], (int) dotY[j]
                    );
                }
            }
        }
        
        // Draw dots
        for (int i = 0; i < NUM_DOTS; i++) {
            // Update pulse
            dotPulse[i] += dotPulseSpeed[i];
            float pulseFactor = (float) Math.sin(dotPulse[i]) * 0.3f + 0.7f;
            
            // Calculate current size and opacity with pulse
            float currentSize = dotSize[i] * pulseFactor;
            float currentOpacity = dotOpacity[i] * pulseFactor;
            
            int alpha = (int) (currentOpacity * 255);
            
            // Draw dot with glow effect
            for (int r = (int) currentSize + 2; r > 0; r--) {
                float glowOpacity = (1 - (float) r / (currentSize + 2)) * 0.3f;
                int glowAlpha = (int) (glowOpacity * alpha);
                g2d.setColor(new Color(100, 100, 105, glowAlpha));
                g2d.fillOval(
                    (int) (dotX[i] - r), (int) (dotY[i] - r),
                    r * 2, r * 2
                );
            }
            
            // Draw main dot
            int dotAlpha = (int) (currentOpacity * 200);
            if (pulseFactor > 0.9f) {
                g2d.setColor(new Color(DOT_BRIGHT.getRed(), DOT_BRIGHT.getGreen(), DOT_BRIGHT.getBlue(), dotAlpha));
            } else {
                g2d.setColor(new Color(DOT_COLOR.getRed(), DOT_COLOR.getGreen(), DOT_COLOR.getBlue(), dotAlpha));
            }
            g2d.fillOval(
                (int) (dotX[i] - currentSize), (int) (dotY[i] - currentSize),
                (int) currentSize * 2, (int) currentSize * 2
            );
        }
        
        g2d.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        frameCount++;
        
        int width = getWidth();
        int height = getHeight();
        
        // Update dot positions
        for (int i = 0; i < NUM_DOTS; i++) {
            dotX[i] += dotSpeedX[i];
            dotY[i] += dotSpeedY[i];
            
            // Wrap around screen
            if (dotX[i] < -10) dotX[i] = width + 10;
            if (dotX[i] > width + 10) dotX[i] = -10;
            if (dotY[i] < -10) dotY[i] = height + 10;
            if (dotY[i] > height + 10) dotY[i] = -10;
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
