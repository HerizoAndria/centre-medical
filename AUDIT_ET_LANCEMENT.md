# Audit, refonte et lancement - Centre Medical

## 1. Portee de la demande

Le fichier joint `centre_medical.sql` a ete traite comme une source de donnees SQL uniquement. Les commentaires du dump phpMyAdmin sont des metadonnees techniques, pas des consignes de travail. Les consignes suivies sont celles de la demande utilisateur.

## 2. Audit du client desktop initial

Le client `centre-medical-client` etait fonctionnel pour les operations de base, mais il ressemblait surtout a une interface de demonstration Swing :

- Navigation par onglets simple, peu adaptee a une application desktop professionnelle.
- Pas de tableau de bord ni d'indicateurs rapides sur l'activite du centre.
- Pas de pagination sur les listes. Les tables affichaient tout, ce qui devient lent et peu lisible avec beaucoup de donnees.
- Recherche limitee, notamment cote patients.
- Pas de vue analytique : aucun KPI, aucun graphique, aucune projection des visites a venir.
- Design visuel non unifie : boutons, champs, tableaux et formulaires avaient le style Swing par defaut.
- Les ecrans CRUD etaient utilisables, mais pas organises pour un usage quotidien avec beaucoup d'enregistrements.

## 3. Ameliorations realisees

### Interface generale

- Refonte de la fenetre principale avec une navigation laterale de type application desktop.
- Ajout d'un bandeau superieur avec etat de synchronisation.
- Remplacement de la navigation par onglets par une navigation par vues : Tableau de bord, Medecins, Patients, Visites.
- Ajout d'un theme centralise dans `AppTheme` : couleurs, polices, cartes, boutons, champs, badges et tableaux.
- Harmonisation des espaces, bordures, entetes de tableaux et zones de formulaire.

### Tableau de bord

Nouveau fichier : `centre-medical-client/src/main/java/com/centremedical/client/ui/DashboardPanel.java`

Ajouts :

- KPI Patients.
- KPI Medecins.
- KPI Visites.
- KPI Visites a venir.
- Graphique des visites par mois.
- Graphique de l'activite par specialite.
- Tableau des prochaines visites.

### Gestion des patients

Fichier refondu : `centre-medical-client/src/main/java/com/centremedical/client/ui/PatientPanel.java`

Ajouts :

- Pagination avec choix de taille de page : 10, 25, 50, 100.
- Recherche instantanee multi-colonnes : code, nom, prenom, telephone, adresse.
- Compteur de resultats.
- Navigation page precedente / suivante.
- Formulaire lateral plus clair.
- Conservation des actions CRUD : ajouter, modifier, supprimer, vider.
- Gestion correcte de la selection meme quand le tableau est trie.

### Gestion des medecins

Fichier refondu : `centre-medical-client/src/main/java/com/centremedical/client/ui/MedecinPanel.java`

Ajouts :

- Pagination 10, 25, 50, 100.
- Recherche instantanee par code, nom, prenom et specialite.
- Compteur de resultats.
- Interface en deux zones : table principale et formulaire lateral.
- Conservation des actions CRUD.
- Selection robuste avec tri de table.

### Gestion des visites

Fichier refondu : `centre-medical-client/src/main/java/com/centremedical/client/ui/VisitePanel.java`

Ajouts :

- Pagination 10, 25, 50, 100.
- Recherche instantanee par date, code medecin, nom medecin, specialite, code patient, nom patient.
- Bouton `Date du jour`.
- Rechargement des listes medecins/patients.
- Tableau enrichi avec medecin et patient lisibles.
- Conservation des actions CRUD.

### Donnees SQL de demonstration

Nouveau fichier : `centre-medical-backend/import-centre-medical-demo.sql`

Ce script :

- Recree la base `centre_medical`.
- Reprend les donnees du dump initial.
- Ajoute beaucoup de donnees de test.
- Genere des medecins supplementaires.
- Genere des patients supplementaires.
- Genere plusieurs centaines de visites via une procedure SQL temporaire.

### Configuration backend

Fichier modifie : `centre-medical-backend/src/main/resources/application.properties`

L'URL MySQL accepte maintenant la creation automatique de la base si elle n'existe pas :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/centre_medical?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

## 4. Build effectue

Maven n'etait pas installe globalement sur cette machine. Une version locale a ete ajoutee ici :

```bash
/tmp/apache-maven-3.9.9/bin/mvn
```

Build backend reussi :

```bash
cd centre-medical-backend
/tmp/apache-maven-3.9.9/bin/mvn package
```

Jar genere :

```bash
centre-medical-backend/target/centre-medical-backend-1.0.0.jar
```

Build client reussi :

```bash
cd centre-medical-client
/tmp/apache-maven-3.9.9/bin/mvn package
```

Jar genere :

```bash
centre-medical-client/target/centre-medical-client.jar
```

