package org.apache.karaf.examples.jpa.provider.ds.hibernate;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.examples.jpa.Booking;
import org.apache.karaf.examples.jpa.BookingService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

/**
 * Implementation of the booking service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = BookingService.class, immediate = true)
public class BookingServiceImpl implements BookingService {

    @Reference(target = "(osgi.unit.name=booking-hibernate)")
    private JpaTemplate jpaTemplate;

    @Override
    public void add(Booking booking) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(booking);
            entityManager.flush();
        });
    }

    @Override
    public void add(String flight, String customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setFlight(flight);
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(booking);
            entityManager.flush();
        });
    }

    @Override
    public List<Booking> list() {
        return jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT b FROM Booking b", Booking.class).getResultList());
    }

    @Override
    public Booking get(Long id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(Booking.class, id));
    }

    @Override
    public void remove(Long id) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            Booking booking = entityManager.find(Booking.class, id);
            if (booking !=  null) {
                entityManager.remove(booking);
            }
        });
    }
}
