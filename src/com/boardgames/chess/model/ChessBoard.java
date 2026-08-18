package com.boardgames.chess.model;

public class ChessBoard {
    public static final int SIZE = 8;

    private final ChessPiece[][] grid = new ChessPiece[SIZE][SIZE];

    public boolean whiteCanCastleKingSide = true;
    public boolean whiteCanCastleQueenSide = true;
    public boolean blackCanCastleKingSide = true;
    public boolean blackCanCastleQueenSide = true;

    public int[] enPassantTarget = null;

    public static boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    public ChessPiece get(int r, int c) {
        return grid[r][c];
    }

    public void set(int r, int c, ChessPiece p) {
        grid[r][c] = p;
    }

    public boolean isEmpty(int r, int c) {
        return grid[r][c] == null;
    }

    public void setupStandard() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = null;

        PieceType[] backRank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };
        for (int c = 0; c < SIZE; c++) {
            grid[0][c] = new ChessPiece(backRank[c], Side.BLACK);
            grid[1][c] = new ChessPiece(PieceType.PAWN, Side.BLACK);
            grid[6][c] = new ChessPiece(PieceType.PAWN, Side.WHITE);
            grid[7][c] = new ChessPiece(backRank[c], Side.WHITE);
        }
        whiteCanCastleKingSide = whiteCanCastleQueenSide = true;
        blackCanCastleKingSide = blackCanCastleQueenSide = true;
        enPassantTarget = null;
    }

    public ChessBoard copy() {
        ChessBoard b = new ChessBoard();
        for (int r = 0; r < SIZE; r++) System.arraycopy(this.grid[r], 0, b.grid[r], 0, SIZE);
        b.whiteCanCastleKingSide = this.whiteCanCastleKingSide;
        b.whiteCanCastleQueenSide = this.whiteCanCastleQueenSide;
        b.blackCanCastleKingSide = this.blackCanCastleKingSide;
        b.blackCanCastleQueenSide = this.blackCanCastleQueenSide;
        b.enPassantTarget = this.enPassantTarget == null ? null
                : new int[]{this.enPassantTarget[0], this.enPassantTarget[1]};
        return b;
    }

    public int[] findKing(Side side) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                ChessPiece p = grid[r][c];
                if (p != null && p.type == PieceType.KING && p.side == side) return new int[]{r, c};
            }
        return null;
    }

    public boolean hasSufficientMaterial() {
        int nonKingCount = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] != null && grid[r][c].type != PieceType.KING) nonKingCount++;
        return nonKingCount > 0;
    }
}