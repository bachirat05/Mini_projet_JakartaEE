package com.example.mini_projet.DAO;

import com.entity.Categorie;
import com.example.mini_projet.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class CategorieDAO {

    // Récupère toutes les catégories depuis la table "categorie" dans PostgreSQL
    public List<Categorie> getAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL pour tout sélectionner
            return session.createQuery("FROM Categorie", Categorie.class).getResultList();
        } catch (Exception e) {
            System.err.println("ERREUR DANS CategorieDAO : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** Retourne toutes les catégories (pour le <select> du formulaire). */
    public List<Categorie> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Categorie> cats = session
                    .createQuery("FROM Categorie ORDER BY nom", Categorie.class)
                    .getResultList();
            tx.commit();
            return cats;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    /** Retourne une catégorie par son id. */
    public Categorie findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Categorie cat = session.find(Categorie.class, id);
            tx.commit();
            return cat;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
}