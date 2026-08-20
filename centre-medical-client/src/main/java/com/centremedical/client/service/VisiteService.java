package com.centremedical.client.service;

import com.centremedical.client.model.Visite;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisiteService {

    public List<Visite> listerTous() throws Exception {
        var response = ApiClient.get("/visites");
        return ApiClient.mapper().readValue(response.body(), new TypeReference<List<Visite>>() {});
    }

    public void creer(String codeMed, String codePat, String dateIso) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("codeMed", codeMed);
        body.put("codePat", codePat);
        body.put("date", dateIso);
        ApiClient.post("/visites", body);
    }

    public void modifier(Long id, String codeMed, String codePat, String dateIso) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("codeMed", codeMed);
        body.put("codePat", codePat);
        body.put("date", dateIso);
        ApiClient.put("/visites/" + id, body);
    }

    public void supprimer(Long id) throws Exception {
        ApiClient.delete("/visites/" + id);
    }
}
