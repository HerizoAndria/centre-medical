package com.centremedical.repository;

import com.centremedical.model.Visite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisiteRepository extends JpaRepository<Visite, Long> {

    List<Visite> findByPatient_CodePat(String codePat);

    List<Visite> findByMedecin_CodeMed(String codeMed);
}
