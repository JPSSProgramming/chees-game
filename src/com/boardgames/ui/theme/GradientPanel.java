package com.boardgames.ui.theme;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private final Color top;
    private final Color bottom;

    public GradientPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
        super.paintComponent(g0);
    }
}