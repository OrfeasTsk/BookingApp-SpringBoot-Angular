package tedi.backend.repositories;

import tedi.backend.model.Photo;

import java.util.List;

public interface PhotoRepositoryCustom {

    Photo findByName(String name);


}
