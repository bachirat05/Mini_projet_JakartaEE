package com.example.mini_projet.DAO;
import com.entity.Article;

import com.entity.Categorie;
import com.example.mini_projet.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class ArticleDAO {

    //méthode pour lister les annoces récentes dans l'Accueil :
    public List<Article> getAnnoncesRecentes() {
        Transaction transaction = null;

        // On essaie d'ouvrir la session
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Query<Article> query = session.createQuery("SELECT a FROM Article a JOIN FETCH a.idCat", Article.class);
            List<Article> annonces = query.getResultList();
            transaction.commit();

            return annonces;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }


            System.err.println(" ERREUR DANS ArticleDAO : " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    // Méthode pour récupérer un article spécifique par son ID
    public Article getArticleById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // On ramène l'article + sa catégorie + son utilisateur
            Query<Article> query = session.createQuery(
                    "SELECT a FROM Article a JOIN FETCH a.idCat JOIN FETCH a.idUser WHERE a.id = :id",
                    Article.class
            );
            query.setParameter("id", id);
            Article article = query.uniqueResult(); // uniqueResult() car il n'y a qu'un seul article par ID

            transaction.commit();
            return article;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("ERREUR DANS ArticleDAO (getArticleById) : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Méthode pour rechercher (filtrer) un article soit par un mot clé (caractéristiques) ou par sa catégorie
    public List<Article> rechercher(String motCle, String nomCategorie) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 1=1  : Ça permet d’ajouter facilement des AND après sans se poser de questions
            StringBuilder hql = new StringBuilder("SELECT a FROM Article a JOIN FETCH a.idCat WHERE 1=1 ");

            // Si l'utilisateur a tapé un mot-clé
            if (motCle != null && !motCle.trim().isEmpty()) {
                hql.append("AND (lower(a.titre) LIKE :motCle OR lower(a.description) LIKE :motCle OR lower(a.ville) LIKE :motCle) ");
            }

            // Si l'utilisateur a choisi une catégorie spécifique
            if (nomCategorie != null && !nomCategorie.equals("Tous") && !nomCategorie.trim().isEmpty()) {
                hql.append("AND a.idCat.nom = :nomCategorie ");
            }

            hql.append("ORDER BY a.datePublication DESC");

            Query<Article> query = session.createQuery(hql.toString(), Article.class);

            // Remplissage des paramètres
            if (motCle != null && !motCle.trim().isEmpty()) {
                query.setParameter("motCle", "%" + motCle.toLowerCase() + "%");
            }
            if (nomCategorie != null && !nomCategorie.equals("Tous") && !nomCategorie.trim().isEmpty()) {
                query.setParameter("nomCategorie", nomCategorie);
            }

            List<Article> resultats = query.getResultList();
            transaction.commit();
            return resultats;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("🚨 ERREUR DANS LA RECHERCHE : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

// Manar
    public static void createarticle(Article article) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(article);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                e.printStackTrace();
            }

        }
    }

    public static void updatearticle(Article article) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(article);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                e.printStackTrace();
            }

        }
    }

    public static void deletearticle(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Article a = session.find(Article.class, id);
            if (a != null) {
                session.remove(a);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                e.printStackTrace();
            }

        }
    }

    public static Article findarticle(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Article a;
        try {
            tx = session.beginTransaction();
            a = session.find(Article.class, id);
            tx.commit();
            return a;

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Article> filtrefindbyuserid(Integer id, String motcle, String ville) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            StringBuilder hql = new StringBuilder(
                    "FROM Article a WHERE a.idUser.id = :userId");

            if (motcle != null && !motcle.isBlank())
                hql.append(" AND lower(a.titre) LIKE :motCle");

            if (ville != null && !ville.isBlank())
                hql.append(" AND a.ville = :ville");

            hql.append(" ORDER BY a.datePublication DESC");

            var query = session.createQuery(hql.toString(), Article.class)
                    .setParameter("userId", id);

            if (motcle != null && !motcle.isBlank())
                query.setParameter("motCle", "%" + motcle.toLowerCase() + "%");

            if (ville != null && !ville.isBlank())
                query.setParameter("ville", ville);

            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> findVillesDistinctes(Integer userId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "SELECT DISTINCT a.ville FROM Article a " +
                                    "WHERE a.idUser.id = :uid AND a.ville IS NOT NULL ORDER BY a.ville",
                            String.class)
                    .setParameter("uid", userId)
                    .list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Categorie> findAllCategories() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Categorie ORDER BY nom", Categorie.class)
                    .list();
        } catch (Exception e) {
            return Collections.emptyList();
        }


    }
    public long countTotal(Integer userId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "SELECT count(a) FROM Article a WHERE a.idUser.id = :uid", Long.class)
                    .setParameter("uid", userId)
                    .uniqueResult();
        }
    }
    public long countParEtat(Integer userId, Article.EtatArticle etat) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "SELECT count(a) FROM Article a " +
                                    "WHERE a.idUser.id = :uid AND a.etat = :etat", Long.class)
                    .setParameter("uid", userId)
                    .setParameter("etat", etat)
                    .uniqueResult();
        }
    }

}