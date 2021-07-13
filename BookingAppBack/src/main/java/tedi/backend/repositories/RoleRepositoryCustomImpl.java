package tedi.backend.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.Role;
import tedi.backend.model.RoleNames;
import tedi.backend.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class RoleRepositoryCustomImpl implements RoleRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Role findByName(RoleNames name) {
        Role role = null;
        Query query = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = ?1");
        query.setParameter(1, name);
        List<Role> roles = query.getResultList();
        if (roles != null && roles.size() > 0)
            role = roles.get(0);
        return role;
    }
}
