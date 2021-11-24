package org.owasp.wrongsecrets.challenges;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    @Value("${K8S_ENV}")
    private String k8sEnvironment;

    @Value("${APP_VERSION}")
    private String version;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("environment", k8sEnvironment);
        return "index";
    }
}
