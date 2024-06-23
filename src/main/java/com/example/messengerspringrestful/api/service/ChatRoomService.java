package com.example.messengerspringrestful.api.service;

import com.example.messengerspringrestful.api.domain.model.ChatRoom;
import com.example.messengerspringrestful.api.repository.ChatRoomRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервисный класс для работы с чат-комнатами.
 * Обеспечивает создание и поиск чат-комнат между пользователями.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRoomService {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    /**
     * Получает идентификатор чат-комнаты между отправителем и получателем.
     * Если чат-комната не существует и флаг createIfNotExist равен true,
     * создает новую чат-комнату.
     *
     * @param senderId       Идентификатор отправителя.
     * @param recipientId    Идентификатор получателя.
     * @param createIfNotExist Флаг, указывающий на необходимость создания чат-комнаты, если она не существует.
     * @return Optional с идентификатором чат-комнаты или пустой Optional, если чат-комната не найдена и не создана.
     */
    public Optional<String> getChatId(
            String senderId, String recipientId, boolean createIfNotExist) {

        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createIfNotExist) {
                        return  Optional.empty();
                    }
                    var chatId =
                            String.format("%s_%s", senderId, recipientId);

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();
                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }
}
