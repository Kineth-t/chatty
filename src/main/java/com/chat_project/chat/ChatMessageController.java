package com.chat_project.chat;

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
    private final SimpMessagingTemplate template;

    @GetMapping("/messages/{sender}/{recipient}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String sender, @PathVariable String recipient) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(sender, recipient));
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        // john/queue/messages
        template.convertAndSendToUser(savedMessage.getRecipient(), "/queue/messages", 
            ChatNotification.builder()
                .id(savedMessage.getId())
                .sender(savedMessage.getSender())
                .recipient(savedMessage.getRecipient())
                .content(savedMessage.getContent())
                .build()
        );
    }
    
}
