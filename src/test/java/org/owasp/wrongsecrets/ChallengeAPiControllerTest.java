package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.testutil.MockMvcTestSupport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChallengeAPiControllerTest extends MockMvcTestSupport {

  public ChallengeAPiControllerTest() {}

  @Test
  void shouldGetListOfChallenges() throws Exception {
    mvc.perform(get("/api/Challenges"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("hint")));
  }
}

/*
"manageUrl" : "url", "memo" : "memo", "channel" : "channel", "time" : "time", "additionalData" : { "srcIp" : "source", "useragent" : "agent", "referer" : "referer", "location" : "location"}}
 */
