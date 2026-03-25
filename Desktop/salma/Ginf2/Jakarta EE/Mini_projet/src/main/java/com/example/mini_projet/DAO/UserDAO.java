package com.example.mini_projet.DAO;

import com.example.mini_projet.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static String saveUser(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        String message = null;
        try {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            message ="succès";
        }
        catch (RuntimeException ex) {
            if(tx != null) tx.rollback();
            ex.printStackTrace();
            System.out.println("Erreur Hibernate : " + ex.getMessage());
            message = "error";
        }
        return message;

    }
    public static List<User> getAllUsers() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        List<User> users = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            users = session.createQuery("from User", User.class).list();
            tx.commit();
        }
        catch (RuntimeException ex) {
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
        return users;
    }
}
