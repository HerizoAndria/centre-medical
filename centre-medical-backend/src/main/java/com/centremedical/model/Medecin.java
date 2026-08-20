package com.centremedical.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "medecin")
public class Medecin {

    @Id
    @Column(name = "code_med", length = 20)
    private String codeMed;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 60)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Column(nullable = false, length = 60)
    private String prenom;

    @Column(length = 40)
    private String grade;

    @JsonIgnore
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visite> visites;

    public Medecin() {
    }

    public Medecin(String codeMed, String nom, String prenom, String grade) {
        this.codeMed = codeMed;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
    }

    public String getCodeMed() {
        return codeMed;
    }

    public void setCodeMed(String codeMed) {
        this.codeMed = codeMed;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
