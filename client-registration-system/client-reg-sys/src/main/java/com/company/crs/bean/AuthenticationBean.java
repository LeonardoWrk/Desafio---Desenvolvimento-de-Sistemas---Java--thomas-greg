package com.company.crs.bean;

import com.company.crs.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Session scoped bean to handle authentication and maintain user session
 */
@Named
@SessionScoped
public class AuthenticationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationBean.class.getName());

    // API endpoint for authentication
    private static final String AUTH_API_URL = "http://localhost:9090/api/auth/login"; // Replace with actual API URL

    private String username;
    private String password;
    private boolean rememberMe;
    private User currentUser;


    public String getSessionId() {
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
        .getExternalContext().getSession(false);
    return (session != null) ? session.getId() : "No session";
    }

    /**
     * Gets the session creation time as Date object
     * @return Date representing when the session was created or null if no session
     */
    public Date getSessionCreationTime() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return (session != null) ? new Date(session.getCreationTime()) : null;
    }

    /**
     * Gets the session timeout in seconds
     * @return Session timeout in seconds or 0 if no session
     */
    public int getSessionTimeout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        return (session != null) ? session.getMaxInactiveInterval() : 0;
    }

    /**
     * Attempts to authenticate the user with the provided credentials
     * @return navigation outcome
     */
    public String login() {
        try {
            String jwtToken = callAuthApi();

            if (jwtToken != null && !jwtToken.isEmpty()) {
                currentUser = new User(username, jwtToken);
                currentUser.addRole("USER");
                password = null;

                // Store bean in session for filter access
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("authenticationBean", this);

                addMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Welcome " + username);
                return "index?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials");
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Logs out the current user and invalidates the session
     * @return navigation outcome
     */
    public String logout() {
        try {
            currentUser = null;
            username = null;
            password = null;

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.invalidateSession();
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao redirecionar após logout", e);
        }
        return null;
    }

    /**
     * Calls the authentication API to validate credentials and get JWT token
     * @return JWT token if authentication successful, null otherwise
     */
    private String callAuthApi() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(AUTH_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(connection.getInputStream());

                if (rootNode.has("accessToken")) {
                    return rootNode.get("accessToken").asText();
                } else {
                    LOGGER.warning("Token não encontrado na resposta da API");
                    return null;
                }
            } else {
                LOGGER.warning("Falha na autenticação com código de resposta: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao chamar API de autenticação", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Helper method to add faces messages
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    /**
     * Checks if user is logged in
     * @return true if user is authenticated
     */
    public boolean isLoggedIn() {
        boolean loggedIn = currentUser != null && currentUser.getJwtToken() != null;
        return loggedIn;
    }

    /**
     * Gets JWT token for API calls
     * @return JWT token or null if not authenticated
     */
    public String getJwtToken() {
        return currentUser != null ? currentUser.getJwtToken() : null;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}