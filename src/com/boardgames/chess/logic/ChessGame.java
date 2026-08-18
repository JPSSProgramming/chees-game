package com.boardgames.chess.logic;

import com.boardgames.chess.model.ChessBoard;
import com.boardgames.chess.model.ChessMove;
import com.boardgames.chess.model.Side;

import java.util.List;

public class ChessGame {
    private ChessBoard board;
    private Side turn;
    private int halfMoveClock = 0;

    public ChessGame() { reset(); }

    public void reset() {
        board = new ChessBoard();
        board.setupStandard();
        turn = Side.WHITE;
        halfMoveClock = 0;
    }

    public ChessBoard getBoard() { return board; }
    public Side getTurn() { return turn; }

    public List<ChessMove> legalMovesForCurrentPlayer() {
        return ChessMoveGenerator.generateLegalMoves(board, turn);
    }

    public boolean isInCheck() {
        return ChessMoveGenerator.isInCheck(board, turn);
    }

    public void applyMove(ChessMove move) {
        boolean isPawnOrCapture = move.isCapture ||
                board.get(move.fromRow, move.fromCol) != null &&
                        board.get(move.fromRow, move.fromCol).type == com.boardgames.chess.model.PieceType.PAWN;
        ChessMoveGenerator.applyMove(board, move);
        halfMoveClock = isPawnOrCapture ? 0 : halfMoveClock + 1;
        turn = turn.opposite();
    }

    public enum Status { IN_PROGRESS, WHITE_WINS_MATE, BLACK_WINS_MATE, STALEMATE_DRAW, FIFTY_MOVE_DRAW, INSUFFICIENT_MATERIAL_DRAW }

    public Status getStatus() {
        if (!board.hasSufficientMaterial()) return Status.INSUFFICIENT_MATERIAL_DRAW;
        if (halfMoveClock >= 100) return Status.FIFTY_MOVE_DRAW;
        boolean hasMoves = ChessMoveGenerator.hasAnyLegalMove(board, turn);
        if (!hasMoves) {
            if (isInCheck()) {
                return turn == Side.WHITE ? Status.BLACK_WINS_MATE : Status.WHITE_WINS_MATE;
            }
            return Status.STALEMATE_DRAW;
        }
        return Status.IN_PROGRESS;
    }

    public boolean isGameOver() { return getStatus() != Status.IN_PROGRESS; }
}