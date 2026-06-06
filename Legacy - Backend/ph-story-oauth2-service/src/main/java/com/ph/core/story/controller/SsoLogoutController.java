package com.ph.core.story.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ph.core.story.configuration.AuthServerProperties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SsoLogoutController {

    private final AuthServerProperties authServerProperties;

    private final LogoutHandler logoutHandler = new CompositeLogoutHandler(
            new SecurityContextLogoutHandler(),
            new CookieClearingLogoutHandler("JSESSIONID"));

    @GetMapping("/sso/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication,
            @RequestParam(value = "post_logout_redirect_uri", required = false) String redirectUri)
            throws IOException, ServletException {

        logoutHandler.logout(request, response, authentication);
        response.sendRedirect(resolveRedirectUri(redirectUri));
    }

    private String resolveRedirectUri(String requestedRedirectUri) {
        if (!StringUtils.hasText(requestedRedirectUri)) {
            return authServerProperties.getClients().getSpa().getLogoutRedirectUri();
        }

        if (!getAllowedRedirectUris().contains(requestedRedirectUri)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid post_logout_redirect_uri");
        }

        return requestedRedirectUri;
    }

    private Set<String> getAllowedRedirectUris() {
        Set<String> allowedRedirectUris = new HashSet<>();
        allowedRedirectUris.add(authServerProperties.getClients().getSpa().getLogoutRedirectUri());

        var mobile = authServerProperties.getClients().getMobile();
        if (mobile != null) {
            allowedRedirectUris.addAll(mobile.getEffectiveRedirectUris());
        }

        return allowedRedirectUris;
    }
}
