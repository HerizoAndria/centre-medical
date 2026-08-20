# Projet 2 : Gestion des visites dans un centre médical

Implémentation conforme à l'énoncé :
- **Backend** : Spring Boot + MySQL (API REST) — dossier `centre-medical-backend`
- **Client** : Application desktop Java Swing — dossier `centre-medical-client`
- 3 classes/tables : `MEDECIN`, `PATIENT`, `VISITER`
- CRUD complet sur les 3 tables
- Recherche des patients par code ou par nom

## Architecture

```
Client Swing (desktop)  --->  API REST Spring Boot (port 8080)  --->  MySQL
```

Le client Swing ne parle jamais directement à MySQL : il appelle l'API REST en HTTP/JSON.
C'est l'architecture standard recommandée (le sujet demande "Mapper les 3 classes" côté
traitement, ce qui correspond au mapping JPA fait dans le backend).

## 1) Backend Spring Boot

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8 installé et démarré

### Configuration
Éditer `centre-medical-backend/src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/centre_medical?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```
Pas besoin de créer la base à la main : `createDatabaseIfNotExist=true` et
`spring.jpa.hibernate.ddl-auto=update` créent la base et les 3 tables automatiquement
au premier démarrage. Un script `schema.sql` (optionnel, avec données de test) est
fourni si vous préférez créer la base manuellement.

### Lancer le backend
```bash
cd centre-medical-backend
mvn spring-boot:run
```
L'API démarre sur `http://localhost:8080`.

### Endpoints REST disponibles

| Ressource | Méthode | URL | Description |
|---|---|---|---|
| Médecin | GET | `/api/medecins` | Liste tous les médecins |
| Médecin | GET | `/api/medecins/{codeMed}` | Un médecin |
| Médecin | POST | `/api/medecins` | Créer |
| Médecin | PUT | `/api/medecins/{codeMed}` | Modifier |
| Médecin | DELETE | `/api/medecins/{codeMed}` | Supprimer |
| Patient | GET | `/api/patients` | Liste tous les patients |
| Patient | GET | `/api/patients/recherche?motCle=...` | **Recherche par code ou nom** |
| Patient | GET/POST/PUT/DELETE | `/api/patients/{codePat}` | CRUD |
| Visite | GET | `/api/visites` | Liste toutes les visites |
| Visite | GET | `/api/visites/patient/{codePat}` | Visites d'un patient |
| Visite | GET | `/api/visites/medecin/{codeMed}` | Visites d'un médecin |
| Visite | POST/PUT | `/api/visites` | Corps JSON : `{"codeMed":"M001","codePat":"P001","date":"2026-08-18"}` |
| Visite | DELETE | `/api/visites/{id}` | Supprimer |

## 2) Client desktop Java Swing

### Lancer en mode développement
```bash
cd centre-medical-client
mvn compile exec:java -Dexec.mainClass="com.centremedical.client.MainApp"
```
(ou ouvrir le projet dans IntelliJ/Eclipse et exécuter `MainApp.java`)

### Générer un .jar exécutable
```bash
cd centre-medical-client
mvn clean package
java -jar target/centre-medical-client.jar
```

Le client suppose que le backend tourne sur `http://localhost:8080/api`
(modifiable dans `ApiClient.java`, constante `BASE_URL`).

### Fonctionnalités de l'interface
- 3 onglets : **Médecins**, **Patients**, **Visites**
- Chaque onglet a un tableau + formulaire : Ajouter / Modifier / Supprimer / Nouveau
- Onglet Patients : barre de recherche par **code ou nom**
- Onglet Visites : listes déroulantes pour choisir médecin/patient existants + date

## Ordre de test recommandé
1. Démarrer MySQL
2. Démarrer le backend (`mvn spring-boot:run`)
3. Démarrer le client Swing
4. Créer quelques médecins et patients
5. Créer des visites en associant un médecin + un patient + une date
6. Tester la recherche de patients par code/nom

## Remarque
Je n'ai pas pu compiler ce projet dans mon environnement (pas d'accès à Maven Central),
donc testez la compilation chez vous avec `mvn compile` avant de lancer. Le code a été
relu attentivement mais dites-moi si une erreur de compilation apparaît, je la corrige.
