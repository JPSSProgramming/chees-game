package com.checkers.ai;

import com.checkers.logic.MoveGenerator;
import com.checkers.model.Board;
import com.checkers.model.Move;
import com.checkers.model.Piece;
import com.checkers.model.PlayerColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AIEngine {

    private final Random random = new Random();

    public Move chooseMove(Board board, PlayerColor aiColor, Difficulty diff) {
        List<Move> moves = MoveGenerator.generateLegalMoves(board, aiColor);
        if (moves.isEmpty()) return null;
        if (moves.size() == 1) return moves.get(0);

        List<ScoredMove> scored = new ArrayList<>();
        for (Move m : moves) {
            Board copy = board.copy();
            MoveGenerator.applyMove(copy, m);
            int val = -negamax(copy, aiColor.opposite(), diff.depth - 1,
                    Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
            scored.add(new ScoredMove(m, val));
        }
        scored.sort(Comparator.comparingInt((ScoredMove sm) -> sm.score).reversed());

        if (diff.randomness > 0 && random.nextDouble() < diff.randomness && scored.size() > 1) {
            int pool = Math.min(3, scored.size());
            return scored.get(random.nextInt(pool)).move;
        }
        return scored.get(0).move;
    }

    private static class ScoredMove {
        Move move; int score;
        ScoredMove(Move m, int s) { move = m; score = s; }
    }

    private int negamax(Board board, PlayerColor color, int depth, int alpha, int beta) {
        List<Move> moves = MoveGenerator.generateLegalMoves(board, color);
        if (moves.isEmpty()) return -50000 - depth;
        if (depth == 0) return evalForColor(board, color);

        int best = Integer.MIN_VALUE + 1;
        for (Move m : moves) {
            Board copy = board.copy();
            MoveGenerator.applyMove(copy, m);
            int val = -negamax(copy, color.opposite(), depth - 1, -beta, -alpha);
            if (val > best) best = val;
            if (best > alpha) alpha = best;
            if (alpha >= beta) break;
        }
        return best;
    }

    private int evalForColor(Board board, PlayerColor color) {
        int white = staticEvalWhitePerspective(board);
        return color == PlayerColor.WHITE ? white : -white;
    }

    private int staticEvalWhitePerspective(Board board) {
        int score = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.get(r, c);
                if (p.isEmpty()) continue;
                int sign = p.getColor() == PlayerColor.WHITE ? 1 : -1;
                if (p.isKing()) {
                    score += sign * 350;
                    if (c >= 2 && c <= 5) score += sign * 6;
                } else {
                    score += sign * 100;
                    int advance = p.getColor() == PlayerColor.WHITE ? (7 - r) : r;
                    score += sign * advance * 3;
                    if (c >= 2 && c <= 5) score += sign * 4;
                    if (c == 0 || c == Board.SIZE - 1) score += sign * 3;
                }
            }
        }
        return score;
    }
}