package com.boardgames.checkers.ui;

import com.boardgames.checkers.model.Board;
import com.boardgames.checkers.model.Move;
import com.boardgames.checkers.model.Piece;
import com.boardgames.checkers.model.PlayerColor;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    public interface MoveListener {
        void onMoveChosen(Move move);
    }

    private static final int CELL = 68;
    private static final int MARGIN = 30;
    private static final Color LIGHT = new Color(235, 214, 175);
    private static final Color DARK = new Color(130, 84, 52);
    private static final Color SELECT_COLOR = new Color(90, 190, 120);
    private static final Color HINT_COLOR = new Color(70, 150, 230, 190);
    private static final Color FORCED_COLOR = new Color(224, 90, 80);

    private Board board;
    private List<Move> legalMoves = new ArrayList<>();
    private Integer selectedRow = null, selectedCol = null;
    private MoveListener listener;
    private boolean interactive = true;
    private boolean flipped = false;

    public BoardPanel() {
        int size = CELL * Board.SIZE + MARGIN * 2;
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

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    public void setLegalMoves(List<Move> moves) {
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

    private int screenRow(int boardRow) {
        return flipped ? Board.SIZE - 1 - boardRow : boardRow;
    }

    private int screenCol(int boardCol) {
        return flipped ? Board.SIZE - 1 - boardCol : boardCol;
    }

    private int boardRowFromScreen(int screenRow) {
        return flipped ? Board.SIZE - 1 - screenRow : screenRow;
    }

    private int boardColFromScreen(int screenCol) {
        return flipped ? Board.SIZE - 1 - screenCol : screenCol;
    }

    private void handleClick(int x, int y) {
        if (!interactive || board == null) return;
        int sc = (x - MARGIN) / CELL, sr = (y - MARGIN) / CELL;
        if (sr < 0 || sr >= Board.SIZE || sc < 0 || sc >= Board.SIZE) return;
        int r = boardRowFromScreen(sr), c = boardColFromScreen(sc);

        if (selectedRow != null) {
            Move chosen = findMove(selectedRow, selectedCol, r, c);
            if (chosen != null) {
                selectedRow = null;
                selectedCol = null;
                repaint();
                if (listener != null) listener.onMoveChosen(chosen);
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

    private boolean hasMovesFrom(int r, int c) {
        for (Move m : legalMoves) if (m.fromRow() == r && m.fromCol() == c) return true;
        return false;
    }

    private Move findMove(int fr, int fc, int tr, int tc) {
        for (Move m : legalMoves)
            if (m.fromRow() == fr && m.fromCol() == fc && m.toRow() == tr && m.toCol() == tc) return m;
        return null;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int boardSize = CELL * Board.SIZE;
        g.setColor(new Color(0x14161B));
        g.fillRoundRect(MARGIN - 10, MARGIN - 10, boardSize + 20, boardSize + 20, 14, 14);

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int sr = screenRow(r), sc = screenCol(c);
                g.setColor(Board.isPlayable(r, c) ? DARK : LIGHT);
                g.fillRect(MARGIN + sc * CELL, MARGIN + sr * CELL, CELL, CELL);
            }
        }
        drawCoordinates(g, boardSize);
        if (board == null) return;

        for (Move m : legalMoves) {
            if (!m.isCapture()) continue;
            int sr = screenRow(m.fromRow()), sc = screenCol(m.fromCol());
            g.setColor(FORCED_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawOval(MARGIN + sc * CELL + 4, MARGIN + sr * CELL + 4, CELL - 8, CELL - 8);
        }

        if (selectedRow != null) {
            int sr = screenRow(selectedRow), sc = screenCol(selectedCol);
            g.setColor(SELECT_COLOR);
            g.setStroke(new BasicStroke(4));
            g.drawRect(MARGIN + sc * CELL + 2, MARGIN + sr * CELL + 2, CELL - 4, CELL - 4);

            for (Move m : legalMoves) {
                if (m.fromRow() == selectedRow && m.fromCol() == selectedCol) {
                    int dsr = screenRow(m.toRow()), dsc = screenCol(m.toCol());
                    g.setColor(HINT_COLOR);
                    int d = CELL / 3;
                    g.fillOval(MARGIN + dsc * CELL + d, MARGIN + dsr * CELL + d, CELL - 2 * d, CELL - 2 * d);
                }
            }
        }

        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.get(r, c);
                if (!p.isEmpty()) drawPiece(g, screenRow(r), screenCol(c), p);
            }
    }

    private void drawCoordinates(Graphics2D g, int boardSize) {
        g.setFont(Theme.FONT_SUBTITLE);
        g.setColor(Theme.TEXT_SECONDARY);
        FontMetrics fm = g.getFontMetrics();
        for (int c = 0; c < Board.SIZE; c++) {
            int boardCol = boardColFromScreen(c);
            String label = String.valueOf((char) ('a' + boardCol));
            int x = MARGIN + c * CELL + (CELL - fm.stringWidth(label)) / 2;
            g.drawString(label, x, MARGIN + boardSize + 18);
        }
        for (int r = 0; r < Board.SIZE; r++) {
            int boardRow = boardRowFromScreen(r);
            String label = String.valueOf(8 - boardRow);
            int y = MARGIN + r * CELL + (CELL + fm.getAscent()) / 2 - 2;
            g.drawString(label, 10, y);
        }
    }

    private void drawPiece(Graphics2D g, int sr, int sc, Piece p) {
        int pad = 9;
        int x = MARGIN + sc * CELL + pad, y = MARGIN + sr * CELL + pad;
        int size = CELL - 2 * pad;

        g.setColor(new Color(0, 0, 0, 90));
        g.fillOval(x + 2, y + 5, size, size);

        Color top = p.getColor() == PlayerColor.WHITE ? new Color(255, 255, 255) : new Color(70, 70, 74);
        Color bottom = p.getColor() == PlayerColor.WHITE ? new Color(205, 205, 205) : new Color(20, 20, 22);
        RadialGradientPaint paint = new RadialGradientPaint(
                new Point(x + size / 3, y + size / 3), size,
                new float[]{0f, 1f}, new Color[]{top, bottom});
        g.setPaint(paint);
        g.fillOval(x, y, size, size);

        g.setColor(p.getColor() == PlayerColor.WHITE ? new Color(160, 160, 160) : Color.BLACK);
        g.setStroke(new BasicStroke(2.2f));
        g.drawOval(x, y, size, size);

        g.setColor(p.getColor() == PlayerColor.WHITE ? new Color(230, 230, 230) : new Color(90, 90, 94));
        g.setStroke(new BasicStroke(1.4f));
        g.drawOval(x + size / 6, y + size / 6, size - size / 3, size - size / 3);

        if (p.isKing()) {
            g.setColor(Theme.ACCENT);
            drawStar(g, x + size / 2, y + size / 2, size / 3, size / 7);
        }
    }

    private void drawStar(Graphics2D g, int cx, int cy, int rOuter, int rInner) {
        Polygon star = new Polygon();
        int points = 5;
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI / points * i - Math.PI / 2;
            int r = (i % 2 == 0) ? rOuter : rInner;
            star.addPoint(cx + (int) (Math.cos(angle) * r), cy + (int) (Math.sin(angle) * r));
        }
        g.fillPolygon(star);
    }
}