package com.boardgames.ui;

import com.boardgames.checkers.ui.CheckersGamePanel;
import com.boardgames.chess.ui.ChessGamePanel;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private static final String CARD_MENU = "menu";
    private static final String CARD_CHECKERS = "checkers";
    private static final String CARD_CHESS = "chess";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final CheckersGamePanel checkersPanel;
    private final ChessGamePanel chessPanel;

    public AppFrame() {
        super("Настільні ігри");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        MenuPanel menuPanel = new MenuPanel(this::openCheckers, this::openChess);
        checkersPanel = new CheckersGamePanel(this, this::openMenu);
        chessPanel = new ChessGamePanel(this, this::openMenu);

        cards.add(menuPanel, CARD_MENU);
        cards.add(checkersPanel, CARD_CHECKERS);
        cards.add(chessPanel, CARD_CHESS);

        setContentPane(cards);

        setMinimumSize(new Dimension(900, 700));
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void openMenu() {
        cardLayout.show(cards, CARD_MENU);
    }

    private void openCheckers() {
        cardLayout.show(cards, CARD_CHECKERS);
        checkersPanel.startNewGame();
    }

    private void openChess() {
        cardLayout.show(cards, CARD_CHESS);
        chessPanel.startNewGame();
    }
}