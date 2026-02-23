package org.owasp.wrongsecrets;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Controller used to generate content for the error page. */
@Controller
public class SecretsErrorController {

  @GetMapping("/error")
  @Operation(summary = "Returns data for the error page")
  public String handleError() {
    return "error";
  }
}
