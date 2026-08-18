package com.boardgames.ui.theme;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private final Color bg;
    private final Color hoverBg;
    private final Color pressedBg;
    private final int arc;

    public RoundedButton(String text, Color bg, Color hoverBg, Color fg) {
        this(text, bg, hoverBg, fg, 14);
    }

    public RoundedButton(String text, Color bg, Color hoverBg, Color fg, int arc) {
        super(text);
        this.bg = bg;
        this.hoverBg = hoverBg;
        this.pressedBg = hoverBg.darker();
        this.arc = arc;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(fg);
        setFont(Theme.FONT_BUTTON);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 24));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = getModel().isPressed() ? pressedBg : (getModel().isRollover() ? hoverBg : bg);
        g.setColor(fill);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g.dispose();

        super.paintComponent(g0);
    }
}