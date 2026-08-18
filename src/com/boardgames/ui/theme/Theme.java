package com.boardgames.ui.theme;

import javax.swing.*;
import java.awt.*;

public final class Theme {
    private Theme() {}

    public static final Color BG_PRIMARY = new Color(0x1B1D23);
    public static final Color BG_SECONDARY = new Color(0x242730);
    public static final Color BG_CARD = new Color(0x2B2F3A);
    public static final Color BG_CARD_HOVER = new Color(0x343A48);

    public static final Color ACCENT = new Color(0xE0A83E);
    public static final Color ACCENT_HOVER = new Color(0xF0BE5C);
    public static final Color ACCENT_PRESSED = new Color(0xC48F2C);

    public static final Color SECONDARY_BTN = new Color(0x3A3F4C);
    public static final Color SECONDARY_BTN_HOVER = new Color(0x474D5C);

    public static final Color SUCCESS = new Color(0x54B36A);
    public static final Color DANGER = new Color(0xE0625A);

    public static final Color TEXT_PRIMARY = new Color(0xF3F3F1);
    public static final Color TEXT_SECONDARY = new Color(0xA7ACB8);
    public static final Color TEXT_ON_ACCENT = new Color(0x1B1D23);

    private static final String FAMILY = "Segoe UI";
    private static final String SYMBOL_FAMILY = "Serif";

    public static final Font FONT_TITLE = new Font(FAMILY, Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SECTION = new Font(FAMILY, Font.BOLD, 12);
    public static final Font FONT_BODY = new Font(FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font(FAMILY, Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font(FAMILY, Font.BOLD, 14);
    public static final Font FONT_STATUS = new Font(FAMILY, Font.BOLD, 17);
    public static final Font FONT_SCORE = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font FONT_CARD_TITLE = new Font(FAMILY, Font.BOLD, 18);

    public static final Font FONT_SYMBOL_LARGE = new Font(SYMBOL_FAMILY, Font.PLAIN, 34);
    public static final Font FONT_SYMBOL_MEDIUM = new Font(SYMBOL_FAMILY, Font.PLAIN, 24);

    public static javax.swing.border.TitledBorder sectionBorder(String title) {
        return javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(0x3D4250), 1, true),
                title, javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                FONT_SECTION, TEXT_SECONDARY);
    }

    public static void styleRadio(JRadioButton rb) {
        rb.setFont(FONT_BODY);
        rb.setForeground(TEXT_PRIMARY);
        rb.setBackground(BG_SECONDARY);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static <T> void styleCombo(JComboBox<T> box) {
        box.setFont(FONT_BODY);
        box.setBackground(BG_CARD);
        box.setForeground(TEXT_PRIMARY);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setFont(FONT_BODY);
                c.setForeground(TEXT_PRIMARY);
                c.setBackground(isSelected ? ACCENT.darker() : BG_CARD);
                return c;
            }
        });
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
        label.setFont(FONT_BODY);
    }

    public static JPanel iconTitle(String symbol, String text, Font textFont, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);
        JLabel iconLbl = new JLabel(symbol);
        iconLbl.setFont(FONT_SYMBOL_MEDIUM);
        iconLbl.setForeground(color);
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(textFont);
        textLbl.setForeground(color);
        p.add(iconLbl);
        p.add(textLbl);
        return p;
    }
    public static void installLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        UIManager.put("control", BG_SECONDARY);
        UIManager.put("info", BG_CARD);
        UIManager.put("nimbusBase", BG_PRIMARY);
        UIManager.put("nimbusBlueGrey", BG_SECONDARY);
        UIManager.put("nimbusLightBackground", BG_CARD);
        UIManager.put("nimbusSelectionBackground", ACCENT);
        UIManager.put("nimbusSelectedText", TEXT_ON_ACCENT);
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("nimbusDisabledText", TEXT_SECONDARY);
        UIManager.put("text", TEXT_PRIMARY);
        UIManager.put("textForeground", TEXT_PRIMARY);
        UIManager.put("textBackground", BG_CARD);
        UIManager.put("ComboBox.background", BG_CARD);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", TEXT_ON_ACCENT);
        UIManager.put("ComboBox.buttonBackground", BG_CARD);
        UIManager.put("List.background", BG_CARD);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", ACCENT);
        UIManager.put("List.selectionForeground", TEXT_ON_ACCENT);
        UIManager.put("OptionPane.background", BG_SECONDARY);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Panel.background", BG_PRIMARY);
    }
}