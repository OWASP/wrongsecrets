package org.owasp.wrongsecrets.testutil;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class MockMvcTestSupport {
  @Autowired private WebApplicationContext context;
  protected MockMvc mvc;

  @BeforeEach
  void setUpMockMvc() {
    this.mvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
  }
}
