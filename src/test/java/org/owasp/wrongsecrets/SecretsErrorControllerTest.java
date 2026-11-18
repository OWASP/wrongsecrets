package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecretsErrorControllerTest {

  static {
    // Set default locale to English for all tests
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void ensureLocaleIsEnglish() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Autowired private MockMvc mvc;

  @Test
  void shouldReturnErrorPage() throws Exception {
    mvc.perform(get("/error"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("WHOOOOPS")))
        .andExpect(content().string(containsString("Something went wrong!")));
  }
}
