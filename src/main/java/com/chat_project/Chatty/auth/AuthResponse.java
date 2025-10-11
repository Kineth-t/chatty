package com.chat_project.Chatty.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String username;
    private String email;
    private String message;
    
    // Internal use only (not sent to frontend)
    private String token;
}