package org.owasp.wrongsecrets;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

/** Filter used to provide basic security headers in all cases. */
@Component
public class SecurityHeaderAddingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse res = (HttpServletResponse) response;
    res.addHeader("Server", "WrongSecrets - Star us!");
    res.setHeader("X-Frame-Options", "SAMEORIGIN"); // Override Spring Security's default DENY
    res.setHeader("X-Content-Type-Options", "nosniff");

    // Improved Content Security Policy - more restrictive than wildcard
    res.setHeader(
        "Content-Security-Policy",
        "default-src 'self'; script-src 'self' 'unsafe-inline' https://buttons.github.io"
            + " https://api.github.com; style-src 'self' 'unsafe-inline'"
            + " https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src"
            + " 'self' data: https:; connect-src 'self' https://api.github.com");

    // Add Permissions Policy header
    res.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

    // Add cache control headers to prevent caching of sensitive content
    res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    res.setHeader("Pragma", "no-cache");
    res.setHeader("Expires", "0");

    chain.doFilter(request, res);
  }
}
