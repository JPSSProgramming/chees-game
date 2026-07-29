package com.checkers.ui;

import com.checkers.ai.Difficulty;
import com.checkers.model.PlayerColor;

import javax.swing.*;
import java.awt.*;

public class StartDialog extends JDialog {

    public enum Mode { PVP, PVA }

    private Mode chosenMode = Mode.PVA;
    private Difficulty chosenDifficulty = Difficulty.MEDIUM;
    private PlayerColor chosenHumanColor = PlayerColor.WHITE;
    private boolean confirmed = false;

    public StartDialog(Frame owner) {
        super(owner, "Нова гра — Шашки", true);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Українські шашки");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(15));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.setBorder(BorderFactory.createTitledBorder("Режим гри"));
        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButton pvpBtn = new JRadioButton("Двоє гравців (по черзі за одним комп'ютером)");
        JRadioButton pvaBtn = new JRadioButton("Проти штучного інтелекту", true);
        modeGroup.add(pvpBtn);
        modeGroup.add(pvaBtn);
        modePanel.add(pvaBtn);
        modePanel.add(pvpBtn);
        content.add(modePanel);

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        diffPanel.setBorder(BorderFactory.createTitledBorder("Рівень складності ШІ"));
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(Difficulty.MEDIUM);
        diffPanel.add(diffBox);
        content.add(diffPanel);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Ваш колір (проти ШІ)"));
        ButtonGroup colorGroup = new ButtonGroup();
        JRadioButton whiteBtn = new JRadioButton("Білі (ходять першими)", true);
        JRadioButton blackBtn = new JRadioButton("Чорні");
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

        JButton startBtn = new JButton("Почати гру");
        startBtn.addActionListener(e -> {
            chosenMode = pvaBtn.isSelected() ? Mode.PVA : Mode.PVP;
            chosenDifficulty = (Difficulty) diffBox.getSelectedItem();
            chosenHumanColor = whiteBtn.isSelected() ? PlayerColor.WHITE : PlayerColor.BLACK;
            confirmed = true;
            setVisible(false);
        });

        content.add(Box.createVerticalStrut(15));
        JPanel btnPanel = new JPanel();
        btnPanel.add(startBtn);
        content.add(btnPanel);

        add(content, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() { return confirmed; }
    public Mode getChosenMode() { return chosenMode; }
    public Difficulty getChosenDifficulty() { return chosenDifficulty; }
    public PlayerColor getChosenHumanColor() { return chosenHumanColor; }
}