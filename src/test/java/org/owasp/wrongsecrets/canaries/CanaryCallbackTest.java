package org.owasp.wrongsecrets.canaries;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CanaryCallbackTest {
  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldAcceptPostOfMessage() throws Exception {
    var additonalCanaryData = new AdditionalCanaryData("source", "agent", "referer", "location");
    var canaryToken = new CanaryToken("url", "memo", "channel", "time", additonalCanaryData);

    mvc.perform(
            post("/canaries/tokencallback")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(canaryToken)))
        .andExpect(status().isAccepted());
  }
}

/*
"manageUrl" : "url", "memo" : "memo", "channel" : "channel", "time" : "time", "additionalData" : { "srcIp" : "source", "useragent" : "agent", "referer" : "referer", "location" : "location"}}
 */
