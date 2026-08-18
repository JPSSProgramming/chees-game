package com.boardgames.checkers.ui;

import com.boardgames.checkers.ai.AIEngine;
import com.boardgames.checkers.ai.Difficulty;
import com.boardgames.checkers.logic.CheckersGame;
import com.boardgames.checkers.model.Move;
import com.boardgames.checkers.model.PlayerColor;
import com.boardgames.ui.theme.RoundedButton;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CheckersGamePanel extends JPanel {

    private final Frame dialogOwner;
    private final Runnable onBackToMenu;

    private final CheckersGame game = new CheckersGame();
    private final AIEngine aiEngine = new AIEngine();
    private final BoardPanel boardPanel = new BoardPanel();
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel(" ", SwingConstants.CENTER);

    private StartDialog.Mode mode = StartDialog.Mode.PVA;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private PlayerColor humanColor = PlayerColor.WHITE;

    public CheckersGamePanel(Frame dialogOwner, Runnable onBackToMenu) {
        this.dialogOwner = dialogOwner;
        this.onBackToMenu = onBackToMenu;

        setLayout(new BorderLayout());
        setBackground(Theme.BG_PRIMARY);

        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_PRIMARY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(14, 10, 2, 10));
        scoreLabel.setFont(Theme.FONT_SCORE);
        scoreLabel.setForeground(Theme.TEXT_SECONDARY);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        RoundedButton newGameBtn = new RoundedButton("Нова гра", Theme.ACCENT, Theme.ACCENT_HOVER, Theme.TEXT_ON_ACCENT);
        newGameBtn.addActionListener(e -> showStartDialogAndReset());
        RoundedButton menuBtn = new RoundedButton("У головне меню", Theme.SECONDARY_BTN, Theme.SECONDARY_BTN_HOVER, Theme.TEXT_PRIMARY);
        menuBtn.addActionListener(e -> onBackToMenu.run());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.BG_SECONDARY);
        top.add(statusLabel, BorderLayout.CENTER);
        top.add(scoreLabel, BorderLayout.SOUTH);

        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG_PRIMARY);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 14, 0));
        bottom.add(newGameBtn);
        bottom.add(menuBtn);

        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(Theme.BG_PRIMARY);
        boardWrapper.add(boardPanel);

        add(top, BorderLayout.NORTH);
        add(boardWrapper, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        boardPanel.setListener(this::onHumanMove);
    }

    public void startNewGame() {
        showStartDialogAndReset();
    }

    private void showStartDialogAndReset() {
        StartDialog dialog = new StartDialog(dialogOwner);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            mode = dialog.getChosenMode();
            difficulty = dialog.getChosenDifficulty();
            humanColor = dialog.getChosenHumanColor();
        }
        game.reset();
        boardPanel.setFlipped(mode == StartDialog.Mode.PVA && humanColor == PlayerColor.BLACK);
        refresh();
        maybeTriggerAI();
    }

    private boolean isAiTurn() {
        return mode == StartDialog.Mode.PVA && game.getTurn() != humanColor;
    }

    private void onHumanMove(Move move) {
        if (game.isGameOver()) return;
        if (mode == StartDialog.Mode.PVA && game.getTurn() != humanColor) return;
        game.applyMove(move);
        refresh();
        maybeTriggerAI();
    }

    private void maybeTriggerAI() {
        if (game.isGameOver()) return;
        if (!isAiTurn()) return;

        boardPanel.setInteractive(false);
        statusLabel.setText("ШІ (" + difficulty.label + ") обмірковує хід...");

        PlayerColor aiColor = game.getTurn();
        Difficulty diff = difficulty;

        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override protected Move doInBackground() {
                return aiEngine.chooseMove(game.getBoard(), aiColor, diff);
            }
            @Override protected void done() {
                try {
                    Move move = get();
                    if (move != null && !game.isGameOver()) game.applyMove(move);
                } catch (Exception ex) { ex.printStackTrace(); }
                refresh();
                maybeTriggerAI();
            }
        };
        worker.execute();
    }

    private void refresh() {
        boardPanel.setBoard(game.getBoard());

        CheckersGame.Status status = game.getStatus();
        int white = game.getBoard().countPieces(PlayerColor.WHITE);
        int black = game.getBoard().countPieces(PlayerColor.BLACK);
        scoreLabel.setText("Білі: " + white + "    Чорні: " + black);

        if (status == CheckersGame.Status.IN_PROGRESS) {
            boolean humanTurn = mode == StartDialog.Mode.PVP || game.getTurn() == humanColor;
            String turnName = game.getTurn() == PlayerColor.WHITE ? "білих" : "чорних";
            statusLabel.setText("Хід " + turnName + (humanTurn ? "" : " (ШІ)"));
            statusLabel.setForeground(Theme.TEXT_PRIMARY);
            boardPanel.setInteractive(humanTurn);
            List<Move> legal = game.legalMovesForCurrentPlayer();
            boardPanel.setLegalMoves(humanTurn ? legal : java.util.Collections.emptyList());
        } else {
            boardPanel.setInteractive(false);
            boardPanel.setLegalMoves(java.util.Collections.emptyList());
            statusLabel.setForeground(Theme.ACCENT);
            switch (status) {
                case WHITE_WINS -> statusLabel.setText("Перемога білих \u2666");
                case BLACK_WINS -> statusLabel.setText("Перемога чорних \u2666");
                case DRAW -> statusLabel.setText("Нічия");
                default -> {}
            }
        }
    }
}