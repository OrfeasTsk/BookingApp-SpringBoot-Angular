package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Message;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class MessageRepositoryCustomImpl implements  MessageRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Message> getReceivedMessages(Long userId,Long depId){

        String queryStr = "select m from Message m where m.forUser.id = ?1 and m.isQuestion = true ";

        if(depId != null)
            queryStr += " and m.aboutDepartment.id = ?2 ";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1, userId);

        if(depId != null)
            query.setParameter(2 , depId);

        return query.getResultList();


    }

    @Override
    public List<Message> getSentMessages(Long userId,Long depId){

        String queryStr = "select m from Message m where m.fromUser.id = ?1 and m.isQuestion = true ";

        if(depId != null)
            queryStr += " and m.aboutDepartment.id = ?2 ";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1, userId);

        if(depId != null)
            query.setParameter(2 , depId);

        return query.getResultList();


    }

}
