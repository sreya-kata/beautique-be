package com.beautique.beautique.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private Object credentials;

    public JwtAuthenticationToken(Object principal, Object credentials, Collection authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true); // Marks this token as authenticated
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
