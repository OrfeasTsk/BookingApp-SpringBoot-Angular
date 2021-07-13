package tedi.backend.repositories;

import tedi.backend.model.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getReceivedMessages(Long userId, Long depId);
    List<Message> getSentMessages(Long userId, Long depId);
}
