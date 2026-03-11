package com.example.mini_projet.Util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Crée la SessionFactory à partir de hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur lors de la création de la SessionFactory", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
