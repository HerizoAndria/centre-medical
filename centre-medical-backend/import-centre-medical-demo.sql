-- Import enrichi pour la base centre_medical.
-- Source fonctionnelle: dump centre_medical.sql fourni par l'utilisateur.
-- Les commentaires du dump sont traites comme metadata, pas comme instructions.

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS centre_medical;
CREATE DATABASE centre_medical CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE centre_medical;

CREATE TABLE medecin (
  code_med varchar(20) NOT NULL,
  grade varchar(40) DEFAULT NULL,
  nom varchar(60) NOT NULL,
  prenom varchar(60) NOT NULL,
  PRIMARY KEY (code_med)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE patient (
  code_pat varchar(20) NOT NULL,
  adresse varchar(150) DEFAULT NULL,
  nom varchar(60) NOT NULL,
  prenom varchar(60) NOT NULL,
  sexe varchar(1) DEFAULT NULL,
  PRIMARY KEY (code_pat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE visiter (
  id bigint NOT NULL AUTO_INCREMENT,
  date_visite date NOT NULL,
  code_med varchar(20) NOT NULL,
  code_pat varchar(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_visite (code_med, code_pat, date_visite),
  KEY fk_visiter_patient (code_pat),
  CONSTRAINT fk_visiter_medecin FOREIGN KEY (code_med) REFERENCES medecin (code_med) ON DELETE CASCADE,
  CONSTRAINT fk_visiter_patient FOREIGN KEY (code_pat) REFERENCES patient (code_pat) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO medecin (code_med, grade, nom, prenom) VALUES
('M001', 'Dentiste', 'ANDRIAVELOTOVO', 'Herizo'),
('M002', 'Chirurgien', 'RAKOTONANDRASANA', 'Jeannot'),
('M003', 'Specialiste cancer', 'ANDRIANTSOA', 'Hermenio'),
('M004', 'Cardiologue', 'RANAIVO', 'Hary Manana');

INSERT INTO patient (code_pat, adresse, nom, prenom, sexe) VALUES
('P001', 'Ampasambazaha', 'RABE', 'Koto', 'M'),
('P002', 'Antanifotsy', 'RANDRIA', 'Mihaja', 'F'),
('P003', 'Tanambao', 'FALY', 'Henika', 'F'),
('P004', 'Andavamba', 'RAKOTOMAMONJY', 'Florent', 'M');

INSERT INTO visiter (id, date_visite, code_med, code_pat) VALUES
(1, '2026-05-10', 'M001', 'P001'),
(2, '2026-07-17', 'M002', 'P002'),
(3, '2026-08-17', 'M004', 'P003');

DROP PROCEDURE IF EXISTS seed_centre_medical;
DELIMITER //
CREATE PROCEDURE seed_centre_medical()
BEGIN
  DECLARE i INT DEFAULT 5;
  DECLARE grade_label VARCHAR(40);
  DECLARE nom_label VARCHAR(60);
  DECLARE prenom_label VARCHAR(60);

  WHILE i <= 36 DO
    SET grade_label = CASE MOD(i, 12)
      WHEN 0 THEN 'Cardiologue'
      WHEN 1 THEN 'Generaliste'
      WHEN 2 THEN 'Pediatre'
      WHEN 3 THEN 'Gynecologue'
      WHEN 4 THEN 'Dermatologue'
      WHEN 5 THEN 'Radiologue'
      WHEN 6 THEN 'ORL'
      WHEN 7 THEN 'Neurologue'
      WHEN 8 THEN 'Ophtalmologue'
      WHEN 9 THEN 'Chirurgien'
      WHEN 10 THEN 'Dentiste'
      ELSE 'Urgentiste'
    END;
    SET nom_label = CASE MOD(i, 10)
      WHEN 0 THEN 'RAKOTO'
      WHEN 1 THEN 'RAZAFY'
      WHEN 2 THEN 'ANDRIANA'
      WHEN 3 THEN 'RABE'
      WHEN 4 THEN 'RANDRIA'
      WHEN 5 THEN 'RASOLO'
      WHEN 6 THEN 'RANAIVO'
      WHEN 7 THEN 'RAMANANA'
      WHEN 8 THEN 'RAJAO'
      ELSE 'RAVAO'
    END;
    SET prenom_label = CASE MOD(i, 10)
      WHEN 0 THEN 'Nirina'
      WHEN 1 THEN 'Miora'
      WHEN 2 THEN 'Tahina'
      WHEN 3 THEN 'Fanja'
      WHEN 4 THEN 'Tojo'
      WHEN 5 THEN 'Lova'
      WHEN 6 THEN 'Hery'
      WHEN 7 THEN 'Aina'
      WHEN 8 THEN 'Sitraka'
      ELSE 'Onja'
    END;

    INSERT INTO medecin (code_med, grade, nom, prenom)
    VALUES (CONCAT('M', LPAD(i, 3, '0')), grade_label, CONCAT(nom_label, i), prenom_label);
    SET i = i + 1;
  END WHILE;

  SET i = 5;
  WHILE i <= 180 DO
    SET nom_label = CASE MOD(i, 14)
      WHEN 0 THEN 'RABEARIVELO'
      WHEN 1 THEN 'RAKOTOBE'
      WHEN 2 THEN 'RANDRIANARISOA'
      WHEN 3 THEN 'RAZAFINDRAKOTO'
      WHEN 4 THEN 'ANDRIAMALALA'
      WHEN 5 THEN 'RASOAMANANA'
      WHEN 6 THEN 'RANAIVONIRINA'
      WHEN 7 THEN 'RAKOTOMALALA'
      WHEN 8 THEN 'RAKOTOSON'
      WHEN 9 THEN 'ANDRIATSILAVO'
      WHEN 10 THEN 'RAVELO'
      WHEN 11 THEN 'RAZANAKOTO'
      WHEN 12 THEN 'RANARIVELO'
      ELSE 'RATSIMBA'
    END;
    SET prenom_label = CASE MOD(i, 16)
      WHEN 0 THEN 'Jean'
      WHEN 1 THEN 'Marie'
      WHEN 2 THEN 'Haja'
      WHEN 3 THEN 'Mamy'
      WHEN 4 THEN 'Sarah'
      WHEN 5 THEN 'Eric'
      WHEN 6 THEN 'Clara'
      WHEN 7 THEN 'Toky'
      WHEN 8 THEN 'Bodo'
      WHEN 9 THEN 'Mickael'
      WHEN 10 THEN 'Fara'
      WHEN 11 THEN 'Elodie'
      WHEN 12 THEN 'Hobiana'
      WHEN 13 THEN 'Zo'
      WHEN 14 THEN 'Mialy'
      ELSE 'Tiana'
    END;

    INSERT INTO patient (code_pat, adresse, nom, prenom, sexe)
    VALUES (
      CONCAT('P', LPAD(i, 3, '0')),
      CONCAT('Lot ', MOD(i * 17, 400), ', quartier ', CASE MOD(i, 8)
        WHEN 0 THEN 'Centre'
        WHEN 1 THEN 'Ampasambazaha'
        WHEN 2 THEN 'Tanambao'
        WHEN 3 THEN 'Antanifotsy'
        WHEN 4 THEN 'Andavamba'
        WHEN 5 THEN 'Ambalavao'
        WHEN 6 THEN 'Mahazoarivo'
        ELSE 'Ankofafa'
      END),
      CONCAT(nom_label, i),
      prenom_label,
      IF(MOD(i, 2) = 0, 'F', 'M')
    );
    SET i = i + 1;
  END WHILE;

  SET i = 1;
  WHILE i <= 540 DO
    INSERT IGNORE INTO visiter (date_visite, code_med, code_pat)
    VALUES (
      DATE_ADD('2026-01-01', INTERVAL MOD(i, 420) DAY),
      CONCAT('M', LPAD(MOD(i * 7, 36) + 1, 3, '0')),
      CONCAT('P', LPAD(MOD(i * 11, 180) + 1, 3, '0'))
    );
    SET i = i + 1;
  END WHILE;
END//
DELIMITER ;

CALL seed_centre_medical();
DROP PROCEDURE seed_centre_medical;

ALTER TABLE visiter AUTO_INCREMENT = 1000;
