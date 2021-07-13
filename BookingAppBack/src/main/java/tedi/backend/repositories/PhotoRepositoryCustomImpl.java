package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Photo;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class PhotoRepositoryCustomImpl implements PhotoRepositoryCustom {


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Photo findByName(String name) {
        Photo photo = null;
        Query query = entityManager.createQuery("SELECT p FROM Photo p WHERE p.name = ?1");
        query.setParameter(1, name);
        List<Photo> photos = query.getResultList();
        if (photos != null && photos.size() > 0)
            photo = photos.get(0);
        return photo;
    }



}
