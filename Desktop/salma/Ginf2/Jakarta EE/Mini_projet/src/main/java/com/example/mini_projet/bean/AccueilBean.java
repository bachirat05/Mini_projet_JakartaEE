package com.example.mini_projet.bean;
import com.entity.Article;
import com.entity.Categorie;
import com.example.mini_projet.DAO.ArticleDAO;
import com.example.mini_projet.DAO.CategorieDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped // Garde la recherche en mémoire (même si on va sur d'autres pages)
public class AccueilBean implements Serializable {
    private List<Categorie> categories; // Liste dynamique des catégories
    private List<Article> annonces;
    private ArticleDAO articleDAO = new ArticleDAO();
    private CategorieDAO categorieDAO = new CategorieDAO();

    // Nouveaux champs pour stocker les choix de l'utilisateur (chrger tous les pub sans filtre )
    private String motCle = "";
    private String categorieFiltre = "Tous";

    @PostConstruct
    public void init() {
        // Au démarrage du serveur, on charge les catégories de la BDD
        categories = categorieDAO.getAllCategories();
        filtrerAjax(); // Charge la liste complète au démarrage
    }

    // charger les articles
    public void filtrerAjax() {
        annonces = articleDAO.rechercher(motCle, categorieFiltre);
    }

    // Utilisée par la barre de recherche
    public String filtrer() {
        filtrerAjax();
        return "/accueil.xhtml?faces-redirect=true";
    }

    // --- GETTERS & SETTERS (Obligatoires pour JSF) ---
    public List<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }
    public List<Article> getAnnonces() { return annonces; }
    public void setAnnonces(List<Article> annonces) { this.annonces = annonces; }

    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public String getCategorieFiltre() { return categorieFiltre; }
    public void setCategorieFiltre(String categorieFiltre) { this.categorieFiltre = categorieFiltre; }
}