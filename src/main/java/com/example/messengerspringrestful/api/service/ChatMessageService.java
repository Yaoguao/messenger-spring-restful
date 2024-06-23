package com.example.messengerspringrestful.api.service;

import com.example.messengerspringrestful.api.domain.model.ChatMessage;
import com.example.messengerspringrestful.api.domain.model.MessageStatus;
import com.example.messengerspringrestful.api.exception.ResourceNotFoundException;
import com.example.messengerspringrestful.api.repository.ChatMessageRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для работы с сообщениями чата.
 * Обеспечивает сохранение, поиск и удаление сообщений чата,
 * а также управление их статусами.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageService {

    @Autowired
    ChatMessageRepository repository;

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    MongoOperations mongoOperations;

    /**
     * Сохраняет сообщение чата.
     *
     * @param chatMessage Сохраняемое сообщение чата.
     * @return Сохраненное сообщение чата.
     */
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    /**
     * Возвращает количество новых сообщений между отправителем и получателем.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     * @return Количество новых сообщений.
     */
    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    /**
     * Возвращает список сообщений чата между отправителем и получателем.
     * Обновляет статусы сообщений на DELIVERED, если найдены сообщения.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     * @return Список сообщений чата.
     */
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {

        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);

        List<ChatMessage> messages = chatId.map(cId -> repository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;
    }

    /**
     * Находит сообщение чата по его идентификатору и обновляет его статус на DELIVERED.
     *
     * @param id Идентификатор сообщения чата.
     * @return Обновленное сообщение чата.
     * @throws ResourceNotFoundException если сообщение не найдено.
     */
    public ChatMessage findById(String id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("can't find message (" + id + ")"));
    }


    /**
     * Обновляет статусы всех сообщений чата между отправителем и получателем.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     * @param status      Новый статус для обновления.
     */
    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Query query = new Query(
                Criteria
                        .where("senderId").is(senderId)
                        .and("recipientId").is(recipientId));
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }


    /**
     * Удаляет сообщение чата по его идентификатору.
     *
     * @param id Идентификатор сообщения чата для удаления.
     */
    public void deleteById(String id) {
        repository.deleteById(id);
    }


    /**
     * Удаляет все сообщения чата с указанным идентификатором чат-комнаты.
     *
     * @param chatId Идентификатор чат-комнаты.
     */
    public void deleteByChatId(String chatId) {
        repository.deleteByChatId(chatId);
    }


    /**
     * Удаляет все сообщения чата между указанными отправителем и получателем.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     */
    public void deleteBySenderIdAndRecipientId(String senderId, String recipientId) {
        repository.deleteBySenderIdAndRecipientId(senderId, recipientId);
    }

    /**
     * Изменяет содержимое сообщения чата по его идентификатору.
     *
     * @param id      Идентификатор сообщения чата для изменения.
     * @param content Новое содержимое сообщения.
     * @return Обновленное сообщение чата.
     */
    public ChatMessage changeById(String id, String content) {
        System.out.println("Content: " + content);
        ChatMessage chatMessage = repository.findById(id).orElseThrow();
        chatMessage.setContent(content);
        return repository.save(chatMessage);
    }
}
