package com.checkers.logic;

import com.checkers.model.Board;
import com.checkers.model.Move;
import com.checkers.model.PlayerColor;

import java.util.List;

public class CheckersGame {
    private Board board;
    private PlayerColor turn;
    private int movesWithoutCapture = 0;

    public CheckersGame() { reset(); }

    public void reset() {
        board = new Board();
        board.setupStandard();
        turn = PlayerColor.WHITE;
        movesWithoutCapture = 0;
    }

    public Board getBoard() { return board; }
    public PlayerColor getTurn() { return turn; }

    public List<Move> legalMovesForCurrentPlayer() {
        return MoveGenerator.generateLegalMoves(board, turn);
    }

    public void applyMove(Move move) {
        MoveGenerator.applyMove(board, move);
        movesWithoutCapture = move.isCapture() ? 0 : movesWithoutCapture + 1;
        turn = turn.opposite();
    }

    public enum Status { IN_PROGRESS, WHITE_WINS, BLACK_WINS, DRAW }

    public Status getStatus() {
        if (movesWithoutCapture >= 40) return Status.DRAW;
        boolean whiteHasPieces = board.countPieces(PlayerColor.WHITE) > 0;
        boolean blackHasPieces = board.countPieces(PlayerColor.BLACK) > 0;
        if (!whiteHasPieces) return Status.BLACK_WINS;
        if (!blackHasPieces) return Status.WHITE_WINS;
        if (!MoveGenerator.hasAnyMoves(board, turn)) {
            return turn == PlayerColor.WHITE ? Status.BLACK_WINS : Status.WHITE_WINS;
        }
        return Status.IN_PROGRESS;
    }

    public boolean isGameOver() { return getStatus() != Status.IN_PROGRESS; }
}