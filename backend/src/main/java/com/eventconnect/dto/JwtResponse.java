package com.eventconnect.dto;

/**
 * DTO pour les réponses d'authentification JWT
 */
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String nom;
    private String prenom;

    public JwtResponse() {}

    public JwtResponse(String token, String email, String nom, String prenom) {
        this.token = token;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
    }

    public JwtResponse(String token, String type, String email, String nom, String prenom) {
        this.token = token;
        this.type = type;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
