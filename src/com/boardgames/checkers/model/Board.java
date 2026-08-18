package com.boardgames.checkers.model;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] grid;

    public Board() {
        grid = new Piece[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = Piece.EMPTY;
    }

    public void setupStandard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid[r][c] = Piece.EMPTY;
                if (isPlayable(r, c)) {
                    if (r < 3) grid[r][c] = Piece.BLACK_MAN;
                    else if (r > 4) grid[r][c] = Piece.WHITE_MAN;
                }
            }
        }
    }

    public static boolean isPlayable(int r, int c) { return (r + c) % 2 == 1; }
    public static boolean inBounds(int r, int c) { return r >= 0 && r < SIZE && c >= 0 && c < SIZE; }

    public Piece get(int r, int c) { return grid[r][c]; }
    public void set(int r, int c, Piece p) { grid[r][c] = p; }

    public Board copy() {
        Board b = new Board();
        for (int r = 0; r < SIZE; r++) System.arraycopy(this.grid[r], 0, b.grid[r], 0, SIZE);
        return b;
    }

    public int countPieces(PlayerColor color) {
        int n = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c].getColor() == color) n++;
        return n;
    }
}