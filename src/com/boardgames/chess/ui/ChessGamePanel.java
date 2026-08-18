package com.boardgames.chess.ui;

import com.boardgames.chess.ai.ChessAIEngine;
import com.boardgames.chess.ai.ChessDifficulty;
import com.boardgames.chess.logic.ChessGame;
import com.boardgames.chess.model.ChessMove;
import com.boardgames.chess.model.Side;
import com.boardgames.ui.theme.RoundedButton;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessGamePanel extends JPanel {

    private final Frame dialogOwner;
    private final Runnable onBackToMenu;

    private final ChessGame game = new ChessGame();
    private final ChessAIEngine aiEngine = new ChessAIEngine();
    private final ChessBoardPanel boardPanel = new ChessBoardPanel();
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel(" ", SwingConstants.CENTER);

    private ChessStartDialog.Mode mode = ChessStartDialog.Mode.PVA;
    private ChessDifficulty difficulty = ChessDifficulty.MEDIUM;
    private Side humanSide = Side.WHITE;

    public ChessGamePanel(Frame dialogOwner, Runnable onBackToMenu) {
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
        ChessStartDialog dialog = new ChessStartDialog(dialogOwner);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            mode = dialog.getChosenMode();
            difficulty = dialog.getChosenDifficulty();
            humanSide = dialog.getChosenHumanSide();
        }
        game.reset();
        boardPanel.setFlipped(mode == ChessStartDialog.Mode.PVA && humanSide == Side.BLACK);
        refresh();
        maybeTriggerAI();
    }

    private boolean isAiTurn() {
        return mode == ChessStartDialog.Mode.PVA && game.getTurn() != humanSide;
    }

    private void onHumanMove(ChessMove move) {
        if (game.isGameOver()) return;
        if (mode == ChessStartDialog.Mode.PVA && game.getTurn() != humanSide) return;
        game.applyMove(move);
        refresh();
        maybeTriggerAI();
    }

    private void maybeTriggerAI() {
        if (game.isGameOver()) return;
        if (!isAiTurn()) return;

        boardPanel.setInteractive(false);
        statusLabel.setText("ШІ (" + difficulty.label + ") обмірковує хід...");
        statusLabel.setForeground(Theme.TEXT_PRIMARY);

        Side aiSide = game.getTurn();
        ChessDifficulty diff = difficulty;

        SwingWorker<ChessMove, Void> worker = new SwingWorker<>() {
            @Override
            protected ChessMove doInBackground() {
                return aiEngine.chooseMove(game.getBoard(), aiSide, diff);
            }

            @Override
            protected void done() {
                try {
                    ChessMove move = get();
                    if (move != null && !game.isGameOver()) game.applyMove(move);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                refresh();
                maybeTriggerAI();
            }
        };
        worker.execute();
    }

    private void refresh() {
        boardPanel.setBoard(game.getBoard());

        int[] checkSquare = null;
        if (game.isInCheck()) checkSquare = game.getBoard().findKing(game.getTurn());
        boardPanel.setCheckSquare(checkSquare);

        int whiteMaterial = materialValue(Side.WHITE);
        int blackMaterial = materialValue(Side.BLACK);
        scoreLabel.setText("Матеріал білих: " + whiteMaterial + "    Матеріал чорних: " + blackMaterial);

        ChessGame.Status status = game.getStatus();
        if (status == ChessGame.Status.IN_PROGRESS) {
            boolean humanTurn = mode == ChessStartDialog.Mode.PVP || game.getTurn() == humanSide;
            String turnName = game.getTurn() == Side.WHITE ? "білих" : "чорних";
            String checkSuffix = game.isInCheck() ? "  —  ШАХ!" : "";
            statusLabel.setText("Хід " + turnName + (humanTurn ? "" : " (ШІ)") + checkSuffix);
            statusLabel.setForeground(game.isInCheck() ? Theme.DANGER : Theme.TEXT_PRIMARY);
            boardPanel.setInteractive(humanTurn);
            List<ChessMove> legal = game.legalMovesForCurrentPlayer();
            boardPanel.setLegalMoves(humanTurn ? legal : java.util.Collections.emptyList());
        } else {
            boardPanel.setInteractive(false);
            boardPanel.setLegalMoves(java.util.Collections.emptyList());
            statusLabel.setForeground(Theme.ACCENT);
            switch (status) {
                case WHITE_WINS_MATE -> statusLabel.setText("Мат! Перемога білих \u2666");
                case BLACK_WINS_MATE -> statusLabel.setText("Мат! Перемога чорних \u2666");
                case STALEMATE_DRAW -> statusLabel.setText("Пат — нічия");
                case FIFTY_MOVE_DRAW -> statusLabel.setText("Нічия за правилом 50 ходів");
                case INSUFFICIENT_MATERIAL_DRAW -> statusLabel.setText("Нічия — недостатньо матеріалу");
                default -> {
                }
            }
        }
    }

    private int materialValue(Side side) {
        int sum = 0;
        var board = game.getBoard();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                var p = board.get(r, c);
                if (p != null && p.side == side) sum += p.type.value / 100;
            }
        return sum;
    }
}