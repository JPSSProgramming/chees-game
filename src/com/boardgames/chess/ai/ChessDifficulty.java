package com.boardgames.chess.ai;

public enum ChessDifficulty {
    NOVICE("Новачок", 2, 300, 0.30),
    EASY("Легкий", 3, 800, 0.18),
    MEDIUM("Середній", 4, 1500, 0.06),
    HARD("Складний", 5, 3000, 0.0),
    EXPERT("Експерт", 6, 5000, 0.0);

    public final String label;
    public final int maxDepth;
    public final int timeBudgetMs;
    public final double randomness;

    ChessDifficulty(String label, int maxDepth, int timeBudgetMs, double randomness) {
        this.label = label;
        this.maxDepth = maxDepth;
        this.timeBudgetMs = timeBudgetMs;
        this.randomness = randomness;
    }

    @Override
    public String toString() {
        return label;
    }
}