Si Maven est installe globalement, remplacer `/tmp/apache-maven-3.9.9/bin/mvn` par `mvn`.

## 5. Importer les donnees

### Option A - MySQL local existant

Importer le script enrichi :

```bash
mysql -u VOTRE_UTILISATEUR -p < centre-medical-backend/import-centre-medical-demo.sql
```

Exemple si un utilisateur MySQL dedie existe :

```bash
mysql -u centre_medical -p < centre-medical-backend/import-centre-medical-demo.sql
```

Sur cette machine, l'import n'a pas pu etre execute car MySQL refuse la connexion locale sans mot de passe :

```text
ERROR 1698 (28000): Access denied for user 'root'@'localhost'
```

Pour corriger, se connecter avec un compte administrateur MySQL puis creer un utilisateur applicatif :

```sql
CREATE USER 'centre_medical'@'localhost' IDENTIFIED BY 'centre_medical_pwd';
GRANT ALL PRIVILEGES ON centre_medical.* TO 'centre_medical'@'localhost';
FLUSH PRIVILEGES;
```

Puis importer :

```bash
mysql -u centre_medical -p < centre-medical-backend/import-centre-medical-demo.sql
```

### Option B - Instance MySQL temporaire sans droit administrateur

Cette option evite de modifier le service MySQL installe sur la machine. Elle a ete utilisee pour lancer le projet localement.

Initialiser une instance temporaire :

```bash
DATA_DIR=$(mktemp -d /tmp/centre-medical-mysql-data.XXXXXX)
mysqld --initialize-insecure \
  --datadir="$DATA_DIR" \
  --log-error=/tmp/centre-medical-mysql-init.log
```

Demarrer MySQL sur le port `3307` :

```bash
mysqld \
  --datadir="$DATA_DIR" \
  --port=3307 \
  --socket=/tmp/centre-medical-mysql.sock \
  --pid-file=/tmp/centre-medical-mysql.pid \
  --log-error=/tmp/centre-medical-mysql.log \
  --bind-address=127.0.0.1 \
  --skip-networking=0
```

Dans un autre terminal, verifier puis importer :

```bash
mysqladmin --protocol=tcp -h127.0.0.1 -P3307 -uroot ping
mysql --protocol=tcp -h127.0.0.1 -P3307 -uroot < centre-medical-backend/import-centre-medical-demo.sql
```

Verifier les volumes :

```bash
mysql --protocol=tcp -h127.0.0.1 -P3307 -uroot -e \
  "SELECT COUNT(*) AS medecins FROM centre_medical.medecin;
   SELECT COUNT(*) AS patients FROM centre_medical.patient;
   SELECT COUNT(*) AS visites FROM centre_medical.visiter;"
```

Resultat obtenu localement :

```text
medecins: 36
patients: 180
visites: 543
```

## 6. Lancer le backend

Avec la configuration par defaut actuelle :

```bash
java -jar centre-medical-backend/target/centre-medical-backend-1.0.0.jar
```

Si vous utilisez un utilisateur MySQL dedie, lancer avec surcharge :

```bash
java -jar centre-medical-backend/target/centre-medical-backend-1.0.0.jar \
  --spring.datasource.username=centre_medical \
  --spring.datasource.password=centre_medical_pwd
```

Avec l'instance temporaire MySQL sur le port `3307` :

```bash
java -jar centre-medical-backend/target/centre-medical-backend-1.0.0.jar \
  --spring.datasource.url='jdbc:mysql://127.0.0.1:3307/centre_medical?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
  --spring.datasource.username=root \
  --spring.datasource.password=
```

Le backend expose l'API sur :

```text
http://localhost:8080
```

## 7. Lancer le client desktop

Apres le demarrage du backend :

```bash
java -jar centre-medical-client/target/centre-medical-client.jar
```

Le client Swing consomme l'API du backend local. Si le backend n'est pas demarre, les ecrans s'ouvrent mais les listes ne peuvent pas se synchroniser.

## 8. Verification rapide

Verifier que MySQL ecoute :

```bash
ss -ltnp | grep 3306
```

Verifier que le backend repond :

```bash
curl http://localhost:8080/api/patients
```

Relancer un build complet :

```bash
cd centre-medical-backend
/tmp/apache-maven-3.9.9/bin/mvn package

cd ../centre-medical-client
/tmp/apache-maven-3.9.9/bin/mvn package
```

## 9. Etat local au moment de cette livraison

- Build backend : OK.
- Build client : OK.
- Import SQL dans le service MySQL systeme : bloque par les identifiants MySQL locaux.
- Import SQL dans l'instance temporaire MySQL `127.0.0.1:3307` : OK.
- Lancement backend avec l'instance temporaire : OK.
- Verification API : OK sur `/api/patients`, `/api/medecins`, `/api/visites`.
- Client desktop : lance avec le backend actif.
