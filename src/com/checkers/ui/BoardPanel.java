package com.checkers.ui;

import com.checkers.model.Board;
import com.checkers.model.Move;
import com.checkers.model.Piece;
import com.checkers.model.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {

    public interface MoveListener { void onMoveChosen(Move move); }

    private static final int CELL = 72;
    private static final Color LIGHT = new Color(235, 214, 175);
    private static final Color DARK = new Color(140, 92, 56);
    private static final Color SELECT_COLOR = new Color(90, 170, 90);
    private static final Color HINT_COLOR = new Color(60, 140, 220, 170);
    private static final Color FORCED_COLOR = new Color(220, 70, 70);

    private Board board;
    private List<Move> legalMoves = new ArrayList<>();
    private Integer selectedRow = null, selectedCol = null;
    private MoveListener listener;
    private boolean interactive = true;
    private boolean flipped = false;

    public BoardPanel() {
        setPreferredSize(new Dimension(CELL * Board.SIZE, CELL * Board.SIZE));
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { handleClick(e.getX(), e.getY()); }
        });
    }

    public void setListener(MoveListener listener) { this.listener = listener; }
    public void setBoard(Board board) { this.board = board; repaint(); }

    public void setLegalMoves(List<Move> moves) {
        this.legalMoves = moves;
        selectedRow = null; selectedCol = null;
        repaint();
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        if (!interactive) { selectedRow = null; selectedCol = null; }
        repaint();
    }

    public void setFlipped(boolean flipped) { this.flipped = flipped; repaint(); }

    private int screenRow(int boardRow) { return flipped ? Board.SIZE - 1 - boardRow : boardRow; }
    private int screenCol(int boardCol) { return flipped ? Board.SIZE - 1 - boardCol : boardCol; }
    private int boardRowFromScreen(int screenRow) { return flipped ? Board.SIZE - 1 - screenRow : screenRow; }
    private int boardColFromScreen(int screenCol) { return flipped ? Board.SIZE - 1 - screenCol : screenCol; }

    private void handleClick(int x, int y) {
        if (!interactive || board == null) return;
        int sc = x / CELL, sr = y / CELL;
        if (sr < 0 || sr >= Board.SIZE || sc < 0 || sc >= Board.SIZE) return;
        int r = boardRowFromScreen(sr), c = boardColFromScreen(sc);

        if (selectedRow != null) {
            Move chosen = findMove(selectedRow, selectedCol, r, c);
            if (chosen != null) {
                selectedRow = null; selectedCol = null;
                repaint();
                if (listener != null) listener.onMoveChosen(chosen);
                return;
            }
        }

        if (hasMovesFrom(r, c)) { selectedRow = r; selectedCol = c; }
        else { selectedRow = null; selectedCol = null; }
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

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int sr = screenRow(r), sc = screenCol(c);
                g.setColor(Board.isPlayable(r, c) ? DARK : LIGHT);
                g.fillRect(sc * CELL, sr * CELL, CELL, CELL);
            }
        }
        if (board == null) return;

        for (Move m : legalMoves) {
            if (!m.isCapture()) continue;
            int sr = screenRow(m.fromRow()), sc = screenCol(m.fromCol());
            g.setColor(FORCED_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawOval(sc * CELL + 4, sr * CELL + 4, CELL - 8, CELL - 8);
        }

        if (selectedRow != null) {
            int sr = screenRow(selectedRow), sc = screenCol(selectedCol);
            g.setColor(SELECT_COLOR);
            g.setStroke(new BasicStroke(4));
            g.drawRect(sc * CELL + 2, sr * CELL + 2, CELL - 4, CELL - 4);

            for (Move m : legalMoves) {
                if (m.fromRow() == selectedRow && m.fromCol() == selectedCol) {
                    int dsr = screenRow(m.toRow()), dsc = screenCol(m.toCol());
                    g.setColor(HINT_COLOR);
                    int d = CELL / 3;
                    g.fillOval(dsc * CELL + d, dsr * CELL + d, CELL - 2 * d, CELL - 2 * d);
                }
            }
        }

        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.get(r, c);
                if (!p.isEmpty()) drawPiece(g, screenRow(r), screenCol(c), p);
            }
    }

    private void drawPiece(Graphics2D g, int sr, int sc, Piece p) {
        int pad = 10;
        int x = sc * CELL + pad, y = sr * CELL + pad;
        int size = CELL - 2 * pad;

        Color base = p.getColor() == PlayerColor.WHITE ? Color.WHITE : new Color(35, 35, 35);
        Color rim = p.getColor() == PlayerColor.WHITE ? new Color(180, 180, 180) : Color.BLACK;

        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(x + 3, y + 5, size, size);

        g.setColor(base);
        g.fillOval(x, y, size, size);
        g.setColor(rim);
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(x, y, size, size);

        if (p.isKing()) {
            Color starColor = p.getColor() == PlayerColor.WHITE ? new Color(190, 150, 30) : new Color(230, 190, 60);
            g.setColor(starColor);
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