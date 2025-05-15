package com.company.crs.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import java.util.ResourceBundle;

/**
 * Utility class for JSF operations
 */
public class FacesUtils {

    /**
     * Adds an info message to the current FacesContext
     * 
     * @param message The message to display
     */
    public static void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    /**
     * Adds an error message to the current FacesContext
     * 
     * @param message The error message to display
     */
    public static void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    /**
     * Adds a warning message to the current FacesContext
     * 
     * @param message The warning message to display
     */
    public static void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
    }
    
    /**
     * Gets a message from the resource bundle
     * 
     * @param key The key of the message
     * @return The message from the resource bundle
     */
    public static String getMessage(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication()
                .getResourceBundle(context, "msg");
        return bundle.getString(key);
    }
    
    /**
     * Gets the Flash context to pass messages between redirects
     * 
     * @return The Flash context
     */
    public static Flash getFlash() {
        return FacesContext.getCurrentInstance().getExternalContext().getFlash();
    }
    
    /**
     * Puts a message in the Flash context to survive redirects
     * 
     * @param key The key for the message
     * @param value The value to store
     */
    public static void putFlashMessage(String key, Object value) {
        getFlash().put(key, value);
    }
}
