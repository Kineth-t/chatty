package com.chat_project.Chatty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enables a simple in-memory message broker that will handle messages sent to destinations starting with "/user".
        registry.enableSimpleBroker("/user");

        // Sets the prefix for messages that are bound for methods annotated with @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");

        // Sets the prefix for user-specific destinations (for sending messages to specific users)
        registry.setUserDestinationPrefix(("/user"));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Adds a WebSocket endpoint at "/ws" path where clients can establish WebSocket connections
        registry.addEndpoint("/ws")
                .withSockJS();
    }
}
