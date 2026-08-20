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
import java.time.format.DateTimeFormatter;

public class VisitePanel extends JPanel {

    private final VisiteService visiteService = new VisiteService();
    private final MedecinService medecinService = new MedecinService();
    private final PatientService patientService = new PatientService();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Id", "Code Med", "Medecin", "Code Pat", "Patient", "Date"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Medecin> comboMedecin = new JComboBox<>();
    private final JComboBox<Patient> comboPatient = new JComboBox<>();
    private final JTextField champDate = new JTextField(10);
    private final JLabel labelIdSelectionne = new JLabel("Aucune visite selectionnee");

    private Long idSelectionne = null;

    public VisitePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireFormulaire(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> chargerSelection());

        chargerListesDeroulantes();
        rafraichir();
    }

    private JPanel construireFormulaire() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Visite"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Medecin :"), c);
        c.gridx = 1; form.add(comboMedecin, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Patient :"), c);
        c.gridx = 1; form.add(comboPatient, c);
        y++;
        c.gridx = 0; c.gridy = y; form.add(new JLabel("Date (AAAA-MM-JJ) :"), c);
        c.gridx = 1; form.add(champDate, c);
        y++;
        c.gridx = 0; c.gridy = y; c.gridwidth = 2; form.add(labelIdSelectionne, c);
        y++;

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnEffacer = new JButton("Nouveau / Effacer");
        JButton btnRafraichirListes = new JButton("Rafraichir medecins/patients");

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnEffacer);
        boutons.add(btnRafraichirListes);

        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        form.add(boutons, c);

        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnEffacer.addActionListener(e -> effacerFormulaire());
        btnRafraichirListes.addActionListener(e -> chargerListesDeroulantes());

        return form;
    }

    private void chargerListesDeroulantes() {
        try {
            comboMedecin.removeAllItems();
            for (Medecin m : medecinService.listerTous()) comboMedecin.addItem(m);

            comboPatient.removeAllItems();
            for (Patient p : patientService.listerTous()) comboPatient.addItem(p);
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void chargerSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idSelectionne = Long.valueOf(tableModel.getValueAt(row, 0).toString());
        String codeMed = tableModel.getValueAt(row, 1).toString();
        String codePat = tableModel.getValueAt(row, 3).toString();
        champDate.setText(tableModel.getValueAt(row, 5).toString());
        selectionnerDansCombo(comboMedecin, codeMed);
        selectionnerDansCombo(comboPatient, codePat);
        labelIdSelectionne.setText("Visite selectionnee : id " + idSelectionne);
    }

    private void selectionnerDansCombo(JComboBox<?> combo, String code) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            String itemCode = (item instanceof Medecin) ? ((Medecin) item).getCodeMed() : ((Patient) item).getCodePat();
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
        if (comboMedecin.getItemCount() > 0) comboMedecin.setSelectedIndex(0);
        if (comboPatient.getItemCount() > 0) comboPatient.setSelectedIndex(0);
    }

    public void rafraichir() {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
            tableModel.setRowCount(0);
            for (Visite v : visiteService.listerTous()) {
                tableModel.addRow(new Object[]{
                        v.getId(),
                        v.getMedecin() != null ? v.getMedecin().getCodeMed() : "",
                        v.getMedecin() != null ? v.getMedecin().getNom() + " " + v.getMedecin().getPrenom() : "",
                        v.getPatient() != null ? v.getPatient().getCodePat() : "",
                        v.getPatient() != null ? v.getPatient().getNom() + " " + v.getPatient().getPrenom() : "",
                        v.getDate() != null ? v.getDate().format(fmt) : ""
                });
            }
        } catch (Exception ex) {
            afficherErreur(ex);
        }
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
        if (!validerFormulaire()) return;
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
        if (!validerFormulaire()) return;
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
        if (confirmation != JOptionPane.YES_OPTION) return;
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
