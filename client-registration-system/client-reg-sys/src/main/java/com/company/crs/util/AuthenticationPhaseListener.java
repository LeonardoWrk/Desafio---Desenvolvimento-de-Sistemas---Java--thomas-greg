package com.company.crs.util;

import com.company.crs.bean.AuthenticationBean;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

import java.util.logging.Logger;

/**
 * Phase listener to handle authentication checks during JSF lifecycle
 */
public class AuthenticationPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationPhaseListener.class.getName());

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        
        // Skip authentication check for login page and resources
        String viewId = facesContext.getViewRoot().getViewId();
        if (viewId.contains("/login.xhtml") || viewId.contains("/javax.faces.resource/")) {
            return;
        }
        
        // Check if user is authenticated
        AuthenticationBean authBean = facesContext.getApplication()
                .evaluateExpressionGet(facesContext, "#{authenticationBean}", AuthenticationBean.class);
        
        if (authBean == null || !authBean.isLoggedIn()) {
            LOGGER.info("Unauthenticated access attempt to " + viewId);
            
            // Redirect to login page
            NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
            navigationHandler.handleNavigation(facesContext, null, "login?faces-redirect=true");
            
            // Skip rendering phase
            facesContext.renderResponse();
        } else if (viewId.contains("/secured/") && authBean.getCurrentUser() != null) {
            // Check if token is expired (if we had a JwtUtil bean)
            // This is just a placeholder for additional security checks
            LOGGER.fine("Authenticated access to secured page: " + viewId);
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        // Not used
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
