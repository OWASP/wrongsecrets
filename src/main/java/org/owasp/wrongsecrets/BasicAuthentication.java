package org.owasp.wrongsecrets;

public record BasicAuthentication(
    String username, String password, String role, String urlPattern) {}
