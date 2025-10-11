package com.chat_project.Chatty.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract any claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate token with just UserDetails
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate token with extra claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // Build the JWT token
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder() // Starts building a new JWT
                .claims(extraClaims) // Adds extra key-value data into the token
                .subject(userDetails.getUsername()) // Sets the subject field (useing username)
                .issuedAt(new Date(System.currentTimeMillis())) // Adds the timestamp when the token was created
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Adds expiration date/time
                .signWith(getSignInKey()) // Signs the JWT using the HMAC key derived from your secret
                .compact(); // Finalises and returns the token as a compact String
    }

    // Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts
            .parser() // Creates a parser instance
            .verifyWith(getSignInKey()) // Ensures the signature is valid
            .build() // Builds the parser
            .parseSignedClaims(token) // Parses the JWT string
            .getPayload(); // Retrieves the claims body
    }

    // Get signing key from secret
    private SecretKey getSignInKey() {
        // Converts your secret string into a SecretKey for HMAC-SHA signing
        // Keys.hmacShaKeyFor(...) ensures the key is correctly formatted and strong enough
        // UTF-8 encoding is used to convert the secret text into bytes
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}