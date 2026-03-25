package com.example.mini_projet.bean;
import com.entity.Article;
import com.example.mini_projet.DAO.ArticleDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class DetailsBean {

    private int articleId; // L'ID reçu depuis l'URL
    private Article article; // L'article qu'on va afficher
    private ArticleDAO articleDAO = new ArticleDAO();

    // Cette méthode sera appelée automatiquement au chargement de la page
    public void loadArticle() {
        if (articleId > 0) {
            article = articleDAO.getArticleById(articleId);
        }
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}