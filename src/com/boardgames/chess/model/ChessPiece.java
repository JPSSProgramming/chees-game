package com.boardgames.chess.model;

public final class ChessPiece {
    public final PieceType type;
    public final Side side;

    public ChessPiece(PieceType type, Side side) {
        this.type = type;
        this.side = side;
    }

    public char fenChar() {
        char c;
        switch (type) {
            case PAWN: c = 'p'; break;
            case KNIGHT: c = 'n'; break;
            case BISHOP: c = 'b'; break;
            case ROOK: c = 'r'; break;
            case QUEEN: c = 'q'; break;
            default: c = 'k';
        }
        return side == Side.WHITE ? Character.toUpperCase(c) : c;
    }
}