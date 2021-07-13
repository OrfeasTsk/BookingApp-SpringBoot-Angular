package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Booking;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Boolean BookingExists(Long depId, Date startDate, Date endDate) {

        String queryStr = "select b from Booking b where b.department.id = ?1 and ((b.startDate <= ?2 and b.endDate >= ?2) or (b.endDate >= ?3 and b.startDate <= ?3) or (?2 < b.startDate and ?3 > b.endDate))";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1 , depId);
        query.setParameter(2 , startDate);
        query.setParameter( 3 , endDate);
        List<Booking> bookings = query.getResultList();
        if(bookings != null && bookings.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Boolean hasBooked(Long depId, Long userId) {

        String queryStr = "select b from Booking b where b.department.id = ?1 and b.tenant.id = ?2";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1 , depId);
        query.setParameter(2 , userId);
        List<Booking> bookings = query.getResultList();
        if(bookings != null && bookings.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Boolean hasBookedAtLeastOne(Long tenantId, Long hostId) {

        String queryStr = "select b from Booking b where b.department.host.id = ?1 and b.tenant.id = ?2";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1 , hostId);
        query.setParameter(2 , tenantId);
        List<Booking> bookings = query.getResultList();
        if(bookings != null && bookings.size() > 0)
            return true;
        else
            return false;
    }



}
