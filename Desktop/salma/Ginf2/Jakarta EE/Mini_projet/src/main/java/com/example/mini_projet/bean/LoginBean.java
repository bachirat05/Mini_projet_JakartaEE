package com.example.mini_projet.bean;

import com.entity.User;
import com.example.mini_projet.DAO.UserDAO;
import com.example.mini_projet.Util.HashUtil;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String email;
    private String password;
    private User user; // utilisateur connecté
    private String message; // message d'erreur

    public String login() {

        // 🔹 Vérification champs
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            message = "Champs obligatoires manquants";
            return null; // reste sur la même page
        }

        String passwordHashe = HashUtil.hashPassword(password);
        List<User> users = UserDAO.getAllUsers();

        for (User u : users) {
            if (u.getMail().equalsIgnoreCase(email) &&
                    u.getMotdepasse().equalsIgnoreCase(passwordHashe)) {

                user = u;// stocké en session automatiquement
                // redirection JSF
                if(u.getRole() == User.RoleUser.Admin){
                    return "accueilAdmin.xhtml?faces-redirect=true";
                }
                return "accueil.xhtml?faces-redirect=true";
            }
        }

        message = "Identifiants incorrects";
        return null;
    }

    public String logout() {
        user = null;
        return "connexion.xhtml?faces-redirect=true";
    }

    // 🔹 Getters / Setters

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User getUser() { return user; }

    public String getMessage() { return message; }

    public void setUser(User user) {
        this.user = user;
    }

    // Ces méthodes vont servir dans JSF ; rendered="#{loginBean.admin}
    // C’est une convention JavaBeans : isXxx() → #{xxx}
    public boolean isAdmin() {
        return user != null && user.getRole() == User.RoleUser.Admin;
    }

    public boolean isEtudiant() {
        return user != null && user.getRole() == User.RoleUser.Etudiant;
    }
}