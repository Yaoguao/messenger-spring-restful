package com.example.messengerspringrestful.api.controller.ws;

import com.example.messengerspringrestful.api.domain.model.ChatMessage;
import com.example.messengerspringrestful.api.domain.model.ChatNotification;
import com.example.messengerspringrestful.api.service.ChatMessageService;
import com.example.messengerspringrestful.api.service.ChatRoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для обработки запросов, связанных с чатом и сообщениями.
 * Обрабатывает сохранение сообщений, подсчет новых сообщений,
 * поиск сообщений, удаление сообщений и изменение содержимого сообщений.
 */
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    SimpMessagingTemplate messagingTemplate;

    ChatMessageService chatMessageService;

    ChatRoomService chatRoomService;

    public static final String CHAT = "/chat";
    public static final String COUNT_MESSAGES = "/messages/{senderId}/{recipientId}/count";
    public static final String FIND_MESSAGE_TO_ALL = "/messages/{senderId}/{recipientId}";
    public static final String FIND_MESSAGE = "/messages/{id}";

    public static final String DELETE_MESSAGE_BY_ID = "/messages/{id}";
    public static final String DELETE_MESSAGES_BY_CHATID = "/messages/{chatId}/clear";
    public static final String DELETE_MESSAGES_BY_SENDERID_AND_RECIPIENTID = "/messages/{senderId}/{recipientId}";

    public static final String CHANGE_MESSAGE_BY_ID = "/messages/{id}";


    /**
     * Обрабатывает входящие сообщения чата.
     * Сохраняет сообщение, определяет чат-идентификатор и отправляет уведомление получателю.
     *
     * @param chatMessage Входящее сообщение чата.
     */
    @MessageMapping(CHAT)
    public void processMessage(@Payload ChatMessage chatMessage) {

        Optional<String> chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);

        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()));
    }

    /**
     * Возвращает количество новых сообщений между отправителем и получателем.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     * @return Количество новых сообщений.
     */
    @GetMapping(COUNT_MESSAGES)
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    /**
     * Возвращает список сообщений чата между отправителем и получателем.
     *
     * @param senderId    Идентификатор отправителя.
     * @param recipientId Идентификатор получателя.
     * @return Список сообщений чата.
     */
    @GetMapping(FIND_MESSAGE_TO_ALL)
    public ResponseEntity<?> findChatMessages ( @PathVariable String senderId,
                                                @PathVariable String recipientId ) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    /**
     * Лень
     * @param id
     * @return
     */
    @GetMapping(FIND_MESSAGE)
    public ResponseEntity<?> findMessage ( @PathVariable String id ) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }

    @DeleteMapping(DELETE_MESSAGE_BY_ID)
    public ResponseEntity<?> deleteMessage( @PathVariable String id ) {
        chatMessageService.deleteById(id);
        return ResponseEntity
                .ok("Message " + id + ": delete");
    }

    @DeleteMapping(DELETE_MESSAGES_BY_CHATID)
    public ResponseEntity<?> deleteMessages( @PathVariable String chatId ) {
        chatMessageService.deleteByChatId(chatId);
        return ResponseEntity
                .ok("Messages " + chatId + ": delete");
    }

    @DeleteMapping(DELETE_MESSAGES_BY_SENDERID_AND_RECIPIENTID)
    public ResponseEntity<?> deleteBySenderIdAndRecipientId( @PathVariable String senderId,
                                                             @PathVariable String recipientId ) {

        chatMessageService.deleteBySenderIdAndRecipientId(senderId, recipientId);

        return ResponseEntity
                .ok("Messages " + senderId + " " + recipientId + ": delete");
    }

    @PutMapping(CHANGE_MESSAGE_BY_ID)
    public ResponseEntity<?> changeById (@PathVariable String id,
                                         @RequestBody String content ) {


        return ResponseEntity.ok(chatMessageService.changeById(id, content));
    }

}
