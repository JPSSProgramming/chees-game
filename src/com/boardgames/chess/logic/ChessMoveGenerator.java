package com.boardgames.chess.logic;

import com.boardgames.chess.model.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMoveGenerator {

    private static final int[][] KNIGHT_OFFSETS = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
    private static final int[][] DIAG_DIRS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private static final int[][] ORTHO_DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final PieceType[] PROMOTIONS = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

    public static List<ChessMove> generateLegalMoves(ChessBoard board, Side side) {
        List<ChessMove> pseudo = generatePseudoLegalMoves(board, side);
        List<ChessMove> legal = new ArrayList<>();
        for (ChessMove m : pseudo) {
            ChessBoard copy = board.copy();
            applyMove(copy, m);
            int[] king = copy.findKing(side);
            if (king != null && !isSquareAttacked(copy, king[0], king[1], side.opposite())) {
                legal.add(m);
            }
        }
        return legal;
    }

    public static boolean isInCheck(ChessBoard board, Side side) {
        int[] king = board.findKing(side);
        return king != null && isSquareAttacked(board, king[0], king[1], side.opposite());
    }

    private static List<ChessMove> generatePseudoLegalMoves(ChessBoard board, Side side) {
        List<ChessMove> moves = new ArrayList<>();
        for (int r = 0; r < ChessBoard.SIZE; r++) {
            for (int c = 0; c < ChessBoard.SIZE; c++) {
                ChessPiece p = board.get(r, c);
                if (p == null || p.side != side) continue;
                switch (p.type) {
                    case PAWN -> addPawnMoves(board, r, c, side, moves);
                    case KNIGHT -> addOffsetMoves(board, r, c, side, KNIGHT_OFFSETS, moves);
                    case BISHOP -> addSlidingMoves(board, r, c, side, DIAG_DIRS, moves);
                    case ROOK -> addSlidingMoves(board, r, c, side, ORTHO_DIRS, moves);
                    case QUEEN -> {
                        addSlidingMoves(board, r, c, side, DIAG_DIRS, moves);
                        addSlidingMoves(board, r, c, side, ORTHO_DIRS, moves);
                    }
                    case KING -> {
                        addKingStepMoves(board, r, c, side, moves);
                        addCastlingMoves(board, r, c, side, moves);
                    }
                }
            }
        }
        return moves;
    }

    private static void addPawnMoves(ChessBoard board, int r, int c, Side side, List<ChessMove> moves) {
        int dir = side == Side.WHITE ? -1 : 1;
        int startRow = side == Side.WHITE ? 6 : 1;
        int promoRow = side == Side.WHITE ? 0 : 7;

        int r1 = r + dir;
        if (ChessBoard.inBounds(r1, c) && board.isEmpty(r1, c)) {
            if (r1 == promoRow) {
                for (PieceType pt : PROMOTIONS) moves.add(ChessMove.promotion(r, c, r1, c, false, pt));
            } else {
                moves.add(ChessMove.quiet(r, c, r1, c));
            }
            if (r == startRow) {
                int r2 = r + 2 * dir;
                if (ChessBoard.inBounds(r2, c) && board.isEmpty(r2, c)) {
                    moves.add(ChessMove.quiet(r, c, r2, c));
                }
            }
        }
        for (int dc : new int[]{-1, 1}) {
            int cc = c + dc;
            if (!ChessBoard.inBounds(r1, cc)) continue;
            ChessPiece target = board.get(r1, cc);
            if (target != null && target.side != side) {
                if (r1 == promoRow) {
                    for (PieceType pt : PROMOTIONS) moves.add(ChessMove.promotion(r, c, r1, cc, true, pt));
                } else {
                    moves.add(ChessMove.capture(r, c, r1, cc));
                }
            } else if (target == null && board.enPassantTarget != null
                    && board.enPassantTarget[0] == r1 && board.enPassantTarget[1] == cc) {
                moves.add(ChessMove.enPassant(r, c, r1, cc));
            }
        }
    }

    private static void addOffsetMoves(ChessBoard board, int r, int c, Side side, int[][] offsets, List<ChessMove> moves) {
        for (int[] o : offsets) {
            int rr = r + o[0], cc = c + o[1];
            if (!ChessBoard.inBounds(rr, cc)) continue;
            ChessPiece target = board.get(rr, cc);
            if (target == null) moves.add(ChessMove.quiet(r, c, rr, cc));
            else if (target.side != side) moves.add(ChessMove.capture(r, c, rr, cc));
        }
    }

    private static void addKingStepMoves(ChessBoard board, int r, int c, Side side, List<ChessMove> moves) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int rr = r + dr, cc = c + dc;
                if (!ChessBoard.inBounds(rr, cc)) continue;
                ChessPiece target = board.get(rr, cc);
                if (target == null) moves.add(ChessMove.quiet(r, c, rr, cc));
                else if (target.side != side) moves.add(ChessMove.capture(r, c, rr, cc));
            }
        }
    }

    private static void addSlidingMoves(ChessBoard board, int r, int c, Side side, int[][] dirs, List<ChessMove> moves) {
        for (int[] d : dirs) {
            int rr = r + d[0], cc = c + d[1];
            while (ChessBoard.inBounds(rr, cc)) {
                ChessPiece target = board.get(rr, cc);
                if (target == null) {
                    moves.add(ChessMove.quiet(r, c, rr, cc));
                } else {
                    if (target.side != side) moves.add(ChessMove.capture(r, c, rr, cc));
                    break;
                }
                rr += d[0];
                cc += d[1];
            }
        }
    }

    private static void addCastlingMoves(ChessBoard board, int r, int c, Side side, List<ChessMove> moves) {
        int row = side == Side.WHITE ? 7 : 0;
        if (r != row || c != 4) return;
        Side enemy = side.opposite();
        boolean kingSideRight = side == Side.WHITE ? board.whiteCanCastleKingSide : board.blackCanCastleKingSide;
        boolean queenSideRight = side == Side.WHITE ? board.whiteCanCastleQueenSide : board.blackCanCastleQueenSide;

        if (isSquareAttacked(board, row, 4, enemy)) return;

        if (kingSideRight && board.isEmpty(row, 5) && board.isEmpty(row, 6)) {
            ChessPiece rook = board.get(row, 7);
            if (rook != null && rook.type == PieceType.ROOK && rook.side == side
                    && !isSquareAttacked(board, row, 5, enemy) && !isSquareAttacked(board, row, 6, enemy)) {
                moves.add(ChessMove.castleKingSide(row, 4, 6));
            }
        }
        if (queenSideRight && board.isEmpty(row, 1) && board.isEmpty(row, 2) && board.isEmpty(row, 3)) {
            ChessPiece rook = board.get(row, 0);
            if (rook != null && rook.type == PieceType.ROOK && rook.side == side
                    && !isSquareAttacked(board, row, 3, enemy) && !isSquareAttacked(board, row, 2, enemy)) {
                moves.add(ChessMove.castleQueenSide(row, 4, 2));
            }
        }
    }

    public static boolean isSquareAttacked(ChessBoard board, int r, int c, Side bySide) {
        int pawnDir = bySide == Side.WHITE ? -1 : 1;
        int pr = r - pawnDir;
        for (int dc : new int[]{-1, 1}) {
            int pc = c + dc;
            if (ChessBoard.inBounds(pr, pc)) {
                ChessPiece p = board.get(pr, pc);
                if (p != null && p.side == bySide && p.type == PieceType.PAWN) return true;
            }
        }
        for (int[] o : KNIGHT_OFFSETS) {
            int rr = r + o[0], cc = c + o[1];
            if (ChessBoard.inBounds(rr, cc)) {
                ChessPiece p = board.get(rr, cc);
                if (p != null && p.side == bySide && p.type == PieceType.KNIGHT) return true;
            }
        }
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int rr = r + dr, cc = c + dc;
                if (ChessBoard.inBounds(rr, cc)) {
                    ChessPiece p = board.get(rr, cc);
                    if (p != null && p.side == bySide && p.type == PieceType.KING) return true;
                }
            }
        }
        for (int[] d : DIAG_DIRS) {
            int rr = r + d[0], cc = c + d[1];
            while (ChessBoard.inBounds(rr, cc)) {
                ChessPiece p = board.get(rr, cc);
                if (p != null) {
                    if (p.side == bySide && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) return true;
                    break;
                }
                rr += d[0];
                cc += d[1];
            }
        }
        for (int[] d : ORTHO_DIRS) {
            int rr = r + d[0], cc = c + d[1];
            while (ChessBoard.inBounds(rr, cc)) {
                ChessPiece p = board.get(rr, cc);
                if (p != null) {
                    if (p.side == bySide && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) return true;
                    break;
                }
                rr += d[0];
                cc += d[1];
            }
        }
        return false;
    }

    public static void applyMove(ChessBoard board, ChessMove m) {
        ChessPiece moving = board.get(m.fromRow, m.fromCol);
        board.set(m.fromRow, m.fromCol, null);

        if (m.isEnPassant) {
            board.set(m.fromRow, m.toCol, null);
        }

        if (m.isCastleKingSide) {
            int row = m.fromRow;
            ChessPiece rook = board.get(row, 7);
            board.set(row, 7, null);
            board.set(row, 5, rook);
        } else if (m.isCastleQueenSide) {
            int row = m.fromRow;
            ChessPiece rook = board.get(row, 0);
            board.set(row, 0, null);
            board.set(row, 3, rook);
        }

        ChessPiece placed = m.promotion != null ? new ChessPiece(m.promotion, moving.side) : moving;
        board.set(m.toRow, m.toCol, placed);

        if (moving.type == PieceType.KING) {
            if (moving.side == Side.WHITE) {
                board.whiteCanCastleKingSide = false;
                board.whiteCanCastleQueenSide = false;
            } else {
                board.blackCanCastleKingSide = false;
                board.blackCanCastleQueenSide = false;
            }
        }
        if (moving.type == PieceType.ROOK) {
            if (moving.side == Side.WHITE) {
                if (m.fromRow == 7 && m.fromCol == 0) board.whiteCanCastleQueenSide = false;
                if (m.fromRow == 7 && m.fromCol == 7) board.whiteCanCastleKingSide = false;
            } else {
                if (m.fromRow == 0 && m.fromCol == 0) board.blackCanCastleQueenSide = false;
                if (m.fromRow == 0 && m.fromCol == 7) board.blackCanCastleKingSide = false;
            }
        }
        if (m.toRow == 7 && m.toCol == 0) board.whiteCanCastleQueenSide = false;
        if (m.toRow == 7 && m.toCol == 7) board.whiteCanCastleKingSide = false;
        if (m.toRow == 0 && m.toCol == 0) board.blackCanCastleQueenSide = false;
        if (m.toRow == 0 && m.toCol == 7) board.blackCanCastleKingSide = false;

        board.enPassantTarget = null;
        if (moving.type == PieceType.PAWN && Math.abs(m.toRow - m.fromRow) == 2) {
            board.enPassantTarget = new int[]{(m.toRow + m.fromRow) / 2, m.fromCol};
        }
    }

    public static boolean hasAnyLegalMove(ChessBoard board, Side side) {
        return !generateLegalMoves(board, side).isEmpty();
    }
}