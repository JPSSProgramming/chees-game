package com.checkers.ui;

import com.checkers.ai.AIEngine;
import com.checkers.ai.Difficulty;
import com.checkers.logic.CheckersGame;
import com.checkers.model.Move;
import com.checkers.model.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final CheckersGame game = new CheckersGame();
    private final AIEngine aiEngine = new AIEngine();
    private final BoardPanel boardPanel = new BoardPanel();
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel(" ", SwingConstants.CENTER);

    private StartDialog.Mode mode = StartDialog.Mode.PVA;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private PlayerColor humanColor = PlayerColor.WHITE;

    public MainFrame() {
        super("Українські шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 18f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        JButton newGameBtn = new JButton("Нова гра");
        newGameBtn.addActionListener(e -> showStartDialogAndReset());

        JPanel top = new JPanel(new BorderLayout());
        top.add(statusLabel, BorderLayout.CENTER);
        top.add(scoreLabel, BorderLayout.SOUTH);

        JPanel bottom = new JPanel();
        bottom.add(newGameBtn);

        add(top, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        boardPanel.setListener(this::onHumanMove);

        pack();
        setLocationRelativeTo(null);
        showStartDialogAndReset();
    }

    private void showStartDialogAndReset() {
        StartDialog dialog = new StartDialog(this);
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
            boardPanel.setInteractive(humanTurn);
            List<Move> legal = game.legalMovesForCurrentPlayer();
            boardPanel.setLegalMoves(humanTurn ? legal : java.util.Collections.emptyList());
        } else {
            boardPanel.setInteractive(false);
            boardPanel.setLegalMoves(java.util.Collections.emptyList());
            switch (status) {
                case WHITE_WINS -> statusLabel.setText("Перемога білих! \u2666");
                case BLACK_WINS -> statusLabel.setText("Перемога чорних! \u2666");
                case DRAW -> statusLabel.setText("Нічия");
                default -> {}
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}