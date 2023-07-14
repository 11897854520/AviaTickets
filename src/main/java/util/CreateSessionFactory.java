package util;

import model.airportData.Airport;
import model.airportData.Coordinates;
import model.airportData.NameTranslation;
import model.flight.Flight;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class CreateSessionFactory {

    private static SessionFactory sessionFactory;

    private CreateSessionFactory() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Flight.class);
                configuration.addAnnotatedClass(Airport.class);
                configuration.addAnnotatedClass(Coordinates.class);
                configuration.addAnnotatedClass(NameTranslation.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return sessionFactory;
    }
}
