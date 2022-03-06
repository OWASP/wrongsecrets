package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.canaries.CanaryCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatiscisController {

    @Autowired
    private CanaryCounter canaryCounter;
    @Autowired
    private SessionConfiguration sessionConfiguration;

    @GetMapping("/stats")
    public String getStats(Model model) {
        model.addAttribute("canaryounter", canaryCounter.getTotalCount());
        model.addAttribute("sessioncounter", sessionConfiguration.getCounter());
        return "stats";
    }
}
