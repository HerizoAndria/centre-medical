package com.centremedical.controller;

import com.centremedical.exception.ResourceNotFoundException;
import com.centremedical.model.Patient;
import com.centremedical.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public List<Patient> listerTous() {
        return patientRepository.findAll();
    }

    // Recherche des patients par code OU par nom : GET /api/patients/recherche?motCle=xxx
    @GetMapping("/recherche")
    public List<Patient> rechercher(@RequestParam String motCle) {
        return patientRepository.rechercherParCodeOuNom(motCle);
    }

    @GetMapping("/{codePat}")
    public Patient obtenirParCode(@PathVariable String codePat) {
        return patientRepository.findById(codePat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec le code : " + codePat));
    }

    @PostMapping
    public ResponseEntity<Patient> creer(@Valid @RequestBody Patient patient) {
        if (patient.getCodePat() == null || patient.getCodePat().isBlank()) {
            throw new IllegalArgumentException("Le code patient est obligatoire");
        }
        if (patientRepository.existsById(patient.getCodePat())) {
            throw new IllegalArgumentException("Un patient avec ce code existe deja : " + patient.getCodePat());
        }
        Patient sauvegarde = patientRepository.save(patient);
        return new ResponseEntity<>(sauvegarde, HttpStatus.CREATED);
    }

    @PutMapping("/{codePat}")
    public Patient modifier(@PathVariable String codePat, @Valid @RequestBody Patient details) {
        Patient patient = patientRepository.findById(codePat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec le code : " + codePat));
        patient.setNom(details.getNom());
        patient.setPrenom(details.getPrenom());
        patient.setSexe(details.getSexe());
        patient.setAdresse(details.getAdresse());
        return patientRepository.save(patient);
    }

    @DeleteMapping("/{codePat}")
    public ResponseEntity<Void> supprimer(@PathVariable String codePat) {
        Patient patient = patientRepository.findById(codePat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec le code : " + codePat));
        patientRepository.delete(patient);
        return ResponseEntity.noContent().build();
    }
}
