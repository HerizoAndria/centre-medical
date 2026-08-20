package com.centremedical.repository;

import com.centremedical.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, String> {

    // Recherche des patients par code (contient) ou par nom (contient, insensible a la casse)
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.codePat) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :motCle, '%'))")
    List<Patient> rechercherParCodeOuNom(@Param("motCle") String motCle);
}
