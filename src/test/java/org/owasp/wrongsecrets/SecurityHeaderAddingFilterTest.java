package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SecurityHeaderAddingFilterTest {

  private final SecurityHeaderAddingFilter filter = new SecurityHeaderAddingFilter();

  @Test
  void shouldAddSecurityHeadersForHttpResponses() throws Exception {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var chain = mock(FilterChain.class);

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader("X-Frame-Options")).isEqualTo("SAMEORIGIN");
    assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
    assertThat(response.getHeader("Content-Security-Policy"))
        .isEqualTo(
            "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self'"
                + " 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'self'; object-src"
                + " 'none'; base-uri 'self'");
    assertThat(response.getHeader("Server")).isEqualTo("WrongSecrets - Star us!");
    verify(chain).doFilter(request, response);
  }

  @Test
  void shouldPassThroughNonHttpResponses() throws Exception {
    var request = mock(ServletRequest.class);
    var response = mock(ServletResponse.class);
    var chain = mock(FilterChain.class);

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }
}
