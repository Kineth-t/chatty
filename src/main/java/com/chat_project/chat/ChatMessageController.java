package com.chat_project.chat;

import java.net.http.WebSocket;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate template; // Used to send messages via WebSocket to specific users


    @GetMapping("/messages/{sender}/{recipient}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String sender, @PathVariable String recipient) {
        // Returns the list of messages exchanged between sender and recipient
        return ResponseEntity.ok(chatMessageService.findChatMessages(sender, recipient));
    }

    @MessageMapping("/chat") // Maps to: /app/chat
    public void processMessage(@Payload ChatMessage chatMessage) {
        // Save the incoming message to the database
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // Send a notification to the recipient user via WebSocket
        // The recipient listens on /user/{username}/queue/messages
        template.convertAndSendToUser(savedMessage.getRecipient(), "/queue/messages", 
            ChatNotification.builder()
                .id(savedMessage.getId())
                .sender(savedMessage.getSender())
                .recipient(savedMessage.getRecipient())
                .content(savedMessage.getContent())
                .build()
        );
    }
    // User A sends a message to /app/chat (WebSocket endpoint).
    // processMessage() saves the message and notifies User B.
    // User B listens to /user/B/queue/messages to receive messages in real-time.
    // When a chat is loaded, the frontend calls /messages/A/B (HTTP GET) to fetch previous messages.
}
// Frontend example
// Subscription:
// stompClient.subscribe('/user/username/queue/messages', (message) => {
//     const notification = JSON.parse(message.body);
//     // display notification or message
// });

// Sending message
// stompClient.send("/app/chat", {}, JSON.stringify({
//     sender: "john",
//     recipient: "jane",
//     content: "Hello Jane!"
// }));