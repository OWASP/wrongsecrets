package org.owasp.wrongsecrets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Tests that {@link ApiExceptionAdvice} returns RFC 9457-style {@code ProblemDetail} payloads. */
class ApiExceptionAdviceTest {

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc =
        MockMvcBuilders.standaloneSetup(new TestRestController())
            .setControllerAdvice(new ApiExceptionAdvice())
            .build();
  }

  @Test
  void shouldReturnProblemDetailWithRfc9457FieldsForResponseStatusException() throws Exception {
    mvc.perform(get("/test/not-found").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").exists())
        .andExpect(jsonPath("$.detail").exists())
        .andExpect(jsonPath("$.instance").exists());
  }

  @Test
  void shouldReturnProblemDetailWithRfc9457FieldsForGenericException() throws Exception {
    mvc.perform(get("/test/error").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.title").value("Internal Server Error"))
        .andExpect(jsonPath("$.detail").exists())
        .andExpect(jsonPath("$.instance").exists());
  }

  @RestController
  static class TestRestController {

    @GetMapping("/test/not-found")
    public String notFound() {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
    }

    @GetMapping("/test/error")
    public String error() {
      throw new RuntimeException("Unexpected failure");
    }
  }
}
