package com.centremedical.client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;

public final class AppTheme {
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(236, 242, 248);
    public static final Color BORDER = new Color(214, 224, 235);
    public static final Color TEXT = new Color(29, 39, 52);
    public static final Color MUTED = new Color(93, 111, 132);
    public static final Color PRIMARY = new Color(21, 101, 192);
    public static final Color PRIMARY_DARK = new Color(13, 71, 161);
    public static final Color SUCCESS = new Color(42, 142, 92);
    public static final Color WARNING = new Color(202, 122, 24);
    public static final Color DANGER = new Color(190, 58, 58);
    public static final Color INK = new Color(22, 32, 45);
    private static final String FONT_FAMILY = resolveFontFamily();

    private AppTheme() {
    }

    public static void install() {
        setGlobalFont(regular(12));
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.font", bold(12));
        UIManager.put("Label.font", regular(12));
        UIManager.put("TextField.font", regular(12));
        UIManager.put("TextArea.font", regular(12));
        UIManager.put("ComboBox.font", regular(12));
        UIManager.put("Table.font", regular(12));
        UIManager.put("TableHeader.font", bold(12));
        UIManager.put("TabbedPane.font", bold(12));
        UIManager.put("TitledBorder.font", bold(12));
        UIManager.put("OptionPane.messageFont", regular(12));
        UIManager.put("OptionPane.buttonFont", bold(12));
    }

    public static Font regular(float size) {
        return new Font(FONT_FAMILY, Font.PLAIN, Math.round(size));
    }

    public static Font bold(float size) {
        return new Font(FONT_FAMILY, Font.BOLD, Math.round(size));
    }

    private static void setGlobalFont(Font font) {
        FontUIResource resource = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, resource);
            }
        }
    }

    private static String resolveFontFamily() {
        List<String> available = List.of(GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        for (String candidate : List.of("Noto Sans", "Liberation Sans", "DejaVu Sans", "Dialog")) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }

    public static JPanel page(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.add(header(title, subtitle), BorderLayout.NORTH);
        return panel;
    }

    public static JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(8, 2));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(bold(24));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(regular(12));
        subtitleLabel.setForeground(MUTED);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(compoundBorder());
        return panel;
    }

    public static Border compoundBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        );
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(bold(14));
        label.setForeground(TEXT);
        AppIcon.Type iconType = iconForTitle(text);
        if (iconType != null) {
            label.setIcon(AppIcon.of(iconType, 16, MUTED));
            label.setIconTextGap(8);
        }
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = button(text, iconFor(text));
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    public static JButton button(String text) {
        return button(text, iconFor(text));
    }

    public static JButton button(String text, AppIcon.Type iconType) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setBackground(SURFACE);
        button.setForeground(TEXT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setIconTextGap(8);
        if (iconType != null) {
            button.setIcon(AppIcon.of(iconType));
        }
        if ("<".equals(text) || ">".equals(text)) {
            button.setText("");
            button.setPreferredSize(new Dimension(34, 34));
            button.setToolTipText("<".equals(text) ? "Page precedente" : "Page suivante");
        }
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = button(text, iconFor(text));
        button.setForeground(DANGER);
        return button;
    }

    private static AppIcon.Type iconFor(String text) {
        String normalized = text.toLowerCase();
        if (normalized.contains("ajouter")) {
            return AppIcon.Type.ADD;
        }
        if (normalized.contains("modifier")) {
            return AppIcon.Type.EDIT;
        }
        if (normalized.contains("supprimer")) {
            return AppIcon.Type.DELETE;
        }
        if (normalized.contains("nouveau") || normalized.contains("effacer")) {
            return AppIcon.Type.CLEAR;
        }
        if (normalized.contains("rafraichir")) {
            return AppIcon.Type.REFRESH;
        }
        if (normalized.contains("recharger")) {
            return AppIcon.Type.LIST;
        }
        if (normalized.contains("date")) {
            return AppIcon.Type.CALENDAR;
        }
        if ("<".equals(text)) {
            return AppIcon.Type.CHEVRON_LEFT;
        }
        if (">".equals(text)) {
            return AppIcon.Type.CHEVRON_RIGHT;
        }
        return null;
    }

    private static AppIcon.Type iconForTitle(String text) {
        String normalized = text.toLowerCase();
        if (normalized.contains("patient")) {
            return AppIcon.Type.PATIENT;
        }
        if (normalized.contains("medecin")) {
            return AppIcon.Type.DOCTOR;
        }
        if (normalized.contains("visite")) {
            return AppIcon.Type.VISIT;
        }
        if (normalized.contains("prochaine")) {
            return AppIcon.Type.CALENDAR;
        }
        return null;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(228, 235, 243));
        table.setSelectionBackground(new Color(220, 235, 252));
        table.setSelectionForeground(TEXT);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(SURFACE_ALT);
        header.setForeground(TEXT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        return scrollPane;
    }

    public static void styleField(JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    public static JLabel badge(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
        label.setForeground(color);
        label.setFont(bold(12));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return label;
    }
}
