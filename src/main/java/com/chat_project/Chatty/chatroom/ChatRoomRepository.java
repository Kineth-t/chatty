package com.chat_project.Chatty.chatroom;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderAndRecipient(String sender, String recipient);
}
