package com.boardgames.checkers.model;

public enum PlayerColor {
    WHITE, BLACK;
    public PlayerColor opposite() { return this == WHITE ? BLACK : WHITE; }
}