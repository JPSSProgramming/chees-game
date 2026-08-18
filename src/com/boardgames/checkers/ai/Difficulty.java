package com.boardgames.checkers.ai;

public enum Difficulty {
    NOVICE("Новачок", 2, 0.35),
    EASY("Легкий", 3, 0.20),
    MEDIUM("Середній", 5, 0.08),
    HARD("Складний", 7, 0.0),
    EXPERT("Експерт", 9, 0.0);

    public final String label;
    public final int depth;
    public final double randomness;

    Difficulty(String label, int depth, double randomness) {
        this.label = label;
        this.depth = depth;
        this.randomness = randomness;
    }

    @Override
    public String toString() {
        return label;
    }
}