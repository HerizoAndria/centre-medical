package com.centremedical.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * Client HTTP generique pour dialoguer avec le backend Spring Boot.
 * Centralise l'URL de base, le client HTTP et l'ObjectMapper Jackson.
 */
public class ApiClient {

    // Adapter l'URL si le backend tourne sur une autre machine/port
    public static final String BASE_URL = "http://localhost:8080/api";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static class ApiException extends Exception {
        public final int statusCode;
        public ApiException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }
    }

    public static HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return envoyer(request);
    }

    public static HttpResponse<String> post(String path, Object body) throws Exception {
        String json = MAPPER.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();
        return envoyer(request);
    }

    public static HttpResponse<String> put(String path, Object body) throws Exception {
        String json = MAPPER.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(json))
                .build();
        return envoyer(request);
    }

    public static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        return envoyer(request);
    }

    private static HttpResponse<String> envoyer(HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new ApiException(response.statusCode(), extraireMessage(response.body()));
        }
        return response;
    }

    private static String extraireMessage(String body) {
        try {
            var node = MAPPER.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
            if (node.has("erreurs")) {
                return node.get("erreurs").toString();
            }
        } catch (Exception ignored) {
        }
        return body;
    }
}
