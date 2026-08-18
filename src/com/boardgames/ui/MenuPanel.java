package com.boardgames.ui;

import com.boardgames.ui.theme.GradientPanel;
import com.boardgames.ui.theme.MenuCard;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends GradientPanel {

    public MenuPanel(Runnable onCheckers, Runnable onChess) {
        super(new Color(0x22252E), new Color(0x15161B));
        setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("BOARD GAMES");
        eyebrow.setFont(Theme.FONT_SECTION);
        eyebrow.setForeground(Theme.ACCENT);
        eyebrow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Choose a game");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(4, 0, 28, 0));

        MenuCard checkersCard = new MenuCard("⛂", "Checkers",
                "Ukrainian checkers with mandatory capture and checkers", onCheckers);
        checkersCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkersCard.setMaximumSize(new Dimension(440, 100));
        checkersCard.setPreferredSize(new Dimension(440, 100));

        MenuCard chessCard = new MenuCard("♚", "Chess",
                "Classic chess: castling, passing capture, checkmate", onChess);
        chessCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        chessCard.setMaximumSize(new Dimension(440, 100));
        chessCard.setPreferredSize(new Dimension(440, 100));

        content.add(eyebrow);
        content.add(title);
        content.add(checkersCard);
        content.add(Box.createVerticalStrut(18));
        content.add(chessCard);

        add(content);
    }
}