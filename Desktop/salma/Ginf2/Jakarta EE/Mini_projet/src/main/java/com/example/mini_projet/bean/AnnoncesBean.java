package com.example.mini_projet.bean;


import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.entity.Article;
import com.entity.Categorie;
import com.entity.User;
import com.example.mini_projet.DAO.ArticleDAO;
import com.example.mini_projet.DAO.CategorieDAO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("annoncesBean")
@ViewScoped
public class AnnoncesBean implements Serializable {

    private final ArticleDAO articleDAO = new ArticleDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();

    // Filtres
    private String motCle = "";
    private String villeFiltre = "";
    private Integer categorieFiltre;

    // Listes pour l'affichage
    private List<Article> annonces = new ArrayList<>();
    private List<String> villes = new ArrayList<>();
    private List<Categorie> categories = new ArrayList<>();

    // Statistiques
    private long totalAnnonces;
    private long totalNeuf;
    private long totalBonEtat;
    private long totalAncien;

    // Formulaire d'édition / création
    private Article articleCourant = new Article();
    private boolean modeEdition = false;
    private Integer selectedCategoryId;
    private String selectedEtatName;

    @PostConstruct
    public void init() {
        chargerDonnees();
        categories = safeGetCategories();
    }

    public void chargerDonnees() {
        User user = getUtilisateurCourant();
        if (user == null) return;

        try {
            annonces = articleDAO.filtrefindbyuserid(user.getId(), motCle, villeFiltre, categorieFiltre);
            villes = articleDAO.findVillesDistinctes(user.getId());
            totalAnnonces = articleDAO.countTotal(user.getId());
            totalNeuf = articleDAO.countParEtat(user.getId(), Article.EtatArticle.Neuf);
            totalBonEtat = articleDAO.countParEtat(user.getId(), Article.EtatArticle.Bon_Etat);
            totalAncien = articleDAO.countParEtat(user.getId(), Article.EtatArticle.Ancien_Usage);
        } catch (Exception e) {
            ajouterMessageErreur("Erreur de base de données : " + e.getMessage());
        }
    }

    // --- Actions ---

    public void filtrer() {
        chargerDonnees();
    }

    public void reinitialiserFiltres() {
        this.motCle = "";
        this.villeFiltre = "";
        this.categorieFiltre = null;
        chargerDonnees();
    }

    public String preparerCreation() {
        this.modeEdition = false;
        this.articleCourant = new Article();
        this.selectedCategoryId = null;
        this.selectedEtatName = null;
        return "gestion-annonce.xhtml?faces-redirect=true";
    }

    public String preparerEdition(Article a) {
        this.modeEdition = true;
        this.articleCourant = a;
        if (a.getIdCat() != null) {
            this.selectedCategoryId = a.getIdCat().getId();
        }
        if (a.getEtat() != null) {
            this.selectedEtatName = a.getEtat().name();
        }
        return "gestion-annonce.xhtml?faces-redirect=true"; // On transmet normalement l'ID via un paramètre, ou on garde le bean en session, mais Flash est mieux pour ViewScoped.
    }

    // Version recommandée si l'on navigue de page ViewScoped en ViewScoped sans tout perdre
    public String goEditionId(Integer id) {
        try {
            Article a = ArticleDAO.findarticle(id);
            if (a != null) {
                // On pourrait placer l'objet dans le FlashScope
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("articleEdition", a);
                return "gestion-annonce.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            ajouterMessageErreur("Erreur lors de la récupération.");
        }
        return null;
    }

    // Lors du chargement de la page de gestion-annonce (si redirigé avec Flash)
    public void onPageGestionLoad() {
        Article a = (Article) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("articleEdition");
        if (a != null) {
            this.modeEdition = true;
            this.articleCourant = a;
            if (a.getIdCat() != null) this.selectedCategoryId = a.getIdCat().getId();
            if (a.getEtat() != null) this.selectedEtatName = a.getEtat().name();
        } else if (!FacesContext.getCurrentInstance().isPostback() && this.articleCourant.getId() == null) {
            this.modeEdition = false;
            // Pour être sûr d'avoir un bean propre
        }
    }

    public String sauvegarder() {
        try {
            Categorie cat = categorieDAO.findById(selectedCategoryId);
            Article.EtatArticle etat = Article.EtatArticle.valueOf(selectedEtatName);

            articleCourant.setIdCat(cat);
            articleCourant.setEtat(etat);

            if (modeEdition) {
                // Mettre à jour l'existant en base
                Article aBase = ArticleDAO.findarticle(articleCourant.getId());
                aBase.setTitre(articleCourant.getTitre());
                aBase.setDescription(articleCourant.getDescription());
                aBase.setPrix(articleCourant.getPrix());
                aBase.setEtat(etat);
                aBase.setVille(articleCourant.getVille());
                aBase.setIdCat(cat);
                ArticleDAO.updatearticle(aBase);
                ajouterMessageSucces("Annonce modifiée !");
            } else {
                articleCourant.setIdUser(getUtilisateurCourant());
                articleCourant.setDatePublication(LocalDate.now());
                ArticleDAO.createarticle(articleCourant);
                ajouterMessageSucces("Annonce publiée !");
            }

            return "mes-annonces.xhtml?faces-redirect=true";

        } catch (Exception e) {
            ajouterMessageErreur("Erreur de saisie ou SQL : " + e.getMessage());
            return null; // Reste sur la page
        }
    }

    public void supprimer(Integer id) {
        try {
            ArticleDAO.deletearticle(id);
            ajouterMessageSucces("Annonce supprimée.");
            chargerDonnees(); // Recharge la liste
        } catch (Exception e) {
            ajouterMessageErreur("Erreur de suppression : " + e.getMessage());
        }
    }

    // --- Utilitaires ---

    private User getUtilisateurCourant() {
        return (User) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("utilisateurConnecte");
    }

    private List<Categorie> safeGetCategories() {
        try {
            return categorieDAO.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void ajouterMessageSucces(String msg) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    private void ajouterMessageErreur(String msg) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    // --- Getters et Setters ---

    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public String getVilleFiltre() { return villeFiltre; }
    public void setVilleFiltre(String villeFiltre) { this.villeFiltre = villeFiltre; }

    public Integer getCategorieFiltre() { return categorieFiltre; }
    public void setCategorieFiltre(Integer categorieFiltre) { this.categorieFiltre = categorieFiltre; }

    public List<Article> getAnnonces() { return annonces; }
    public void setAnnonces(List<Article> annonces) { this.annonces = annonces; }

    public List<String> getVilles() { return villes; }
    public void setVilles(List<String> villes) { this.villes = villes; }

    public List<Categorie> getCategories() { return categories; }
    public void setCategories(List<Categorie> categories) { this.categories = categories; }

    public long getTotalAnnonces() { return totalAnnonces; }
    public long getTotalNeuf() { return totalNeuf; }
    public long getTotalBonEtat() { return totalBonEtat; }
    public long getTotalAncien() { return totalAncien; }

    public Article getArticleCourant() { return articleCourant; }
    public void setArticleCourant(Article articleCourant) { this.articleCourant = articleCourant; }

    public boolean isModeEdition() { return modeEdition; }
    public void setModeEdition(boolean modeEdition) { this.modeEdition = modeEdition; }

    public Integer getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Integer selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }

    public String getSelectedEtatName() { return selectedEtatName; }
    public void setSelectedEtatName(String selectedEtatName) { this.selectedEtatName = selectedEtatName; }
}