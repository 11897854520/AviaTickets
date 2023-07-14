package dao;

import jakarta.persistence.NoResultException;
import model.airportData.Airport;
import model.flight.Flight;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.CreateSessionFactory;

import java.util.List;

public class Dao {


    public static void save(Object object) {
        Session session = CreateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(object);
        transaction.commit();
        session.close();
    }

    public static List<String> findAllAirports() {
        List airports = CreateSessionFactory.getSessionFactory().openSession()
                .createQuery("SELECT code FROM Airport", Airport.class).list();
        return airports;
    }

    public static Airport findByCode(String code) {
        Query query = CreateSessionFactory.getSessionFactory().openSession()
                .createQuery("from Airport WHERE code = :code", Airport.class);
        query.setParameter("code", code);
        return (Airport) query.getSingleResult();
    }

    public static List<Flight> findAllFlights() {
        return CreateSessionFactory.getSessionFactory().openSession()
                .createQuery("from Flight", Flight.class).list();
    }

    public static Flight findByNumber(String number) {
        try {
            Query query = CreateSessionFactory.getSessionFactory().openSession()
                    .createQuery("from Flight WHERE number = :number", Flight.class);
            query.setParameter("number", number);
            return (Flight) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
