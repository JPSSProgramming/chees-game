package com.boardgames.chess.ui;

import com.boardgames.chess.model.*;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChessBoardPanel extends JPanel {

    public interface MoveListener {
        void onMoveChosen(ChessMove move);
    }

    private static final int CELL = 68;
    private static final int MARGIN = 30;
    private static final Color LIGHT = new Color(238, 217, 183);
    private static final Color DARK = new Color(118, 79, 56);
    private static final Color SELECT_COLOR = new Color(90, 190, 120);
    private static final Color HINT_COLOR = new Color(70, 150, 230, 190);
    private static final Color CAPTURE_HINT = new Color(224, 90, 80, 200);
    private static final Color CHECK_COLOR = new Color(230, 60, 60, 150);

    private ChessBoard board;
    private List<ChessMove> legalMoves = new ArrayList<>();
    private Integer selectedRow = null, selectedCol = null;
    private int[] checkSquare = null;
    private MoveListener listener;
    private boolean interactive = true;
    private boolean flipped = false;

    public ChessBoardPanel() {
        int size = CELL * ChessBoard.SIZE + MARGIN * 2;
        setPreferredSize(new Dimension(size, size));
        setBackground(Theme.BG_PRIMARY);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    public void setListener(MoveListener listener) {
        this.listener = listener;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        repaint();
    }

    public void setLegalMoves(List<ChessMove> moves) {
        this.legalMoves = moves;
        selectedRow = null;
        selectedCol = null;
        repaint();
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        if (!interactive) {
            selectedRow = null;
            selectedCol = null;
        }
        repaint();
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        repaint();
    }

    public void setCheckSquare(int[] square) {
        this.checkSquare = square;
        repaint();
    }

    private int screenRow(int r) {
        return flipped ? ChessBoard.SIZE - 1 - r : r;
    }

    private int screenCol(int c) {
        return flipped ? ChessBoard.SIZE - 1 - c : c;
    }

    private int boardRowFromScreen(int sr) {
        return flipped ? ChessBoard.SIZE - 1 - sr : sr;
    }

    private int boardColFromScreen(int sc) {
        return flipped ? ChessBoard.SIZE - 1 - sc : sc;
    }

    private void handleClick(int x, int y) {
        if (!interactive || board == null) return;
        int sc = (x - MARGIN) / CELL, sr = (y - MARGIN) / CELL;
        if (sr < 0 || sr >= ChessBoard.SIZE || sc < 0 || sc >= ChessBoard.SIZE) return;
        int r = boardRowFromScreen(sr), c = boardColFromScreen(sc);

        if (selectedRow != null) {
            List<ChessMove> candidates = findMoves(selectedRow, selectedCol, r, c);
            if (!candidates.isEmpty()) {
                ChessMove chosen = candidates.size() == 1 ? candidates.get(0) : resolvePromotion(candidates);
                selectedRow = null;
                selectedCol = null;
                repaint();
                if (chosen != null && listener != null) listener.onMoveChosen(chosen);
                return;
            }
        }

        if (hasMovesFrom(r, c)) {
            selectedRow = r;
            selectedCol = c;
        } else {
            selectedRow = null;
            selectedCol = null;
        }
        repaint();
    }

    private ChessMove resolvePromotion(List<ChessMove> candidates) {
        String[] labels = {"Ферзь", "Тура", "Слон", "Кінь"};
        PieceType[] types = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        int choice = JOptionPane.showOptionDialog(this, "Оберіть фігуру для перетворення пішака:",
                "Перетворення пішака", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, labels, labels[0]);
        if (choice < 0) choice = 0;
        PieceType chosenType = types[choice];
        for (ChessMove m : candidates) if (m.promotion == chosenType) return m;
        return candidates.get(0);
    }

    private boolean hasMovesFrom(int r, int c) {
        for (ChessMove m : legalMoves) if (m.fromRow == r && m.fromCol == c) return true;
        return false;
    }

    private List<ChessMove> findMoves(int fr, int fc, int tr, int tc) {
        List<ChessMove> result = new ArrayList<>();
        for (ChessMove m : legalMoves)
            if (m.fromRow == fr && m.fromCol == fc && m.toRow == tr && m.toCol == tc) result.add(m);
        return result;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int boardSize = CELL * ChessBoard.SIZE;
        g.setColor(new Color(0x14161B));
        g.fillRoundRect(MARGIN - 10, MARGIN - 10, boardSize + 20, boardSize + 20, 14, 14);

        for (int r = 0; r < ChessBoard.SIZE; r++) {
            for (int c = 0; c < ChessBoard.SIZE; c++) {
                int sr = screenRow(r), sc = screenCol(c);
                g.setColor((r + c) % 2 == 0 ? LIGHT : DARK);
                g.fillRect(MARGIN + sc * CELL, MARGIN + sr * CELL, CELL, CELL);
            }
        }
        drawCoordinates(g, boardSize);
        if (board == null) return;

        if (checkSquare != null) {
            int sr = screenRow(checkSquare[0]), sc = screenCol(checkSquare[1]);
            g.setColor(CHECK_COLOR);
            g.fillOval(MARGIN + sc * CELL + 6, MARGIN + sr * CELL + 6, CELL - 12, CELL - 12);
        }

        if (selectedRow != null) {
            int sr = screenRow(selectedRow), sc = screenCol(selectedCol);
            g.setColor(SELECT_COLOR);
            g.setStroke(new BasicStroke(4));
            g.drawRect(MARGIN + sc * CELL + 2, MARGIN + sr * CELL + 2, CELL - 4, CELL - 4);

            for (ChessMove m : legalMoves) {
                if (m.fromRow == selectedRow && m.fromCol == selectedCol) {
                    int dsr = screenRow(m.toRow), dsc = screenCol(m.toCol);
                    boolean isCap = m.isCapture;
                    g.setColor(isCap ? CAPTURE_HINT : HINT_COLOR);
                    if (isCap) {
                        g.setStroke(new BasicStroke(4));
                        g.drawOval(MARGIN + dsc * CELL + 5, MARGIN + dsr * CELL + 5, CELL - 10, CELL - 10);
                    } else {
                        int d = CELL / 3;
                        g.fillOval(MARGIN + dsc * CELL + d, MARGIN + dsr * CELL + d, CELL - 2 * d, CELL - 2 * d);
                    }
                }
            }
        }

        Font font = new Font("Serif", Font.PLAIN, (int) (CELL * 0.78));
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        for (int r = 0; r < ChessBoard.SIZE; r++) {
            for (int c = 0; c < ChessBoard.SIZE; c++) {
                ChessPiece p = board.get(r, c);
                if (p == null) continue;
                String glyph = glyphFor(p);
                int sr = screenRow(r), sc = screenCol(c);
                int x = MARGIN + sc * CELL + (CELL - fm.stringWidth(glyph)) / 2;
                int y = MARGIN + sr * CELL + (CELL - fm.getHeight()) / 2 + fm.getAscent();

                Color fill = p.side == Side.WHITE ? new Color(250, 250, 248) : new Color(28, 28, 30);
                Color outline = p.side == Side.WHITE ? new Color(40, 40, 40) : new Color(215, 215, 215);

                g.setColor(outline);
                for (int[] o : new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {0, -1}, {0, 1}, {-1, 0}, {1, 0}}) {
                    g.drawString(glyph, x + o[0], y + o[1]);
                }
                g.setColor(fill);
                g.drawString(glyph, x, y);
            }
        }
    }

    private void drawCoordinates(Graphics2D g, int boardSize) {
        g.setFont(Theme.FONT_SUBTITLE);
        g.setColor(Theme.TEXT_SECONDARY);
        FontMetrics fm = g.getFontMetrics();
        for (int c = 0; c < ChessBoard.SIZE; c++) {
            int boardCol = boardColFromScreen(c);
            String label = String.valueOf((char) ('a' + boardCol));
            int x = MARGIN + c * CELL + (CELL - fm.stringWidth(label)) / 2;
            g.drawString(label, x, MARGIN + boardSize + 18);
        }
        for (int r = 0; r < ChessBoard.SIZE; r++) {
            int boardRow = boardRowFromScreen(r);
            String label = String.valueOf(8 - boardRow);
            int y = MARGIN + r * CELL + (CELL + fm.getAscent()) / 2 - 2;
            g.drawString(label, 10, y);
        }
    }

    private String glyphFor(ChessPiece p) {
        boolean w = p.side == Side.WHITE;
        return switch (p.type) {
            case KING -> w ? "\u2654" : "\u265A";
            case QUEEN -> w ? "\u2655" : "\u265B";
            case ROOK -> w ? "\u2656" : "\u265C";
            case BISHOP -> w ? "\u2657" : "\u265D";
            case KNIGHT -> w ? "\u2658" : "\u265E";
            case PAWN -> w ? "\u2659" : "\u265F";
        };
    }
}