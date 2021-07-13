package tedi.backend.repositories;


import tedi.backend.model.RoleNames;
import tedi.backend.model.User;

import java.util.List;

public interface UserRepositoryCustom {
    User findByUsername(String username);
    Boolean UsernameExists(String username);
    Boolean EmailExists(String email);
    List<User> findByRole(RoleNames name);
}
