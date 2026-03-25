package com.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users")
public class User {
    @Id
    // Pour dire à Hibernates de générer la clé automatiquement
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(
            name = "users_seq",
            sequenceName = "users_id_user_seq",
            allocationSize = 1
    )

    @Column(name = "id_user", nullable = false)
    private Integer id;

    @Column(name = "nom", length = 50)
    private String nom;

    @Column(name = "prenom", length = 50)
    private String prenom;

    @Column(name = "mail", length = 100)
    private String mail;

    @Column(name = "motdepasse")
    private String motdepasse;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "ville", length = 50)
    private String ville;

    @Enumerated(EnumType.STRING)
    // Ajout de non insertable et non updatable à cause de problèmes d'insertion
    @Column(name = "role", columnDefinition = "role_user",insertable = false, updatable = false)
    private RoleUser role;
    public enum RoleUser{
        Etudiant,
        Admin
    }
    // Ajout d'un constructeur vide ; important
    public User() {}
    public User(String nom, String prenom, String mail, String motdepasse, String telephone, String ville) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motdepasse = motdepasse;
        this.telephone = telephone;
        this.ville = ville;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public RoleUser getRole() { return role; }

    public void setRole(RoleUser role) { this.role = role; }

}