package com.centremedical.client.service;

import com.centremedical.client.model.Medecin;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class MedecinService {

    public List<Medecin> listerTous() throws Exception {
        var response = ApiClient.get("/medecins");
        return ApiClient.mapper().readValue(response.body(), new TypeReference<List<Medecin>>() {});
    }

    public void creer(Medecin medecin) throws Exception {
        ApiClient.post("/medecins", medecin);
    }

    public void modifier(String codeMed, Medecin medecin) throws Exception {
        ApiClient.put("/medecins/" + codeMed, medecin);
    }

    public void supprimer(String codeMed) throws Exception {
        ApiClient.delete("/medecins/" + codeMed);
    }
}
