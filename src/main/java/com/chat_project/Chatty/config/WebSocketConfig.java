package com.chat_project.Chatty.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructor Injection
    public WebSocketConfig(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for both /user and /topic destinations
        registry.enableSimpleBroker("/user", "/topic");
        
        // Prefix for @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
        
        // Prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint with CORS support
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173") // Allow all origins for development
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwt = null;
                    
                    // Try to get token from Authorization header
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    if (authorization != null && !authorization.isEmpty()) {
                        String authHeader = authorization.get(0);
                        if (authHeader.startsWith("Bearer ")) {
                            jwt = authHeader.substring(7);
                        }
                    }
                    
                    // Fallback: Try to get from cookie header
                    if (jwt == null) {
                        List<String> cookies = accessor.getNativeHeader("Cookie");
                        if (cookies != null && !cookies.isEmpty()) {
                            String cookieHeader = cookies.get(0);
                            String[] cookieArray = cookieHeader.split("; ");
                            for (String cookie : cookieArray) {
                                if (cookie.startsWith("token=")) {
                                    jwt = cookie.substring(6);
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (jwt != null) {
                        try {
                            String username = jwtService.extractUsername(jwt);
                            
                            if (username != null) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                if (jwtService.isTokenValid(jwt, userDetails)) {
                                    UsernamePasswordAuthenticationToken authentication = 
                                        new UsernamePasswordAuthenticationToken(
                                            userDetails, 
                                            null, 
                                            userDetails.getAuthorities()
                                        );
                                    accessor.setUser(authentication);
                                }
                            }
                        } catch (Exception e) {
                            // Invalid token, continue without authentication
                        }
                    }
                }
                
                return message;
            }
        });
    }
}