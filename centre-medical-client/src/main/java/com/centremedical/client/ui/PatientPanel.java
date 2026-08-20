package com.centremedical.client.ui;

import com.centremedical.client.model.Patient;
import com.centremedical.client.service.ApiClient;
import com.centremedical.client.service.PatientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientService service = new PatientService();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Code", "Nom", "Prenom", "Sexe", "Adresse"}, 0) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    private final JTextField champRecherche = new JTextField(20);

    private final JTextField champCode = new JTextField(10);
    private final JTextField champNom = new JTextField(15);
    private final JTextField champPrenom = new JTextField(15);
    private final JComboBox<String> champSexe = new JComboBox<>(new String[]{"M", "F"});
    private final JTextField champAdresse = new JTextField(20);

    public PatientPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(construireBarreRecherche(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construireFormulaire(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                champCode.setText(tableModel.getValueAt(row, 0).toString());
                champCode.setEditable(false);
                champNom.setText(tableModel.getValueAt(row, 1).toString());
                champPrenom.setText(tableModel.getValueAt(row, 2).toString());
                champSexe.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 3)));
                champAdresse.setText(String.valueOf(tableModel.getValueAt(row, 4)));
            }
        });

        rafraichir();
    }

    private JPanel construireBarreRecherche() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Recherche par code ou nom"));
        JButton btnRechercher = new JButton("Rechercher");
        JButton btnTout = new JButton("Afficher tous");
        panel.add(champRecherche);
        panel.add(btnRechercher);
        panel.add(btnTout);

        btnRechercher.addActionListener(e -> rechercher());
        btnTout.addActionListener(e -> rafraichir());
        champRecherche.addActionListener(e -> rechercher());

        return panel;
    }

    private JPanel construireFormulaire() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Patient"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        ajouterChamp(form, c, "Code patient :", champCode, y++);
        ajouterChamp(form, c, "Nom :", champNom, y++);
        ajouterChamp(form, c, "Prenom :", champPrenom, y++);
        ajouterChamp(form, c, "Sexe :", champSexe, y++);
        ajouterChamp(form, c, "Adresse :", champAdresse, y++);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnEffacer = new JButton("Nouveau / Effacer");

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnEffacer);

        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        form.add(boutons, c);

        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnEffacer.addActionListener(e -> effacerFormulaire());

        return form;
    }

    private void ajouterChamp(JPanel form, GridBagConstraints c, String label, JComponent champ, int y) {
        c.gridx = 0; c.gridy = y; c.gridwidth = 1;
        form.add(new JLabel(label), c);
        c.gridx = 1;
        form.add(champ, c);
    }

    private void effacerFormulaire() {
        table.clearSelection();
        champCode.setText("");
        champCode.setEditable(true);
        champNom.setText("");
        champPrenom.setText("");
        champSexe.setSelectedIndex(0);
        champAdresse.setText("");
    }

    public void rafraichir() {
        try {
            List<Patient> liste = service.listerTous();
            remplirTable(liste);
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void rechercher() {
        String motCle = champRecherche.getText().trim();
        if (motCle.isEmpty()) {
            rafraichir();
            return;
        }
        try {
            List<Patient> liste = service.rechercher(motCle);
            remplirTable(liste);
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void remplirTable(List<Patient> liste) {
        tableModel.setRowCount(0);
        for (Patient p : liste) {
            tableModel.addRow(new Object[]{p.getCodePat(), p.getNom(), p.getPrenom(), p.getSexe(), p.getAdresse()});
        }
    }

    private void ajouter() {
        if (champCode.getText().isBlank() || champNom.getText().isBlank() || champPrenom.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Code, nom et prenom sont obligatoires.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Patient p = new Patient(champCode.getText().trim(), champNom.getText().trim(), champPrenom.getText().trim(),
                    (String) champSexe.getSelectedItem(), champAdresse.getText().trim());
            service.creer(p);
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void modifier() {
        if (champCode.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selectionnez un patient a modifier.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Patient p = new Patient(champCode.getText().trim(), champNom.getText().trim(), champPrenom.getText().trim(),
                    (String) champSexe.getSelectedItem(), champAdresse.getText().trim());
            service.modifier(champCode.getText().trim(), p);
            effacerFormulaire();
            rafraichir();
        } catch (Exception ex) {
            afficherErreur(ex);
        }
    }

    private void supprimer() {
        if (champCode.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selectionnez un patient a supprimer.", "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer le patient " + champCode.getText() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;
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
