package com.company.crs.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Bean for controlling the application layout
 */
@Named
@ApplicationScoped
public class LayoutBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String appName = "Client Registration System";
    private String appVersion = "1.0";
    private String theme = "saga";
    private boolean sidebarCollapsed = false;
    
    /**
     * Gets the application name
     * 
     * @return The application name
     */
    public String getAppName() {
        return appName;
    }
    
    /**
     * Gets the application version
     * 
     * @return The application version
     */
    public String getAppVersion() {
        return appVersion;
    }
    
    /**
     * Gets the current theme
     * 
     * @return The current theme
     */
    public String getTheme() {
        return theme;
    }
    
    /**
     * Sets the current theme
     * 
     * @param theme The theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    /**
     * Checks if the sidebar is collapsed
     * 
     * @return True if the sidebar is collapsed, false otherwise
     */
    public boolean isSidebarCollapsed() {
        return sidebarCollapsed;
    }
    
    /**
     * Sets whether the sidebar is collapsed
     * 
     * @param sidebarCollapsed True to collapse the sidebar, false otherwise
     */
    public void setSidebarCollapsed(boolean sidebarCollapsed) {
        this.sidebarCollapsed = sidebarCollapsed;
    }
    
    /**
     * Toggles the sidebar collapsed state
     */
    public void toggleSidebar() {
        this.sidebarCollapsed = !this.sidebarCollapsed;
    }
}
