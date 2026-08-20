-- Ce script est OPTIONNEL : Spring Boot (spring.jpa.hibernate.ddl-auto=update)
-- cree et met a jour automatiquement les tables au demarrage.
-- Vous pouvez utiliser ce script si vous preferez creer la base manuellement.

CREATE DATABASE IF NOT EXISTS centre_medical;
USE centre_medical;

CREATE TABLE IF NOT EXISTS medecin (
    code_med VARCHAR(20) PRIMARY KEY,
    nom      VARCHAR(60) NOT NULL,
    prenom   VARCHAR(60) NOT NULL,
    grade    VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS patient (
    code_pat VARCHAR(20) PRIMARY KEY,
    nom      VARCHAR(60) NOT NULL,
    prenom   VARCHAR(60) NOT NULL,
    sexe     CHAR(1),
    adresse  VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS visiter (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code_med    VARCHAR(20) NOT NULL,
    code_pat    VARCHAR(20) NOT NULL,
    date_visite DATE NOT NULL,
    UNIQUE KEY uk_visite (code_med, code_pat, date_visite),
    FOREIGN KEY (code_med) REFERENCES medecin(code_med) ON DELETE CASCADE,
    FOREIGN KEY (code_pat) REFERENCES patient(code_pat) ON DELETE CASCADE
);

-- Quelques donnees de test (facultatif)
INSERT INTO medecin (code_med, nom, prenom, grade) VALUES
  ('M001', 'Benali', 'Karim', 'Professeur'),
  ('M002', 'Haddad', 'Sara', 'Maitre assistant');

INSERT INTO patient (code_pat, nom, prenom, sexe, adresse) VALUES
  ('P001', 'Amrani', 'Youssef', 'M', '12 Rue des Fleurs, Alger'),
  ('P002', 'Cherif', 'Lina', 'F', '5 Avenue de la Paix, Oran');
