package com.centremedical.client.ui;

import com.centremedical.client.model.Medecin;
import com.centremedical.client.model.Patient;
import com.centremedical.client.model.Visite;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.MedecinService;
import com.centremedical.client.service.PatientService;
import com.centremedical.client.service.VisiteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final MedecinService medecinService = new MedecinService();
    private final VisiteService visiteService = new VisiteService();

    private final JLabel patientsKpi = new JLabel("-");
    private final JLabel medecinsKpi = new JLabel("-");
    private final JLabel visitesKpi = new JLabel("-");
    private final JLabel prochainesKpi = new JLabel("-");
    private final ChartPanel visitesParMois = new ChartPanel("Visites par mois");
    private final ChartPanel visitesParGrade = new ChartPanel("Activite par specialite");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Date", "Patient", "Medecin", "Specialite"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout());
        JPanel page = AppTheme.page("Tableau de bord", "Vue d'ensemble du centre medical et de l'activite des visites");

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(buildKpiGrid(), BorderLayout.NORTH);
        content.add(buildAnalytics(), BorderLayout.CENTER);
        page.add(content, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    private JPanel buildKpiGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 12, 0));
        grid.setOpaque(false);
        grid.add(kpiCard("Patients", patientsKpi, "Dossiers actifs", AppTheme.PRIMARY, AppIcon.Type.USERS));
        grid.add(kpiCard("Medecins", medecinsKpi, "Equipe disponible", AppTheme.SUCCESS, AppIcon.Type.DOCTOR));
        grid.add(kpiCard("Visites", visitesKpi, "Historique total", AppTheme.WARNING, AppIcon.Type.CHART));
        grid.add(kpiCard("A venir", prochainesKpi, "30 prochains jours", AppTheme.DANGER, AppIcon.Type.CLOCK));
        return grid;
    }

    private JPanel kpiCard(String title, JLabel value, String helper, Color accent, AppIcon.Type iconType) {
        JPanel card = AppTheme.card(new BorderLayout(0, 8));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AppTheme.MUTED);
        titleLabel.setFont(AppTheme.bold(12));

        JLabel icon = new JLabel(AppIcon.of(iconType, 20, accent));
        icon.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);
        top.add(icon, BorderLayout.EAST);

        value.setForeground(AppTheme.TEXT);
        value.setFont(AppTheme.bold(30));

        JLabel helperLabel = AppTheme.badge(helper, accent);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(helperLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildAnalytics() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 12);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.55;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        container.add(visitesParMois, c);

        c.gridx = 1;
        c.weightx = 0.45;
        container.add(visitesParGrade, c);

        JPanel tableCard = AppTheme.card(new BorderLayout(0, 12));
        tableCard.add(AppTheme.sectionTitle("Prochaines visites"), BorderLayout.NORTH);
        JTable table = new JTable(tableModel);
        AppTheme.styleTable(table);
        tableCard.add(AppTheme.tableScroll(table), BorderLayout.CENTER);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0.85;
        c.insets = new Insets(14, 0, 0, 0);
        container.add(tableCard, c);

        return container;
    }

    public void rafraichir() {
        try {
            List<Patient> patients = patientService.listerTous();
            List<Medecin> medecins = medecinService.listerTous();
            List<Visite> visites = visiteService.listerTous();

            patientsKpi.setText(String.valueOf(patients.size()));
            medecinsKpi.setText(String.valueOf(medecins.size()));
            visitesKpi.setText(String.valueOf(visites.size()));

            LocalDate today = LocalDate.now();
            long prochaines = visites.stream()
                    .filter(v -> v.getDate() != null)
                    .filter(v -> !v.getDate().isBefore(today) && !v.getDate().isAfter(today.plusDays(30)))
                    .count();
            prochainesKpi.setText(String.valueOf(prochaines));

            visitesParMois.setData(buildMonthlyData(visites));
            visitesParGrade.setData(buildGradeData(visites));
            fillUpcoming(visites);
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private Map<String, Integer> buildMonthlyData(List<Visite> visites) {
        Map<YearMonth, Long> grouped = visites.stream()
                .filter(v -> v.getDate() != null)
                .collect(Collectors.groupingBy(v -> YearMonth.from(v.getDate()), Collectors.counting()));

        List<YearMonth> months = new ArrayList<>();
        YearMonth current = YearMonth.now().minusMonths(5);
        for (int i = 0; i < 6; i++) {
            months.add(current.plusMonths(i));
        }

        Map<String, Integer> data = new LinkedHashMap<>();
        for (YearMonth month : months) {
            String label = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + month.getYear();
            data.put(label, grouped.getOrDefault(month, 0L).intValue());
        }
        return data;
    }

    private Map<String, Integer> buildGradeData(List<Visite> visites) {
        Map<String, Long> grouped = visites.stream()
                .filter(v -> v.getMedecin() != null)
                .collect(Collectors.groupingBy(v -> valueOr(v.getMedecin().getGrade(), "Non precise"), Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(6)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().intValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private void fillUpcoming(List<Visite> visites) {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        visites.stream()
                .filter(v -> v.getDate() != null && !v.getDate().isBefore(today))
                .sorted(Comparator.comparing(Visite::getDate))
                .limit(10)
                .forEach(v -> tableModel.addRow(new Object[]{
                        v.getDate(),
                        v.getPatient() != null ? fullName(v.getPatient().getNom(), v.getPatient().getPrenom()) : "",
                        v.getMedecin() != null ? fullName(v.getMedecin().getNom(), v.getMedecin().getPrenom()) : "",
                        v.getMedecin() != null ? valueOr(v.getMedecin().getGrade(), "Non precise") : ""
                }));
    }

    private String fullName(String nom, String prenom) {
        return valueOr(nom, "") + " " + valueOr(prenom, "");
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void afficherErreur(Exception ex) {
        String message = ex instanceof ApiClient.ApiException ? ex.getMessage() : "Erreur de connexion au serveur : " + ex.getMessage();
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static class ChartPanel extends JPanel {
        private final String title;
        private Map<String, Integer> data = new LinkedHashMap<>();

        ChartPanel(String title) {
            this.title = title;
            setBackground(AppTheme.SURFACE);
            setBorder(AppTheme.compoundBorder());
            setPreferredSize(new Dimension(360, 260));
        }

        void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 48;
            int right = 18;
            int top = 52;
            int bottom = 48;
            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            g2.setColor(AppTheme.TEXT);
            g2.setFont(AppTheme.bold(14));
            g2.drawString(title, 14, 24);

            g2.setColor(AppTheme.BORDER);
            g2.drawLine(left, top, left, top + chartHeight);
            g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight);

            if (data.isEmpty()) {
                g2.setColor(AppTheme.MUTED);
                g2.drawString("Aucune donnee", left + 12, top + 35);
                g2.dispose();
                return;
            }

            int max = Math.max(1, data.values().stream().mapToInt(Integer::intValue).max().orElse(1));
            int count = data.size();
            int gap = 12;
            int barWidth = Math.max(18, (chartWidth - (count + 1) * gap) / count);
            int x = left + gap;

            int i = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int value = entry.getValue();
                int barHeight = (int) Math.round((value / (double) max) * (chartHeight - 16));
                int y = top + chartHeight - barHeight;

                Color color = i % 2 == 0 ? AppTheme.PRIMARY : AppTheme.SUCCESS;
                g2.setColor(color);
                g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                g2.setColor(AppTheme.TEXT);
                g2.setFont(AppTheme.bold(11));
                g2.drawString(String.valueOf(value), x, Math.max(top + 12, y - 6));

                g2.setColor(AppTheme.MUTED);
                g2.setFont(AppTheme.regular(10));
                String label = entry.getKey();
                if (label.length() > 12) {
                    label = label.substring(0, 11) + ".";
                }
                g2.drawString(label, x - 2, top + chartHeight + 18);
                x += barWidth + gap;
                i++;
            }
            g2.dispose();
        }
    }
}
