package com.boardgames.chess.model;

public final class ChessMove {
    public final int fromRow, fromCol, toRow, toCol;
    public final boolean isCapture;
    public final boolean isCastleKingSide;
    public final boolean isCastleQueenSide;
    public final boolean isEnPassant;
    public final PieceType promotion;

    public ChessMove(int fromRow, int fromCol, int toRow, int toCol, boolean isCapture,
                     boolean isCastleKingSide, boolean isCastleQueenSide, boolean isEnPassant,
                     PieceType promotion) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.isCapture = isCapture;
        this.isCastleKingSide = isCastleKingSide;
        this.isCastleQueenSide = isCastleQueenSide;
        this.isEnPassant = isEnPassant;
        this.promotion = promotion;
    }

    public static ChessMove quiet(int fr, int fc, int tr, int tc) {
        return new ChessMove(fr, fc, tr, tc, false, false, false, false, null);
    }

    public static ChessMove capture(int fr, int fc, int tr, int tc) {
        return new ChessMove(fr, fc, tr, tc, true, false, false, false, null);
    }

    public static ChessMove promotion(int fr, int fc, int tr, int tc, boolean capture, PieceType promo) {
        return new ChessMove(fr, fc, tr, tc, capture, false, false, false, promo);
    }

    public static ChessMove enPassant(int fr, int fc, int tr, int tc) {
        return new ChessMove(fr, fc, tr, tc, true, false, false, true, null);
    }

    public static ChessMove castleKingSide(int r, int kingFromCol, int kingToCol) {
        return new ChessMove(r, kingFromCol, r, kingToCol, false, true, false, false, null);
    }

    public static ChessMove castleQueenSide(int r, int kingFromCol, int kingToCol) {
        return new ChessMove(r, kingFromCol, r, kingToCol, false, false, true, false, null);
    }

    private static final String FILES = "abcdefgh";

    public String squareName(int r, int c) {
        return "" + FILES.charAt(c) + (8 - r);
    }

    @Override
    public String toString() {
        if (isCastleKingSide) return "O-O";
        if (isCastleQueenSide) return "O-O-O";
        String s = squareName(fromRow, fromCol) + (isCapture ? "x" : "-") + squareName(toRow, toCol);
        if (promotion != null) s += "=" + promotion.name().charAt(0);
        return s;
    }
}