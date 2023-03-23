package org.owasp.wrongsecrets;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class IndexController {

    @GetMapping("/")
    @Operation(description = "Returns all dynamic data for the welcome screen")
    public String index() {
        return "welcome";
    }
}
