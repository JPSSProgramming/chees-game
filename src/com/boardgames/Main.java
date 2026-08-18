package com.boardgames;

import com.boardgames.ui.AppFrame;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.installLookAndFeel();
            new AppFrame().setVisible(true);
        });
    }
}