package tedi.backend.repositories;

import tedi.backend.model.RoleNames;
import tedi.backend.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public User findByUsername(String username) {
        User user = null;
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = ?1");
        query.setParameter(1, username);
        List<User> users = query.getResultList();
        if (users != null && users.size() > 0)
            user = users.get(0);
        return user;
    }

    @Override
    public Boolean UsernameExists(String username) {

        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = ?1");
        query.setParameter(1, username);
        List<User> users = query.getResultList();
        if (users != null && users.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Boolean EmailExists(String email) {

        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = ?1");
        query.setParameter(1, email);
        List<User> users = query.getResultList();
        if (users != null && users.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public List<User> findByRole(RoleNames name){
        Query query = entityManager.createQuery("SELECT u FROM User u JOIN u.roles r WHERE r.name = ?1");
        query.setParameter(1, name);
        List<User> users = query.getResultList();
        return users;
    }
}
