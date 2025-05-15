package com.company.crs.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for JWT token operations
 * Note: This is a simplified implementation for demonstration purposes.
 * In a production environment, consider using a proper JWT library.
 */
@Named
@ApplicationScoped
public class JwtUtil {

    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Extracts the payload from a JWT token
     * @param token JWT token
     * @return JsonNode containing the payload or null if invalid
     */
    public JsonNode extractPayload(String token) {
        if (token == null || token.isEmpty() || !token.contains(".")) {
            return null;
        }
        
        try {
            // Split the token into parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readTree(payload);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error extracting JWT payload", e);
            return null;
        }
    }
    
    /**
     * Extracts the username from a JWT token
     * @param token JWT token
     * @return username or null if not found
     */
    public String extractUsername(String token) {
        try {
            JsonNode payload = extractPayload(token);
            if (payload != null) {
                // Check common username claim names
                if (payload.has("sub")) {
                    return payload.get("sub").asText();
                } else if (payload.has("username")) {
                    return payload.get("username").asText();
                } else if (payload.has("email")) {
                    return payload.get("email").asText();
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error extracting username from JWT", e);
            return null;
        }
    }
    
    /**
     * Extracts roles from a JWT token
     * @param token JWT token
     * @return List of roles or empty list if none found
     */
    public List<String> extractRoles(String token) {
        List<String> roles = new ArrayList<>();
        try {
            JsonNode payload = extractPayload(token);
            if (payload != null) {
                // Check common role claim names
                JsonNode rolesNode = null;
                if (payload.has("roles")) {
                    rolesNode = payload.get("roles");
                } else if (payload.has("authorities")) {
                    rolesNode = payload.get("authorities");
                } else if (payload.has("scope")) {
                    rolesNode = payload.get("scope");
                }
                
                if (rolesNode != null) {
                    if (rolesNode.isArray()) {
                        for (JsonNode role : rolesNode) {
                            roles.add(role.asText());
                        }
                    } else if (rolesNode.isTextual()) {
                        // Handle space-separated roles in a string
                        String[] roleArray = rolesNode.asText().split("\\s+");
                        for (String role : roleArray) {
                            roles.add(role);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error extracting roles from JWT", e);
        }
        return roles;
    }
    
    /**
     * Checks if a JWT token is expired
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            JsonNode payload = extractPayload(token);
            if (payload != null && payload.has("exp")) {
                long expiration = payload.get("exp").asLong();
                long currentTimeSeconds = System.currentTimeMillis() / 1000;
                return expiration < currentTimeSeconds;
            }
            return true; // If no expiration claim, consider it expired
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking JWT expiration", e);
            return true;
        }
    }
}
