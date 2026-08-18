package com.boardgames.chess.ai;

import com.boardgames.chess.logic.ChessMoveGenerator;
import com.boardgames.chess.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ChessAIEngine {

    private final Random random = new Random();
    private long deadline;

    private static class TimeUp extends RuntimeException {}

    private static class ScoredMove {
        ChessMove move; int score;
        ScoredMove(ChessMove m, int s) { move = m; score = s; }
    }

    public ChessMove chooseMove(ChessBoard board, Side aiSide, ChessDifficulty diff) {
        List<ChessMove> rootMoves = ChessMoveGenerator.generateLegalMoves(board, aiSide);
        if (rootMoves.isEmpty()) return null;
        if (rootMoves.size() == 1) return rootMoves.get(0);

        deadline = System.currentTimeMillis() + diff.timeBudgetMs;
        ChessMove bestOverall = rootMoves.get(0);
        List<ScoredMove> lastCompleted = null;
        ChessMove preferred = null;

        for (int depth = 1; depth <= diff.maxDepth; depth++) {
            try {
                List<ChessMove> ordered = new ArrayList<>(rootMoves);
                orderMoves(board, ordered, preferred);
                List<ScoredMove> scored = new ArrayList<>();
                for (ChessMove m : ordered) {
                    ChessBoard copy = board.copy();
                    ChessMoveGenerator.applyMove(copy, m);
                    int val = -negamax(copy, aiSide.opposite(), depth - 1,
                            Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
                    scored.add(new ScoredMove(m, val));
                }
                scored.sort(Comparator.comparingInt((ScoredMove sm) -> sm.score).reversed());
                lastCompleted = scored;
                bestOverall = scored.get(0).move;
                preferred = bestOverall;
            } catch (TimeUp timeUp) {
                break;
            }
        }

        if (lastCompleted != null && diff.randomness > 0 && random.nextDouble() < diff.randomness
                && lastCompleted.size() > 1) {
            int pool = Math.min(3, lastCompleted.size());
            return lastCompleted.get(random.nextInt(pool)).move;
        }
        return bestOverall;
    }

    private int negamax(ChessBoard board, Side side, int depth, int alpha, int beta) {
        if (System.currentTimeMillis() > deadline) throw new TimeUp();

        List<ChessMove> moves = ChessMoveGenerator.generateLegalMoves(board, side);
        if (moves.isEmpty()) {
            if (ChessMoveGenerator.isInCheck(board, side)) return -100000 - depth;
            return 0;
        }
        if (depth == 0) return evalForSide(board, side);

        orderMoves(board, moves, null);
        int best = Integer.MIN_VALUE + 1;
        for (ChessMove m : moves) {
            ChessBoard copy = board.copy();
            ChessMoveGenerator.applyMove(copy, m);
            int val = -negamax(copy, side.opposite(), depth - 1, -beta, -alpha);
            if (val > best) best = val;
            if (best > alpha) alpha = best;
            if (alpha >= beta) break;
        }
        return best;
    }

    private void orderMoves(ChessBoard board, List<ChessMove> moves, ChessMove preferred) {
        moves.sort((a, b) -> {
            if (preferred != null) {
                boolean aPref = sameMove(a, preferred), bPref = sameMove(b, preferred);
                if (aPref != bPref) return aPref ? -1 : 1;
            }
            return Integer.compare(moveOrderScore(board, b), moveOrderScore(board, a));
        });
    }

    private boolean sameMove(ChessMove a, ChessMove b) {
        return a.fromRow == b.fromRow && a.fromCol == b.fromCol && a.toRow == b.toRow && a.toCol == b.toCol
                && a.promotion == b.promotion;
    }

    private int moveOrderScore(ChessBoard board, ChessMove m) {
        if (m.promotion != null) return 8000 + m.promotion.value;
        if (m.isEnPassant) return 1000;
        if (m.isCapture) {
            ChessPiece captured = board.get(m.toRow, m.toCol);
            ChessPiece attacker = board.get(m.fromRow, m.fromCol);
            int capturedVal = captured != null ? captured.type.value : 100;
            int attackerVal = attacker != null ? attacker.type.value : 0;
            return 1000 + capturedVal * 10 - attackerVal;
        }
        return 0;
    }

    private int evalForSide(ChessBoard board, Side side) {
        int white = staticEvalWhitePerspective(board);
        return side == Side.WHITE ? white : -white;
    }

    private int staticEvalWhitePerspective(ChessBoard board) {
        int score = 0;
        for (int r = 0; r < ChessBoard.SIZE; r++) {
            for (int c = 0; c < ChessBoard.SIZE; c++) {
                ChessPiece p = board.get(r, c);
                if (p == null) continue;
                int sign = p.side == Side.WHITE ? 1 : -1;
                score += sign * p.type.value;
                int center = centerBonus(r, c);
                switch (p.type) {
                    case PAWN -> {
                        int advance = p.side == Side.WHITE ? (6 - r) : (r - 1);
                        score += sign * (advance * 5 + center * 2);
                    }
                    case KNIGHT -> score += sign * center * 4;
                    case BISHOP, ROOK, QUEEN -> score += sign * center;
                    case KING -> score -= sign * center * 2;
                }
            }
        }
        return score;
    }

    private int centerBonus(int r, int c) {
        double dr = Math.abs(r - 3.5), dc = Math.abs(c - 3.5);
        return (int) (7 - (dr + dc));
    }
}