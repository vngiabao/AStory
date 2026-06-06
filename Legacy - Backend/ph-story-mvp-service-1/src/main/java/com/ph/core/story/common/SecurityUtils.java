package com.ph.core.story.common;

import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    public static Long getUserIdFromJwt(Jwt jwt) {
        // Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object claim = jwt.getClaim("user_id");
        if (claim == null) {
            throw new IllegalStateException("JWT missing user_id claim");
        }
        return claim instanceof Number ? ((Number) claim).longValue()
                : Long.valueOf(claim.toString());
    }
}
