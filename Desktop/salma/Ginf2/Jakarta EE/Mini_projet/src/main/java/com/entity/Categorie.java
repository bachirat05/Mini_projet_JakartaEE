package com.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "categorie")
public class Categorie {
    @Id
    @ColumnDefault("nextval('categorie_id_cat_seq')")
    @Column(name = "id_cat", nullable = false)
    private Integer id;

    @Column(name = "nom", length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "typearticle", columnDefinition = "type_article")
    public TypeArticle type;
    public enum TypeArticle {
        Livre,
        Materiau
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

    public TypeArticle getType() { return type; }

    public void setType(TypeArticle type) { this.type = type; }

}