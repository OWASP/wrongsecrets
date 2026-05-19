package org.owasp.wrongsecrets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

/** Filter used to provide basic security headers in all cases. */
@Component
public class SecurityHeaderAddingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (response instanceof HttpServletResponse httpServletResponse) {
      httpServletResponse.setHeader("Server", "WrongSecrets - Star us!"); // NOSONAR
      httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
      httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
      httpServletResponse.setHeader(
          "Content-Security-Policy",
          "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self'"
              + " 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'self'; object-src"
              + " 'none'; base-uri 'self'");
    }
    chain.doFilter(request, response);
  }
}
