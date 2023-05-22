package org.owasp.wrongsecrets;

import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Controller used to return the dynamic data for the welcome screen. */
@Controller
@Slf4j
public class IndexController {

  private final ScoreCard scoreCard;

  private final String ctfServerAddress;

  public IndexController(
      ScoreCard scoreCard, @Value("${CTF_SERVER_ADDRESS}") String ctfServerAddress) {
    this.scoreCard = scoreCard;
    this.ctfServerAddress = ctfServerAddress;
  }

  @GetMapping("/")
  @Operation(description = "Returns all dynamic data for the welcome screen")
  public String index(Model model) {
    if ((!"not_set".equals(ctfServerAddress)) && !Strings.isNullOrEmpty(ctfServerAddress)) {
      model.addAttribute("ctfServerAddress", ctfServerAddress);
    } else {
      model.addAttribute("totalScore", scoreCard.getTotalReceivedPoints());
    }

    return "welcome";
  }
}
