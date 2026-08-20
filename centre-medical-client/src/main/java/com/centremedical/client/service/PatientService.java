package com.centremedical.client.service;

import com.centremedical.client.model.Patient;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PatientService {

    public List<Patient> listerTous() throws Exception {
        var response = ApiClient.get("/patients");
        return ApiClient.mapper().readValue(response.body(), new TypeReference<List<Patient>>() {});
    }

    public List<Patient> rechercher(String motCle) throws Exception {
        String encode = URLEncoder.encode(motCle, StandardCharsets.UTF_8);
        var response = ApiClient.get("/patients/recherche?motCle=" + encode);
        return ApiClient.mapper().readValue(response.body(), new TypeReference<List<Patient>>() {});
    }

    public void creer(Patient patient) throws Exception {
        ApiClient.post("/patients", patient);
    }

    public void modifier(String codePat, Patient patient) throws Exception {
        ApiClient.put("/patients/" + codePat, patient);
    }

    public void supprimer(String codePat) throws Exception {
        ApiClient.delete("/patients/" + codePat);
    }
}
