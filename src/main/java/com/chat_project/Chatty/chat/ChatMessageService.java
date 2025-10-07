package com.chat_project.Chatty.chat;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chat_project.Chatty.chatroom.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(chatMessage.getSender(), chatMessage.getRecipient(), true)
                        .orElseThrow(); // Throw custom exception

        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String sender, String recipient) {
        var chatId = chatRoomService.getChatRoomId(sender, recipient, false)
                        .orElseThrow(); // Throw custom exception

        return repository.findByChatId(chatId);
    }
}
