package com.chat_project.Chatty.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @MessageMapping("/user.addUser") // WebSocket listening on user.addUser
    @SendTo("/user/topic") // Sends the resulting user object to all subscribers of /user/topic
    public User addUser(@Payload User user) { // Deserialises the WebSocket message payload into a User object
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public User disconnect(@Payload User user) {
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        // Responds with HTTP 200 (OK) and the list of users
        return ResponseEntity.ok(userService.findConnectedUser());
    } 
    
}

// Client sends a message to /app/user.addUser → User is saved → Broadcasted to /user/topic.
// Client sends a message to /app/user.disconnectUser → User is marked disconnected → Broadcasted to /user/topic.