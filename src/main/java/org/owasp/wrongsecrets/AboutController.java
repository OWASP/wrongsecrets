package org.owasp.wrongsecrets;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** About controler hosting /about endpoint. */
@Controller
public class AboutController {

  @GetMapping("/about")
  @Operation(description = "Endpoint to get dynamic data on about")
  public String getStats() {
    return "about";
  }
}
