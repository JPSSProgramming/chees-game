package com.boardgames.checkers.ui;

import com.boardgames.checkers.ai.Difficulty;
import com.boardgames.checkers.model.PlayerColor;
import com.boardgames.ui.theme.RoundedButton;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class StartDialog extends JDialog {

    public enum Mode {PVP, PVA}

    private Mode chosenMode = Mode.PVA;
    private Difficulty chosenDifficulty = Difficulty.MEDIUM;
    private PlayerColor chosenHumanColor = PlayerColor.WHITE;
    private boolean confirmed = false;

    public StartDialog(Frame owner) {
        super(owner, "Нова гра — Шашки", true);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        JPanel content = new JPanel();
        content.setBackground(Theme.BG_PRIMARY);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel title = Theme.iconTitle("\u26C2", "Українські шашки", Theme.FONT_TITLE.deriveFont(22f), Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.setBackground(Theme.BG_SECONDARY);
        modePanel.setBorder(Theme.sectionBorder("Режим гри"));
        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButton pvpBtn = new JRadioButton("Двоє гравців (по черзі за одним комп'ютером)");
        JRadioButton pvaBtn = new JRadioButton("Проти штучного інтелекту", true);
        Theme.styleRadio(pvpBtn);
        Theme.styleRadio(pvaBtn);
        modeGroup.add(pvpBtn);
        modeGroup.add(pvaBtn);
        modePanel.add(pvaBtn);
        modePanel.add(pvpBtn);
        content.add(modePanel);
        content.add(Box.createVerticalStrut(10));

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        diffPanel.setBackground(Theme.BG_SECONDARY);
        diffPanel.setBorder(Theme.sectionBorder("Рівень складності ШІ"));
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(Difficulty.MEDIUM);
        Theme.styleCombo(diffBox);
        diffPanel.add(diffBox);
        content.add(diffPanel);
        content.add(Box.createVerticalStrut(10));

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBackground(Theme.BG_SECONDARY);
        colorPanel.setBorder(Theme.sectionBorder("Ваш колір (проти ШІ)"));
        ButtonGroup colorGroup = new ButtonGroup();
        JRadioButton whiteBtn = new JRadioButton("Білі (ходять першими)", true);
        JRadioButton blackBtn = new JRadioButton("Чорні");
        Theme.styleRadio(whiteBtn);
        Theme.styleRadio(blackBtn);
        colorGroup.add(whiteBtn);
        colorGroup.add(blackBtn);
        colorPanel.add(whiteBtn);
        colorPanel.add(blackBtn);
        content.add(colorPanel);

        Runnable updateEnabled = () -> {
            boolean pva = pvaBtn.isSelected();
            diffBox.setEnabled(pva);
            whiteBtn.setEnabled(pva);
            blackBtn.setEnabled(pva);
        };
        pvpBtn.addActionListener(e -> updateEnabled.run());
        pvaBtn.addActionListener(e -> updateEnabled.run());
        updateEnabled.run();

        RoundedButton startBtn = new RoundedButton("Почати гру", Theme.ACCENT, Theme.ACCENT_HOVER, Theme.TEXT_ON_ACCENT);
        startBtn.addActionListener(e -> {
            chosenMode = pvaBtn.isSelected() ? Mode.PVA : Mode.PVP;
            chosenDifficulty = (Difficulty) diffBox.getSelectedItem();
            chosenHumanColor = whiteBtn.isSelected() ? PlayerColor.WHITE : PlayerColor.BLACK;
            confirmed = true;
            setVisible(false);
        });

        content.add(Box.createVerticalStrut(20));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Theme.BG_PRIMARY);
        btnPanel.add(startBtn);
        content.add(btnPanel);

        add(content, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Mode getChosenMode() {
        return chosenMode;
    }

    public Difficulty getChosenDifficulty() {
        return chosenDifficulty;
    }

    public PlayerColor getChosenHumanColor() {
        return chosenHumanColor;
    }
}