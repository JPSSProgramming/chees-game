package com.boardgames.checkers.ai;

public enum Difficulty {
    NOVICE("Beginner", 2, 0.35),
    EASY("Easy", 3, 0.20),
    MEDIUM("Middle", 5, 0.08),
    HARD("Hard", 7, 0.0),
    EXPERT("Expert", 9, 0.0);

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