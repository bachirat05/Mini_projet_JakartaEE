package com.example.mini_projet.bean;
import com.entity.User;
import com.example.mini_projet.DAO.UserDAO;
import com.example.mini_projet.Util.HashUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import jakarta.inject.Inject;


@Named
@RequestScoped
public class RegisterBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    private String nom;
    private String prenom;
    private String ville;
    private String telephone;
    private String email;
    private String password;

    private String message;

    public String register() {

        // 🔹 Vérification des champs
        if (nom == null || prenom == null || email == null || password == null || telephone == null ||
                nom.trim().isEmpty() || prenom.trim().isEmpty() || email.trim().isEmpty() ||
                password.trim().isEmpty() || telephone.trim().isEmpty()) {

            message = "Champs obligatoires manquants";
            return null;
        }

        // 🔹 Création utilisateur
        User user = new User(
                nom,
                prenom,
                email,
                HashUtil.hashPassword(password),
                telephone,
                ville
        );

        String result = UserDAO.saveUser(user);

        if ("succès".equals(result)) {
            loginBean.setUser(user); // STOCKAGE SESSION
            return "accueil.xhtml?faces-redirect=true";
        } else {
            message = result;
            return null;
        }
    }

    // 🔹 Getters / Setters

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMessage() { return message; }
}