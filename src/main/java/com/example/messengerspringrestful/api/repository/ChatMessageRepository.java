package com.example.messengerspringrestful.api.repository;

import com.example.messengerspringrestful.api.domain.model.ChatMessage;
import com.example.messengerspringrestful.api.domain.model.MessageStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);

    void deleteByChatId(String chatId);

    void deleteBySenderIdAndRecipientId(String senderId, String recipientId);

}
