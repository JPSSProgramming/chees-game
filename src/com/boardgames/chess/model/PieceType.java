package com.boardgames.chess.model;

public enum PieceType {
    PAWN(100), KNIGHT(320), BISHOP(330), ROOK(500), QUEEN(900), KING(20000);
    public final int value;
    PieceType(int value) { this.value = value; }
}