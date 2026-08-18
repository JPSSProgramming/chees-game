package com.boardgames.chess.ui;

import com.boardgames.chess.ai.ChessDifficulty;
import com.boardgames.chess.model.Side;
import com.boardgames.ui.theme.RoundedButton;
import com.boardgames.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class ChessStartDialog extends JDialog {

    public enum Mode { PVP, PVA }

    private Mode chosenMode = Mode.PVA;
    private ChessDifficulty chosenDifficulty = ChessDifficulty.MEDIUM;
    private Side chosenHumanSide = Side.WHITE;
    private boolean confirmed = false;

    public ChessStartDialog(Frame owner) {
        super(owner, "Нова гра — Шахи", true);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Theme.BG_PRIMARY);

        JPanel content = new JPanel();
        content.setBackground(Theme.BG_PRIMARY);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel title = Theme.iconTitle("\u265A", "Шахи", Theme.FONT_TITLE.deriveFont(22f), Theme.TEXT_PRIMARY);
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
        modeGroup.add(pvpBtn); modeGroup.add(pvaBtn);
        modePanel.add(pvaBtn); modePanel.add(pvpBtn);
        content.add(modePanel);
        content.add(Box.createVerticalStrut(10));

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        diffPanel.setBackground(Theme.BG_SECONDARY);
        diffPanel.setBorder(Theme.sectionBorder("Рівень складності ШІ"));
        JComboBox<ChessDifficulty> diffBox = new JComboBox<>(ChessDifficulty.values());
        diffBox.setSelectedItem(ChessDifficulty.MEDIUM);
        Theme.styleCombo(diffBox);
        diffPanel.add(diffBox);
        content.add(diffPanel);
        content.add(Box.createVerticalStrut(10));

        JPanel sidePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sidePanel.setBackground(Theme.BG_SECONDARY);
        sidePanel.setBorder(Theme.sectionBorder("Ваш колір (проти ШІ)"));
        ButtonGroup sideGroup = new ButtonGroup();
        JRadioButton whiteBtn = new JRadioButton("Білі (ходять першими)", true);
        JRadioButton blackBtn = new JRadioButton("Чорні");
        Theme.styleRadio(whiteBtn);
        Theme.styleRadio(blackBtn);
        sideGroup.add(whiteBtn); sideGroup.add(blackBtn);
        sidePanel.add(whiteBtn); sidePanel.add(blackBtn);
        content.add(sidePanel);

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
            chosenDifficulty = (ChessDifficulty) diffBox.getSelectedItem();
            chosenHumanSide = whiteBtn.isSelected() ? Side.WHITE : Side.BLACK;
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

    public boolean isConfirmed() { return confirmed; }
    public Mode getChosenMode() { return chosenMode; }
    public ChessDifficulty getChosenDifficulty() { return chosenDifficulty; }
    public Side getChosenHumanSide() { return chosenHumanSide; }
}