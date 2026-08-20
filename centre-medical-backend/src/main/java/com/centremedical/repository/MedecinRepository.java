package com.centremedical.repository;

import com.centremedical.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin, String> {
}
