package tedi.backend.repositories;

import tedi.backend.model.Role;
import tedi.backend.model.RoleNames;

public interface RoleRepositoryCustom {
    Role findByName(RoleNames name);
}
