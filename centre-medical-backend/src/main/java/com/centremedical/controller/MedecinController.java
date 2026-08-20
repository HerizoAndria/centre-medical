package com.centremedical.controller;

import com.centremedical.exception.ResourceNotFoundException;
import com.centremedical.model.Medecin;
import com.centremedical.repository.MedecinRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecins")
public class MedecinController {

    private final MedecinRepository medecinRepository;

    public MedecinController(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    @GetMapping
    public List<Medecin> listerTous() {
        return medecinRepository.findAll();
    }

    @GetMapping("/{codeMed}")
    public Medecin obtenirParCode(@PathVariable String codeMed) {
        return medecinRepository.findById(codeMed)
                .orElseThrow(() -> new ResourceNotFoundException("Medecin introuvable avec le code : " + codeMed));
    }

    @PostMapping
    public ResponseEntity<Medecin> creer(@Valid @RequestBody Medecin medecin) {
        if (medecin.getCodeMed() == null || medecin.getCodeMed().isBlank()) {
            throw new IllegalArgumentException("Le code medecin est obligatoire");
        }
        if (medecinRepository.existsById(medecin.getCodeMed())) {
            throw new IllegalArgumentException("Un medecin avec ce code existe deja : " + medecin.getCodeMed());
        }
        Medecin sauvegarde = medecinRepository.save(medecin);
        return new ResponseEntity<>(sauvegarde, HttpStatus.CREATED);
    }

    @PutMapping("/{codeMed}")
    public Medecin modifier(@PathVariable String codeMed, @Valid @RequestBody Medecin details) {
        Medecin medecin = medecinRepository.findById(codeMed)
                .orElseThrow(() -> new ResourceNotFoundException("Medecin introuvable avec le code : " + codeMed));
        medecin.setNom(details.getNom());
        medecin.setPrenom(details.getPrenom());
        medecin.setGrade(details.getGrade());
        return medecinRepository.save(medecin);
    }

    @DeleteMapping("/{codeMed}")
    public ResponseEntity<Void> supprimer(@PathVariable String codeMed) {
        Medecin medecin = medecinRepository.findById(codeMed)
                .orElseThrow(() -> new ResourceNotFoundException("Medecin introuvable avec le code : " + codeMed));
        medecinRepository.delete(medecin);
        return ResponseEntity.noContent().build();
    }
}
