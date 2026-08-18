package com.boardgames.checkers.model;

public enum Piece {
    EMPTY(null, false),
    WHITE_MAN(PlayerColor.WHITE, false),
    WHITE_KING(PlayerColor.WHITE, true),
    BLACK_MAN(PlayerColor.BLACK, false),
    BLACK_KING(PlayerColor.BLACK, true);

    private final PlayerColor color;
    private final boolean king;

    Piece(PlayerColor color, boolean king) {
        this.color = color;
        this.king = king;
    }

    public PlayerColor getColor() {
        return color;
    }

    public boolean isKing() {
        return king;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isColor(PlayerColor c) {
        return color == c;
    }

    public Piece promoted() {
        if (this == WHITE_MAN) return WHITE_KING;
        if (this == BLACK_MAN) return BLACK_KING;
        return this;
    }
}