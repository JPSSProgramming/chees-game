package com.boardgames.ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuCard extends JPanel {

    private boolean hover = false;
    private final int arc = 20;

    public MenuCard(String icon, String title, String subtitle, Runnable onClick) {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(Theme.FONT_SYMBOL_LARGE);
        iconLabel.setForeground(Theme.TEXT_PRIMARY);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_CARD_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("<html><div style='width:260px'>" + subtitle + "</div></html>");
        subtitleLabel.setFont(Theme.FONT_SUBTITLE);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        add(textPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(3, 5, w - 6, h - 6, arc, arc);

        g.setColor(hover ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
        g.fillRoundRect(0, 0, w - 4, h - 5, arc, arc);

        g.setColor(hover ? Theme.ACCENT : new Color(0x3D4250));
        g.setStroke(new BasicStroke(hover ? 2f : 1.4f));
        g.drawRoundRect(0, 0, w - 5, h - 6, arc, arc);

        g.dispose();
        super.paintComponent(g0);
    }
}