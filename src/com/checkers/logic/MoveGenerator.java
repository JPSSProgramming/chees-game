package com.checkers.logic;

import com.checkers.model.Board;
import com.checkers.model.Move;
import com.checkers.model.Piece;
import com.checkers.model.PlayerColor;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    private static final int[][] DIRS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    public static List<Move> generateLegalMoves(Board board, PlayerColor color) {
        List<Move> captures = new ArrayList<>();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.get(r, c);
                if (p.getColor() == color) {
                    List<int[]> path = new ArrayList<>();
                    path.add(new int[]{r, c});
                    captures.addAll(collectCaptures(board, r, c, p, path, new ArrayList<>()));
                }
            }
        }

        if (!captures.isEmpty()) {
            int max = 0;
            for (Move m : captures) max = Math.max(max, m.captured.size());
            List<Move> best = new ArrayList<>();
            for (Move m : captures) if (m.captured.size() == max) best.add(m);
            return best;
        }

        List<Move> normal = new ArrayList<>();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece p = board.get(r, c);
                if (p.getColor() == color) normal.addAll(collectNormalMoves(board, r, c, p));
            }
        }
        return normal;
    }

    private static List<Move> collectNormalMoves(Board board, int r, int c, Piece p) {
        List<Move> moves = new ArrayList<>();
        if (p.isKing()) {
            for (int[] d : DIRS) {
                int rr = r + d[0], cc = c + d[1];
                while (Board.inBounds(rr, cc) && board.get(rr, cc).isEmpty()) {
                    List<int[]> path = new ArrayList<>();
                    path.add(new int[]{r, c});
                    path.add(new int[]{rr, cc});
                    moves.add(new Move(path, new ArrayList<>()));
                    rr += d[0];
                    cc += d[1];
                }
            }
        } else {
            int forward = p.getColor() == PlayerColor.WHITE ? -1 : 1;
            for (int dc : new int[]{-1, 1}) {
                int rr = r + forward, cc = c + dc;
                if (Board.inBounds(rr, cc) && board.get(rr, cc).isEmpty()) {
                    List<int[]> path = new ArrayList<>();
                    path.add(new int[]{r, c});
                    path.add(new int[]{rr, cc});
                    moves.add(new Move(path, new ArrayList<>()));
                }
            }
        }
        return moves;
    }

    private static List<Move> collectCaptures(Board board, int r, int c, Piece p,
                                              List<int[]> pathSoFar, List<int[]> capturedSoFar) {
        List<Move> result = new ArrayList<>();
        boolean isKing = p.isKing();

        if (!isKing) {
            int[] dRows = {-1, -1, 1, 1};
            int[] dCols = {-1, 1, -1, 1};
            for (int i = 0; i < 4; i++) {
                int dr = dRows[i], dc = dCols[i];
                int er = r + dr, ec = c + dc;
                int lr = r + 2 * dr, lc = c + 2 * dc;
                if (!Board.inBounds(lr, lc)) continue;
                Piece enemy = board.get(er, ec);
                if (enemy.isEmpty() || enemy.getColor() == p.getColor()) continue;
                if (!board.get(lr, lc).isEmpty()) continue;

                Board copy = board.copy();
                copy.set(r, c, Piece.EMPTY);
                copy.set(er, ec, Piece.EMPTY);
                copy.set(lr, lc, p);

                List<int[]> newPath = new ArrayList<>(pathSoFar);
                newPath.add(new int[]{lr, lc});
                List<int[]> newCaptured = new ArrayList<>(capturedSoFar);
                newCaptured.add(new int[]{er, ec});

                List<Move> deeper = collectCaptures(copy, lr, lc, p, newPath, newCaptured);
                if (deeper.isEmpty()) result.add(new Move(new ArrayList<>(newPath), new ArrayList<>(newCaptured)));
                else result.addAll(deeper);
            }
        } else {
            for (int[] d : DIRS) {
                int dr = d[0], dc = d[1];
                int rr = r + dr, cc = c + dc;
                while (Board.inBounds(rr, cc) && board.get(rr, cc).isEmpty()) { rr += dr; cc += dc; }
                if (!Board.inBounds(rr, cc)) continue;
                Piece enemy = board.get(rr, cc);
                if (enemy.isEmpty() || enemy.getColor() == p.getColor()) continue;
                int er = rr, ec = cc;
                int lr = er + dr, lc = ec + dc;
                while (Board.inBounds(lr, lc) && board.get(lr, lc).isEmpty()) {
                    Board copy = board.copy();
                    copy.set(r, c, Piece.EMPTY);
                    copy.set(er, ec, Piece.EMPTY);
                    copy.set(lr, lc, p);

                    List<int[]> newPath = new ArrayList<>(pathSoFar);
                    newPath.add(new int[]{lr, lc});
                    List<int[]> newCaptured = new ArrayList<>(capturedSoFar);
                    newCaptured.add(new int[]{er, ec});

                    List<Move> deeper = collectCaptures(copy, lr, lc, p, newPath, newCaptured);
                    if (deeper.isEmpty()) result.add(new Move(new ArrayList<>(newPath), new ArrayList<>(newCaptured)));
                    else result.addAll(deeper);

                    lr += dr; lc += dc;
                }
            }
        }
        return result;
    }

    public static void applyMove(Board board, Move move) {
        int fr = move.fromRow(), fc = move.fromCol();
        Piece p = board.get(fr, fc);
        board.set(fr, fc, Piece.EMPTY);
        for (int[] cap : move.captured) board.set(cap[0], cap[1], Piece.EMPTY);

        int tr = move.toRow(), tc = move.toCol();
        if (!p.isKing() && ((p.getColor() == PlayerColor.WHITE && tr == 0) ||
                (p.getColor() == PlayerColor.BLACK && tr == Board.SIZE - 1))) {
            p = p.promoted();
        }
        board.set(tr, tc, p);
    }

    public static boolean hasAnyMoves(Board board, PlayerColor color) {
        return !generateLegalMoves(board, color).isEmpty();
    }
}