package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Booking;
import tedi.backend.model.Review;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class ReviewRepositoryCustomImpl implements  ReviewRepositoryCustom{



    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Review> getDepartmentReviews(){
        Query query = entityManager.createQuery("SELECT r FROM Review r WHERE r.forDepartment is not null");
        return query.getResultList();
    }

    @Override
    public List<Review> getHostReviews(){
        Query query = entityManager.createQuery("SELECT r FROM Review r WHERE r.forUser is not null");
        return query.getResultList();
    }

    @Override
    public Boolean hasReviewedDep(Long depId, Long userId) {
        Query query = entityManager.createQuery("SELECT r FROM Review r WHERE r.fromUser.id = ?1 and r.forDepartment.id = ?2");
        query.setParameter(1 , userId);
        query.setParameter(2 , depId);
        List<Review> reviews = query.getResultList();
        if(reviews != null && reviews.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Boolean hasReviewedHost(Long tenantId, Long hostId) {
        Query query = entityManager.createQuery("SELECT r FROM Review r WHERE r.fromUser.id = ?1 and r.forUser.id = ?2");
        query.setParameter(1 , tenantId);
        query.setParameter(2 , hostId);
        List<Review> reviews = query.getResultList();
        if(reviews != null && reviews.size() > 0)
            return true;
        else
            return false;

    }


    @Override
    public Integer getRating(Long userId,Long depId){
        Query query = entityManager.createQuery("SELECT r FROM Review r WHERE r.fromUser.id = ?1 and r.forDepartment.id = ?2");
        query.setParameter(1 , userId);
        query.setParameter(2 , depId);
        List<Review> reviews = query.getResultList();
        if(reviews != null && reviews.size() > 0)
            return reviews.get(0).getStars();
        else
            return -1;



    }

}
