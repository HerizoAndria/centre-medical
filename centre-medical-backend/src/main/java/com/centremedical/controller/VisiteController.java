package com.centremedical.controller;

import com.centremedical.exception.ResourceNotFoundException;
import com.centremedical.model.Medecin;
import com.centremedical.model.Patient;
import com.centremedical.model.Visite;
import com.centremedical.repository.MedecinRepository;
import com.centremedical.repository.PatientRepository;
import com.centremedical.repository.VisiteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visites")
public class VisiteController {

    private final VisiteRepository visiteRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;

    public VisiteController(VisiteRepository visiteRepository,
                             MedecinRepository medecinRepository,
                             PatientRepository patientRepository) {
        this.visiteRepository = visiteRepository;
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public List<Visite> listerTous() {
        return visiteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Visite obtenirParId(@PathVariable Long id) {
        return visiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visite introuvable avec l'id : " + id));
    }

    // Corps attendu : { "codeMed": "M001", "codePat": "P001", "date": "2026-08-18" }
    @PostMapping
    public ResponseEntity<Visite> creer(@RequestBody Map<String, String> requete) {
        Visite visite = construireVisiteDepuisRequete(requete, new Visite());
        Visite sauvegarde = visiteRepository.save(visite);
        return new ResponseEntity<>(sauvegarde, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Visite modifier(@PathVariable Long id, @RequestBody Map<String, String> requete) {
        Visite visite = visiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visite introuvable avec l'id : " + id));
        visite = construireVisiteDepuisRequete(requete, visite);
        return visiteRepository.save(visite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        Visite visite = visiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visite introuvable avec l'id : " + id));
        visiteRepository.delete(visite);
        return ResponseEntity.noContent().build();
    }

    // Visites d'un patient donne : GET /api/visites/patient/{codePat}
    @GetMapping("/patient/{codePat}")
    public List<Visite> parPatient(@PathVariable String codePat) {
        return visiteRepository.findByPatient_CodePat(codePat);
    }

    // Visites d'un medecin donne : GET /api/visites/medecin/{codeMed}
    @GetMapping("/medecin/{codeMed}")
    public List<Visite> parMedecin(@PathVariable String codeMed) {
        return visiteRepository.findByMedecin_CodeMed(codeMed);
    }

    private Visite construireVisiteDepuisRequete(Map<String, String> requete, Visite visite) {
        String codeMed = requete.get("codeMed");
        String codePat = requete.get("codePat");
        String dateStr = requete.get("date");

        if (codeMed == null || codePat == null || dateStr == null) {
            throw new IllegalArgumentException("codeMed, codePat et date sont obligatoires");
        }

        Medecin medecin = medecinRepository.findById(codeMed)
                .orElseThrow(() -> new ResourceNotFoundException("Medecin introuvable avec le code : " + codeMed));
        Patient patient = patientRepository.findById(codePat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec le code : " + codePat));

        visite.setMedecin(medecin);
        visite.setPatient(patient);
        visite.setDate(java.time.LocalDate.parse(dateStr));
        return visite;
    }
}
