package com.centremedical.client.model;

public class Medecin {
    private String codeMed;
    private String nom;
    private String prenom;
    private String grade;

    public Medecin() {
    }

    public Medecin(String codeMed, String nom, String prenom, String grade) {
        this.codeMed = codeMed;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
    }

    public String getCodeMed() { return codeMed; }
    public void setCodeMed(String codeMed) { this.codeMed = codeMed; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return codeMed + " - " + nom + " " + prenom;
    }
}
