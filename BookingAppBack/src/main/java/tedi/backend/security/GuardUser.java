package tedi.backend.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tedi.backend.model.Role;
import tedi.backend.model.User;
import tedi.backend.repositories.UserRepositoryCustomImpl;

import java.util.Set;


@Component
public class GuardUser {

    @Autowired
    UserRepositoryCustomImpl userRepository;

    public boolean checkUserId(Authentication authentication, int id) {
        String name = authentication.getName();
        User result = userRepository.findByUsername(name);

        return result != null && result.getId() == id;
    }



}
