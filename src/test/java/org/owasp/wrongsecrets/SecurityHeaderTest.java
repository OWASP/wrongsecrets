package org.owasp.wrongsecrets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"K8S_ENV=k8s"})
@AutoConfigureMockMvc
class SecurityHeaderTest {

  @Autowired private MockMvc mvc;

  @Test
  void shouldHaveXFrameOptionsHeader() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
  }

  @Test
  void shouldHaveXContentTypeOptionsHeader() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Content-Type-Options", "nosniff"));
  }

  @Test
  void shouldHaveContentSecurityPolicyHeader() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Content-Security-Policy"));
  }

  @Test
  void shouldHavePermissionsPolicyHeader() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(
            header().string("Permissions-Policy", "geolocation=(), microphone=(), camera=()"));
  }

  @Test
  void shouldHaveCacheControlHeaders() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"));
  }

  @Test
  void shouldNotHaveWildcardInCSP() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(
            result -> {
              String csp = result.getResponse().getHeader("Content-Security-Policy");
              if (csp != null && csp.contains("default-src *")) {
                throw new AssertionError(
                    "CSP should not contain wildcard directive 'default-src *'");
              }
            });
  }
}
