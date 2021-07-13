package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Interaction;
import tedi.backend.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class InteractionRepositoryCustomImpl implements InteractionRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public Interaction getInteraction(Long depId, Long userId) {
        Interaction interaction = null;
        Query query = entityManager.createQuery("select i from Interaction i , Department  d, User u where d.id = i.department.id  and u.id = i.user.id and d.id = ?1 and u.id = ?2");
        query.setParameter(1 , depId);
        query.setParameter(2 , userId);
        List<Interaction> interactions = query.getResultList();
        if (interactions != null && interactions.size() > 0) {
            interaction = interactions.get(0);
            return interaction;
        }
        else
            return null;
    }
}
