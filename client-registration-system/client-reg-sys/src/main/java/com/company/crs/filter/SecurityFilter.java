package com.company.crs.filter;

import com.company.crs.bean.AuthenticationBean;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Security filter to protect secured pages
 * Redirects unauthenticated users to the login page
 */
@WebFilter(urlPatterns = {"*.xhtml"})
public class SecurityFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class.getName());

    @Inject
    private AuthenticationBean authBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        boolean isSecuredPage = requestURI.contains("/secured/") || requestURI.endsWith("/index.xhtml");
        boolean isLoginPage = requestURI.contains("/login.xhtml");
        // Corrigido para reconhecer recursos JSF com prefixo jakarta.faces.resource
        boolean isResourceRequest = requestURI.startsWith(contextPath + "/jakarta.faces.resource/")
                                || requestURI.startsWith(contextPath + "/resources/");

        LOGGER.info("Request URI: " + requestURI);
        LOGGER.info("Is secured page: " + isSecuredPage);
        LOGGER.info("Is login page: " + isLoginPage);
        LOGGER.info("Is resource request: " + isResourceRequest);

        if (isLoginPage || isResourceRequest) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLoggedIn = false;

        if (authBean == null) {
            Object sessionAuth = httpRequest.getSession().getAttribute("authenticationBean");
            isLoggedIn = sessionAuth != null && ((AuthenticationBean) sessionAuth).isLoggedIn();
        } else {
            isLoggedIn = authBean.isLoggedIn();
        }

        LOGGER.info("Is logged in: " + isLoggedIn);

        if (isSecuredPage && !isLoggedIn) {
            LOGGER.info("Unauthorized access attempt to " + requestURI);
            httpResponse.sendRedirect(contextPath + "/login.xhtml?faces-redirect=true");
        } else if (isLoginPage && isLoggedIn) {
            httpResponse.sendRedirect(contextPath + "/index.xhtml?faces-redirect=true");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}