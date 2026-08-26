package com.centremedical.client.ui;

import com.centremedical.client.model.Medecin;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.MedecinService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MedecinPanel extends JPanel {
    private final MedecinService service = new MedecinService();
    private final List<Medecin> tousLesMedecins = new ArrayList<>();
    private final List<Medecin> medecinsFiltres = new ArrayList<>();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Code", "Nom", "Prenom", "Specialite"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
    private final JTable table = new JTable(tableModel);

    private final JTextField champRecherche = new JTextField();
    private final JComboBox<Integer> taillePage = new JComboBox<>(new Integer[]{10, 25, 50, 100});
    private final JLabel labelPage = new JLabel();
    private final JLabel labelResultats = new JLabel();

    private final JTextField champCode = new JTextField();
    private final JTextField champNom = new JTextField();
    private final JTextField champPrenom = new JTextField();
    private final JTextField champGrade = new JTextField();

    private int pageCourante = 1;

    public MedecinPanel() {
        setLayout(new BorderLayout());
        JPanel page = AppTheme.page("Medecins", "Equipe medicale, specialites et affectations");
        page.add(buildContent(), BorderLayout.CENTER);
        add(page, BorderLayout.CENTER);

        AppTheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerSelection();
            }
        });
    }

    private JComponent buildContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildTableCard(), buildFormCard());
        splitPane.setResizeWeight(0.72);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        return splitPane;
    }

    private JPanel buildTableCard() {
        JPanel card = AppTheme.card(new BorderLayout(0, 12));
        card.add(buildToolbar(), BorderLayout.NORTH);
        card.add(AppTheme.tableScroll(table), BorderLayout.CENTER);
        card.add(buildPagination(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        JLabel label = new JLabel("Recherche");
        label.setForeground(AppTheme.MUTED);
        label.setIcon(AppIcon.of(AppIcon.Type.SEARCH, 14, AppTheme.MUTED));
        label.setIconTextGap(6);
        search.add(label, BorderLayout.WEST);
        champRecherche.setToolTipText("Code, nom, prenom ou specialite");
        AppTheme.styleField(champRecherche);
        search.add(champRecherche, BorderLayout.CENTER);

        JButton btnRafraichir = AppTheme.button("Rafraichir");
        btnRafraichir.addActionListener(e -> rafraichir());

        toolbar.add(search, BorderLayout.CENTER);
        toolbar.add(btnRafraichir, BorderLayout.EAST);

        champRecherche.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrer();
            }
        });
        return toolbar;
    }

    private JPanel buildPagination() {
        JPanel pagination = new JPanel(new BorderLayout(12, 0));
        pagination.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        labelResultats.setForeground(AppTheme.MUTED);
        left.add(labelResultats);
        left.add(new JLabel("Lignes/page"));
        left.add(taillePage);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton precedent = AppTheme.button("<");
        JButton suivant = AppTheme.button(">");
        precedent.addActionListener(e -> {
            if (pageCourante > 1) {
                pageCourante--;
                remplirPage();
            }
        });
        suivant.addActionListener(e -> {
            if (pageCourante < totalPages()) {
                pageCourante++;
                remplirPage();
            }
        });
        labelPage.setForeground(AppTheme.MUTED);
        right.add(precedent);
        right.add(labelPage);
        right.add(suivant);

        taillePage.addActionListener(e -> {
            pageCourante = 1;
            remplirPage();
        });

        pagination.add(left, BorderLayout.WEST);
        pagination.add(right, BorderLayout.EAST);
        return pagination;
    }

    private JPanel buildFormCard() {
        JPanel form = AppTheme.card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 10, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;

        c.gridy = 0;
        form.add(AppTheme.sectionTitle("Fiche medecin"), c);
        addField(form, c, "Code medecin", champCode, 1);
        addField(form, c, "Nom", champNom, 3);
        addField(form, c, "Prenom", champPrenom, 5);
        addField(form, c, "Specialite / grade", champGrade, 7);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        buttons.setOpaque(false);
        JButton btnAjouter = AppTheme.primaryButton("Ajouter");
        JButton btnModifier = AppTheme.button("Modifier");
        JButton btnSupprimer = AppTheme.dangerButton("Supprimer");
        JButton btnEffacer = AppTheme.button("Nouveau / Effacer");
        buttons.add(btnAjouter);
        buttons.add(btnModifier);
        buttons.add(btnSupprimer);
        buttons.add(btnEffacer);

        c.gridy = 9;
        c.insets = new Insets(6, 0, 0, 0);
        form.add(buttons, c);

        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnEffacer.addActionListener(e -> effacerFormulaire());
        return form;
    }

    private void addField(JPanel form, GridBagConstraints c, String label, JComponent field, int y) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setForeground(AppTheme.MUTED);
        c.gridy = y;
        c.insets = new Insets(8, 0, 4, 0);
        form.add(fieldLabel, c);
        c.gridy = y + 1;
        c.insets = new Insets(0, 0, 2, 0);
        AppTheme.styleField(field);
        form.add(field, c);
    }

    private void effacerFormulaire() {
        table.clearSelection();
        champCode.setText("");
        champCode.setEditable(true);
        champNom.setText("");
        champPrenom.setText("");
        champGrade.setText("");
    }

    public void rafraichir() {
        try {
            tousLesMedecins.clear();
            tousLesMedecins.addAll(service.listerTous());
            filtrer();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void filtrer() {
        String motCle = champRecherche.getText().trim().toLowerCase(Locale.ROOT);
        medecinsFiltres.clear();
        for (Medecin m : tousLesMedecins) {
            if (motCle.isEmpty() || contient(m, motCle)) {
                medecinsFiltres.add(m);
            }
        }
        pageCourante = 1;
        remplirPage();
    }

    private boolean contient(Medecin medecin, String motCle) {
        return texte(medecin.getCodeMed()).contains(motCle)
                || texte(medecin.getNom()).contains(motCle)
                || texte(medecin.getPrenom()).contains(motCle)
                || texte(medecin.getGrade()).contains(motCle);
    }

    private String texte(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private void remplirPage() {
        tableModel.setRowCount(0);
        int size = (Integer) taillePage.getSelectedItem();
        int totalPages = totalPages();
        if (pageCourante > totalPages) {
            pageCourante = totalPages;
        }
        int start = (pageCourante - 1) * size;
        int end = Math.min(start + size, medecinsFiltres.size());
        for (int i = start; i < end; i++) {
            Medecin m = medecinsFiltres.get(i);
            tableModel.addRow(new Object[]{m.getCodeMed(), m.getNom(), m.getPrenom(), m.getGrade()});
        }
        labelPage.setText("Page " + pageCourante + " / " + totalPages);
        labelResultats.setText(medecinsFiltres.size() + " medecin(s)");
    }

    private int totalPages() {
        int size = (Integer) taillePage.getSelectedItem();
        return Math.max(1, (int) Math.ceil(medecinsFiltres.size() / (double) size));
    }

    private void chargerSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        champCode.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        champCode.setEditable(false);
        champNom.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        champPrenom.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        champGrade.setText(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void ajouter() {
        if (champCode.getText().isBlank() || champNom.getText().isBlank() || champPrenom.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Code, nom et prenom sont obligatoires.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Medecin m = new Medecin(champCode.getText().trim(), champNom.getText().trim(),
                    champPrenom.getText().trim(), champGrade.getText().trim());
            service.creer(m);
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void modifier() {
        if (champCode.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selectionnez un medecin a modifier.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Medecin m = new Medecin(champCode.getText().trim(), champNom.getText().trim(),
                    champPrenom.getText().trim(), champGrade.getText().trim());
            service.modifier(champCode.getText().trim(), m);
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (champCode.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selectionnez un medecin a supprimer.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer le medecin " + champCode.getText() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.supprimer(champCode.getText().trim());
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void afficherErreur(Exception ex) {
        String message = ex instanceof ApiClient.ApiException ? ex.getMessage() : "Erreur de connexion au serveur : " + ex.getMessage();
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
