package com.centremedical.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "visiter",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code_med", "code_pat", "date_visite"}))
public class Visite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le medecin est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_med", nullable = false)
    private Medecin medecin;

    @NotNull(message = "Le patient est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_pat", nullable = false)
    private Patient patient;

    @NotNull(message = "La date est obligatoire")
    @Column(name = "date_visite", nullable = false)
    private LocalDate date;

    public Visite() {
    }

    public Visite(Medecin medecin, Patient patient, LocalDate date) {
        this.medecin = medecin;
        this.patient = patient;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
