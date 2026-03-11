package com.entity;
import com.entity.Categorie;
import com.entity.User;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Table(name = "article")
public class Article {
    @Id
    @ColumnDefault("nextval('article_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "titre", length = 200)
    private String titre;

    @Column(name = "auteur", length = 100)
    private String auteur;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "ville", length = 50)
    private String ville;

    @Column(name = "photo")
    private String photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cat")
    private Categorie idCat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User idUser;
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", columnDefinition = "etat_article")
    private EtatArticle etat;

    public enum EtatArticle {
        Neuf,
        Bon_Etat,
        Ancien_Usage
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Categorie getIdCat() {
        return idCat;
    }

    public void setIdCat(Categorie idCat) {
        this.idCat = idCat;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }


}