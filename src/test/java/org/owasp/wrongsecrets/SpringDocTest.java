package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class SpringDocTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired RequestMappingHandlerMapping requestMappingHandlerMapping;

  @Test
  void shouldRedirectToSwaggerUiPage() throws Exception {
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/swagger-ui/index.html"));
  }

  @Test
  void shouldDisplaySwaggerUiPage() throws Exception {
    mockMvc
        .perform(get("/swagger-ui/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Swagger UI")));
  }

  @Test
  void getApiDocs() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.openapi", is("3.0.1")))
        .andExpect(jsonPath("$.info", isA(Object.class)))
        .andExpect(jsonPath("$.servers", isA(Object.class)))
        .andExpect(jsonPath("$.paths", isA(Object.class)))
        .andExpect(jsonPath("$.components", isA(Object.class)))
        .andReturn();
  }

  @Test
  void endpointsPresent() throws Exception {
    String json =
        mockMvc
            .perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OpenAPI openAPI = Yaml.mapper().readValue(json, OpenAPI.class);
    List<String> ownEndpoints =
        requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
            .filter(
                entry ->
                    entry
                        .getValue()
                        .getBeanType()
                        .getPackageName()
                        .startsWith("org.owasp.wrongsecrets"))
            .map(Map.Entry::getKey)
            .map(r -> r.getPathPatternsCondition().getFirstPattern().getPatternString())
            .toList();

    Assertions.assertThat(ownEndpoints).hasSizeGreaterThan(1);
    ownEndpoints.forEach(
        path -> {
          if (!path.equals("/spoil-{id}") && !path.equals("/hidden")) { // this one is hidden
            log.info("Checking for path to be present in OpenAPI spec: {}", path);
            Assertions.assertThat(openAPI.getPaths().get(path)).isNotNull();
          }
        });
  }
}
