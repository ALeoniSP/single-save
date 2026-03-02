package com.aleonisp.singlesave.service;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

final class OidcUserFields {

    private OidcUserFields() {}

    static String email(OidcUser user) {
        Object v = user.getAttributes().get("email");
        return v == null ? null : String.valueOf(v);
    }

    static String displayName(OidcUser user) {
        Object v = user.getAttributes().get("name");
        if (v != null) {
            String s = String.valueOf(v).trim();
            if (!s.isBlank()) {
                return s;
            }
        }
        String fallback = user.getFullName();
        return fallback == null ? null : fallback.trim();
    }
}