package com.centremedical.client.model;

import java.time.LocalDate;

public class Visite {
    private Long id;
    private Medecin medecin;
    private Patient patient;
    private LocalDate date;

    public Visite() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
