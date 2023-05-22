package org.owasp.wrongsecrets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

  private MockMvc mvc;
  @Mock private ScoreCard scoreCard;

  @Test
  void disableScoringWhenAddressIsSet() throws Exception {
    var controller = new IndexController(scoreCard, "address");
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("ctfServerAddress"))
        .andExpect(model().attributeDoesNotExist("totalScore"));
  }

  @Test
  void enableScoringWhenAddressIsNotSet() throws Exception {
    var controller = new IndexController(scoreCard, "not_set");
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(model().attributeDoesNotExist("ctfServerAddress"))
        .andExpect(model().attributeExists("totalScore"));
  }

  @Test
  void shouldShowScoreAndChallengeWhenCompleted() throws Exception {
    when(scoreCard.getTotalReceivedPoints()).thenReturn(1000);
    var controller = new IndexController(scoreCard, "not_set");
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("totalScore", 1000));
  }
}
