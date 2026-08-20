package com.centremedical.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "code_pat", length = 20)
    private String codePat;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 60)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Column(nullable = false, length = 60)
    private String prenom;

    // 'M' ou 'F'
    @Column(length = 1)
    private String sexe;

    @Column(length = 150)
    private String adresse;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visite> visites;

    public Patient() {
    }

    public Patient(String codePat, String nom, String prenom, String sexe, String adresse) {
        this.codePat = codePat;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.adresse = adresse;
    }

    public String getCodePat() {
        return codePat;
    }

    public void setCodePat(String codePat) {
        this.codePat = codePat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}
