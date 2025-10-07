package com.chat_project.Chatty.chatroom;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(String sender, String recipient, boolean createNewRoomIfNotExists) {
        return chatRoomRepository.findBySenderAndRecipient(sender, recipient)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        var chatId = createChatId(sender, recipient);
                        return Optional.of(chatId);
                    }

                    return Optional.empty();
                });
    }

    private String createChatId(String sender, String recipient) {
        var chatId = String.format("%s_%s", sender, recipient);
        ChatRoom senderRecipient = ChatRoom.builder()
                                    .chatId(chatId)
                                    .sender(sender)
                                    .recipient(recipient)
                                    .build();

        ChatRoom recipientSender = ChatRoom.builder()
                                    .chatId(chatId)
                                    .sender(recipient)
                                    .recipient(sender)
                                    .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);
        return chatId;
    }
}
