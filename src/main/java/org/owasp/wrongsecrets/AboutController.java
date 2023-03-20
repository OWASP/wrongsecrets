package org.owasp.wrongsecrets;

import io.swagger.v3.oas.annotations.Operation;
import org.owasp.wrongsecrets.canaries.CanaryCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {



    @GetMapping("/about")
    @Operation(description = "Endpoint to get dynamic data on about")
    public String getStats(Model model) {
        return "about";
    }
}
