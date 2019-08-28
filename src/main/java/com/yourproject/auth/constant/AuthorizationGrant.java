package com.yourproject.auth.constant;

public final class AuthorizationGrant {

    private AuthorizationGrant() {}

    public static final String SCOPE_WRITE = "#oauth2.hasScope('SPRING_BOOT_WRITE')";
    public static final String SCOPE_RESOURCE_SERVICE = "#oauth2.hasScope('SPRING_BOOT_RESOURCE_SERVICE')";

    public static final String AUTHORITY_ADMIN = "hasAuthority('ADMIN')";
    public static final String AUTHORITY_USER = "hasAuthority('USER')";
}
