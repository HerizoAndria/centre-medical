package com.centremedical.client.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel statut = new JLabel();
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();

    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final MedecinPanel medecinPanel = new MedecinPanel();
    private final PatientPanel patientPanel = new PatientPanel();
    private final VisitePanel visitePanel = new VisitePanel();

    public MainFrame() {
        super("Centre Medical - Console desktop");
        AppTheme.install();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1280, 820);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
        showScreen("dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 18));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(AppTheme.INK);
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 18, 16));

        JPanel brand = new JPanel(new BorderLayout(0, 6));
        brand.setOpaque(false);
        JLabel title = new JLabel("Centre Medical");
        title.setForeground(Color.WHITE);
        title.setFont(AppTheme.bold(22));
        JLabel subtitle = new JLabel("Gestion des visites");
        subtitle.setForeground(new Color(185, 199, 216));
        subtitle.setFont(AppTheme.regular(12));
        brand.add(title, BorderLayout.NORTH);
        brand.add(subtitle, BorderLayout.SOUTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 8));
        nav.setOpaque(false);
        addNavButton(nav, "dashboard", "Tableau de bord", AppIcon.Type.DASHBOARD);
        addNavButton(nav, "medecins", "Medecins", AppIcon.Type.DOCTOR);
        addNavButton(nav, "patients", "Patients", AppIcon.Type.PATIENT);
        addNavButton(nav, "visites", "Visites", AppIcon.Type.VISIT);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(AppTheme.BACKGROUND);
        main.add(buildTopBar(), BorderLayout.NORTH);

        content.setBackground(AppTheme.BACKGROUND);
        content.add(dashboardPanel, "dashboard");
        content.add(medecinPanel, "medecins");
        content.add(patientPanel, "patients");
        content.add(visitePanel, "visites");
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JLabel title = new JLabel("Console professionnelle", AppIcon.of(AppIcon.Type.DASHBOARD, 16, AppTheme.TEXT), SwingConstants.LEFT);
        title.setFont(AppTheme.bold(15));
        title.setForeground(AppTheme.TEXT);
        title.setIconTextGap(8);

        statut.setFont(AppTheme.regular(12));
        statut.setForeground(AppTheme.MUTED);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(statut, BorderLayout.EAST);
        return topBar;
    }

    private void addNavButton(JPanel nav, String screen, String label, AppIcon.Type iconType) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        button.setBackground(AppTheme.INK);
        button.setForeground(new Color(216, 226, 239));
        button.setFont(AppTheme.bold(13));
        button.setIcon(AppIcon.of(iconType));
        button.setIconTextGap(10);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> showScreen(screen));
        navigationButtons.put(screen, button);
        nav.add(button);
    }

    private void showScreen(String screen) {
        cards.show(content, screen);
        navigationButtons.forEach((key, button) -> {
            boolean selected = key.equals(screen);
            button.setBackground(selected ? AppTheme.PRIMARY : AppTheme.INK);
            button.setForeground(Color.WHITE);
        });
        switch (screen) {
            case "dashboard" -> dashboardPanel.rafraichir();
            case "medecins" -> medecinPanel.rafraichir();
            case "patients" -> patientPanel.rafraichir();
            case "visites" -> visitePanel.rafraichir();
            default -> {
            }
        }
        statut.setText("Derniere synchronisation : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }
}
