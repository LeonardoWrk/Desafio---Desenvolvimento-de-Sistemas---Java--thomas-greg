package com.company.crs.util;

import com.company.crs.bean.AuthenticationBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for making API calls with JWT authentication
 */
@Named
@ApplicationScoped
public class ApiClient {

    private static final Logger LOGGER = Logger.getLogger(ApiClient.class.getName());
    private static final String API_BASE_URL = "http://localhost:9090/api"; // Replace with actual API URL
    
    @Inject
    private AuthenticationBean authBean;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Makes a GET request to the API with JWT authentication
     * @param endpoint API endpoint (without base URL)
     * @return JsonNode containing the response or null if error
     */
    public JsonNode get(String endpoint) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            
            // Add JWT token if available
            if (authBean != null && authBean.getJwtToken() != null) {
                connection.setRequestProperty("Authorization", "Bearer " + authBean.getJwtToken());
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return objectMapper.readTree(connection.getInputStream());
            } else {
                LOGGER.warning("API GET request failed with response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error making API GET request", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Makes a POST request to the API with JWT authentication
     * @param endpoint API endpoint (without base URL)
     * @param payload JSON payload as Map
     * @return JsonNode containing the response or null if error
     */
    public JsonNode post(String endpoint, Map<String, Object> payload) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            // Add JWT token if available
            if (authBean != null && authBean.getJwtToken() != null) {
                connection.setRequestProperty("Authorization", "Bearer " + authBean.getJwtToken());
            }
            
            // Convert payload to JSON and send
            String jsonPayload = objectMapper.writeValueAsString(payload);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return objectMapper.readTree(connection.getInputStream());
            } else {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))
                            .lines()
                            .reduce("", (acc, line) -> acc + line + "\n");
                    LOGGER.severe("API POST error response: " + errorResponse);
                }
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error making API POST request", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
    
    /**
     * Makes a PUT request to the API with JWT authentication
     * @param endpoint API endpoint (without base URL)
     * @param payload JSON payload as Map
     * @return JsonNode containing the response or null if error
     */
    public JsonNode put(String endpoint, Map<String, Object> payload) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            // Add JWT token if available
            if (authBean != null && authBean.getJwtToken() != null) {
                connection.setRequestProperty("Authorization", "Bearer " + authBean.getJwtToken());
            }
            
            // Convert payload to JSON and send
            String jsonPayload = objectMapper.writeValueAsString(payload);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return objectMapper.readTree(connection.getInputStream());
            } else {
                LOGGER.warning("API PUT request failed with response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error making API PUT request", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Makes a DELETE request to the API with JWT authentication
     * @param endpoint API endpoint (without base URL)
     * @return true if successful, false otherwise
     */
    public boolean delete(String endpoint) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            
            // Add JWT token if available
            if (authBean != null && authBean.getJwtToken() != null) {
                connection.setRequestProperty("Authorization", "Bearer " + authBean.getJwtToken());
            }
            
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error making API DELETE request", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Helper method to get the current authentication bean from session
     * Useful when CDI injection fails
     */
    private AuthenticationBean getAuthBean() {
        if (authBean != null) {
            return authBean;
        }
        
        // Try to get from session if injection failed
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
            return (AuthenticationBean) sessionMap.get("authenticationBean");
        }
        
        return null;
    }
}
