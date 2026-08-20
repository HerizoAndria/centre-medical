package com.centremedical.client.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Gestion des visites - Centre Medical");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        JTabbedPane onglets = new JTabbedPane();

        MedecinPanel medecinPanel = new MedecinPanel();
        PatientPanel patientPanel = new PatientPanel();
        VisitePanel visitePanel = new VisitePanel();

        onglets.addTab("Medecins", medecinPanel);
        onglets.addTab("Patients", patientPanel);
        onglets.addTab("Visites", visitePanel);

        // Rafraichir les donnees pertinentes a chaque changement d'onglet
        onglets.addChangeListener(e -> {
            int index = onglets.getSelectedIndex();
            switch (index) {
                case 0 -> medecinPanel.rafraichir();
                case 1 -> patientPanel.rafraichir();
                case 2 -> visitePanel.rafraichir();
                default -> {}
            }
        });

        setLayout(new BorderLayout());
        add(onglets, BorderLayout.CENTER);

        JLabel statut = new JLabel("  Connecte a l'API : http://localhost:8080/api");
        statut.setFont(statut.getFont().deriveFont(Font.PLAIN, 11f));
        add(statut, BorderLayout.SOUTH);
    }
}
