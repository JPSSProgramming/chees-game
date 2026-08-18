package com.boardgames.ui.theme;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private Color bg;
    private Color borderColor;
    private final int arc;
    private boolean shadow = true;

    public RoundedPanel(Color bg, int arc) {
        this.bg = bg;
        this.arc = arc;
        setOpaque(false);
    }

    public void setPanelBackground(Color bg) { this.bg = bg; repaint(); }
    public void setBorderColor(Color c) { this.borderColor = c; repaint(); }
    public void setShadow(boolean shadow) { this.shadow = shadow; repaint(); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        if (shadow) {
            g.setColor(new Color(0, 0, 0, 70));
            g.fillRoundRect(3, 5, w - 6, h - 6, arc, arc);
        }
        g.setColor(bg);
        g.fillRoundRect(0, 0, w - 4, h - 5, arc, arc);
        if (borderColor != null) {
            g.setColor(borderColor);
            g.setStroke(new BasicStroke(1.6f));
            g.drawRoundRect(0, 0, w - 5, h - 6, arc, arc);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}