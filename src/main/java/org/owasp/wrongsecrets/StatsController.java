package org.owasp.wrongsecrets;

import io.swagger.v3.oas.annotations.Operation;
import org.owasp.wrongsecrets.canaries.CanaryCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatsController {

    @Autowired
    private CanaryCounter canaryCounter;
    @Autowired
    private SessionConfiguration sessionConfiguration;

    @Value("${hints_enabled}")
    private boolean hintsEnabled;
    @Value("${reason_enabled}")
    private boolean reasonEnabled;
    @Value("${ctf_enabled}")
    private boolean ctfModeEnabled;

    @Value("${springdoc.swagger-ui.enabled}")
    private boolean swaggerUIEnabled;

    @Value("${springdoc.api-docs.enabled}")
    private boolean springdockenabled;

    @Value("${canarytokenURLs}")
    private String[] canaryTokenURLs;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerURI;

    @GetMapping("/stats")
    @Operation(description = "Returns all dynamic data for the stats screen")
    public String getStats(Model model) {
        model.addAttribute("canaryCounter", canaryCounter.getTotalCount());
        model.addAttribute("sessioncounter", sessionConfiguration.getCounter());
        model.addAttribute("lastCanaryToken", canaryCounter.getLastToken());
        model.addAttribute("canarytokenURLs", canaryTokenURLs);
        model.addAttribute("hintsEnabled", hintsEnabled);
        model.addAttribute("reasonEnabled", reasonEnabled);
        model.addAttribute("ctfModeEnabled", ctfModeEnabled);
        model.addAttribute("swaggerUIEnabled", swaggerUIEnabled);
        model.addAttribute("springdockenabled", springdockenabled);
        model.addAttribute("swaggerURI", swaggerURI);
        return "stats";
    }
}
