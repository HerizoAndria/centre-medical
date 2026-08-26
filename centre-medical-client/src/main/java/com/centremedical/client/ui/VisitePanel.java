package com.centremedical.client.ui;

import com.centremedical.client.model.Medecin;
import com.centremedical.client.model.Patient;
import com.centremedical.client.model.Visite;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.MedecinService;
import com.centremedical.client.service.PatientService;
import com.centremedical.client.service.VisiteService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class VisitePanel extends JPanel {
    private final VisiteService visiteService = new VisiteService();
    private final MedecinService medecinService = new MedecinService();
    private final PatientService patientService = new PatientService();
    private final List<Visite> toutesLesVisites = new ArrayList<>();
    private final List<Visite> visitesFiltrees = new ArrayList<>();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Id", "Date", "Code Med", "Medecin", "Code Pat", "Patient", "Specialite"}, 0) {
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

    private final JComboBox<Medecin> comboMedecin = new JComboBox<>();
    private final JComboBox<Patient> comboPatient = new JComboBox<>();
    private final JTextField champDate = new JTextField();
    private final JLabel labelIdSelectionne = AppTheme.badge("Aucune visite selectionnee", AppTheme.MUTED);

    private Long idSelectionne = null;
    private int pageCourante = 1;

    public VisitePanel() {
        setLayout(new BorderLayout());
        JPanel page = AppTheme.page("Visites", "Planification et suivi des consultations");
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
        champRecherche.setToolTipText("Date, medecin, patient, code ou specialite");
        AppTheme.styleField(champRecherche);
        search.add(champRecherche, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnRafraichir = AppTheme.button("Rafraichir");
        JButton btnListes = AppTheme.button("Recharger listes");
        btnRafraichir.addActionListener(e -> rafraichir());
        btnListes.addActionListener(e -> chargerListesDeroulantes());
        actions.add(btnListes);
        actions.add(btnRafraichir);

        toolbar.add(search, BorderLayout.CENTER);
        toolbar.add(actions, BorderLayout.EAST);

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
        form.add(AppTheme.sectionTitle("Fiche visite"), c);
        addField(form, c, "Medecin", comboMedecin, 1);
        addField(form, c, "Patient", comboPatient, 3);
        addField(form, c, "Date (AAAA-MM-JJ)", champDate, 5);

        JButton aujourdHui = AppTheme.button("Date du jour");
        aujourdHui.addActionListener(e -> champDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        c.gridy = 7;
        c.insets = new Insets(8, 0, 8, 0);
        form.add(aujourdHui, c);

        c.gridy = 8;
        c.insets = new Insets(2, 0, 12, 0);
        form.add(labelIdSelectionne, c);

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
        c.insets = new Insets(0, 0, 0, 0);
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

    private void chargerListesDeroulantes() {
        try {
            Object selectedMedecin = comboMedecin.getSelectedItem();
            Object selectedPatient = comboPatient.getSelectedItem();

            comboMedecin.removeAllItems();
            for (Medecin m : medecinService.listerTous()) {
                comboMedecin.addItem(m);
            }
            comboPatient.removeAllItems();
            for (Patient p : patientService.listerTous()) {
                comboPatient.addItem(p);
            }

            restaurerSelection(comboMedecin, selectedMedecin);
            restaurerSelection(comboPatient, selectedPatient);
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void restaurerSelection(JComboBox<?> combo, Object selection) {
        if (selection == null) {
            return;
        }
        String code = selection instanceof Medecin medecin ? medecin.getCodeMed() : ((Patient) selection).getCodePat();
        selectionnerDansCombo(combo, code);
    }

    private void chargerSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        idSelectionne = Long.valueOf(tableModel.getValueAt(row, 0).toString());
        String codeMed = tableModel.getValueAt(row, 2).toString();
        String codePat = tableModel.getValueAt(row, 4).toString();
        champDate.setText(tableModel.getValueAt(row, 1).toString());
        selectionnerDansCombo(comboMedecin, codeMed);
        selectionnerDansCombo(comboPatient, codePat);
        labelIdSelectionne.setText("Visite selectionnee : id " + idSelectionne);
    }

    private void selectionnerDansCombo(JComboBox<?> combo, String code) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            String itemCode = item instanceof Medecin medecin ? medecin.getCodeMed() : ((Patient) item).getCodePat();
            if (itemCode.equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void effacerFormulaire() {
        table.clearSelection();
        idSelectionne = null;
        champDate.setText("");
        labelIdSelectionne.setText("Aucune visite selectionnee");
        if (comboMedecin.getItemCount() > 0) {
            comboMedecin.setSelectedIndex(0);
        }
        if (comboPatient.getItemCount() > 0) {
            comboPatient.setSelectedIndex(0);
        }
    }

    public void rafraichir() {
        try {
            chargerListesDeroulantes();
            toutesLesVisites.clear();
            toutesLesVisites.addAll(visiteService.listerTous());
            toutesLesVisites.sort(Comparator.comparing(Visite::getDate, Comparator.nullsLast(Comparator.reverseOrder())));
            filtrer();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void filtrer() {
        String motCle = champRecherche.getText().trim().toLowerCase(Locale.ROOT);
        visitesFiltrees.clear();
        for (Visite v : toutesLesVisites) {
            if (motCle.isEmpty() || contient(v, motCle)) {
                visitesFiltrees.add(v);
            }
        }
        pageCourante = 1;
        remplirPage();
    }

    private boolean contient(Visite visite, String motCle) {
        return texte(String.valueOf(visite.getId())).contains(motCle)
                || texte(visite.getDate() != null ? visite.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "").contains(motCle)
                || texte(visite.getMedecin() != null ? visite.getMedecin().getCodeMed() : "").contains(motCle)
                || texte(visite.getMedecin() != null ? visite.getMedecin().getNom() : "").contains(motCle)
                || texte(visite.getMedecin() != null ? visite.getMedecin().getPrenom() : "").contains(motCle)
                || texte(visite.getMedecin() != null ? visite.getMedecin().getGrade() : "").contains(motCle)
                || texte(visite.getPatient() != null ? visite.getPatient().getCodePat() : "").contains(motCle)
                || texte(visite.getPatient() != null ? visite.getPatient().getNom() : "").contains(motCle)
                || texte(visite.getPatient() != null ? visite.getPatient().getPrenom() : "").contains(motCle);
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
        int end = Math.min(start + size, visitesFiltrees.size());
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        for (int i = start; i < end; i++) {
            Visite v = visitesFiltrees.get(i);
            Medecin medecin = v.getMedecin();
            Patient patient = v.getPatient();
            tableModel.addRow(new Object[]{
                    v.getId(),
                    v.getDate() != null ? v.getDate().format(fmt) : "",
                    medecin != null ? medecin.getCodeMed() : "",
                    medecin != null ? nomComplet(medecin.getNom(), medecin.getPrenom()) : "",
                    patient != null ? patient.getCodePat() : "",
                    patient != null ? nomComplet(patient.getNom(), patient.getPrenom()) : "",
                    medecin != null ? medecin.getGrade() : ""
            });
        }
        labelPage.setText("Page " + pageCourante + " / " + totalPages);
        labelResultats.setText(visitesFiltrees.size() + " visite(s)");
    }

    private int totalPages() {
        int size = (Integer) taillePage.getSelectedItem();
        return Math.max(1, (int) Math.ceil(visitesFiltrees.size() / (double) size));
    }

    private String nomComplet(String nom, String prenom) {
        return (nom == null ? "" : nom) + " " + (prenom == null ? "" : prenom);
    }

    private boolean validerFormulaire() {
        if (comboMedecin.getSelectedItem() == null || comboPatient.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez un medecin et un patient.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(champDate.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ.", "Date invalide", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void ajouter() {
        if (!validerFormulaire()) {
            return;
        }
        try {
            String codeMed = ((Medecin) comboMedecin.getSelectedItem()).getCodeMed();
            String codePat = ((Patient) comboPatient.getSelectedItem()).getCodePat();
            visiteService.creer(codeMed, codePat, champDate.getText().trim());
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void modifier() {
        if (idSelectionne == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez une visite a modifier.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validerFormulaire()) {
            return;
        }
        try {
            String codeMed = ((Medecin) comboMedecin.getSelectedItem()).getCodeMed();
            String codePat = ((Patient) comboPatient.getSelectedItem()).getCodePat();
            visiteService.modifier(idSelectionne, codeMed, codePat, champDate.getText().trim());
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (idSelectionne == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez une visite a supprimer.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer la visite id " + idSelectionne + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            visiteService.supprimer(idSelectionne);
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
