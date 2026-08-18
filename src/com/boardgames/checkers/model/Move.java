package com.boardgames.checkers.model;

import java.util.List;

public class Move {
    public final List<int[]> path;
    public final List<int[]> captured;

    public Move(List<int[]> path, List<int[]> captured) {
        this.path = path;
        this.captured = captured;
    }

    public int fromRow() { return path.get(0)[0]; }
    public int fromCol() { return path.get(0)[1]; }
    public int toRow() { return path.get(path.size() - 1)[0]; }
    public int toCol() { return path.get(path.size() - 1)[1]; }
    public boolean isCapture() { return !captured.isEmpty(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] cell : path) {
            if (sb.length() > 0) sb.append(isCapture() ? "x" : "-");
            sb.append((char) ('a' + cell[1])).append(8 - cell[0]);
        }
        return sb.toString();
    }
}