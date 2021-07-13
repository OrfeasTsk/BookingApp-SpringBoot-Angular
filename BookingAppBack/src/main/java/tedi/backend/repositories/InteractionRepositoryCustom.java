package tedi.backend.repositories;

import tedi.backend.model.Interaction;

public interface InteractionRepositoryCustom {

    Interaction getInteraction(Long depId,Long userId);

}
