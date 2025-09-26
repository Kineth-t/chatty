package com.chat_project.Chatty.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        //Creates a content type resolver that determines the MIME type of messages
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        // Sets JSON as the default MIME type for message content.
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        // Creates a Jackson-based message converter for JSON serialization/deserialization.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        //Sets a new ObjectMapper instance for the converter to handle JSON processing
        converter.setObjectMapper(new ObjectMapper());
        // Assigns the content type resolver to the message converter
        converter.setContentTypeResolver(resolver);
        // Adds the configured converter to the list of message converters
        messageConverters.add(converter);

        // Returns false to indicate that default converters should still be registered alongside the custom one
        return false;
    }
    // Without this configuration:
    // Client sends: {"message": "Hello", "user": "John"}
    // Server receives: Raw string (can't use it easily)

    // With this configuration:

    // Client sends: {"message": "Hello", "user": "John"}
    // Server receives: Automatically converted to your ChatMessage Java object
    // Your controller method can directly use: chatMessage.getMessage() and chatMessage.getUser()
}
