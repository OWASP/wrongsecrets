package org.owasp.wrongsecrets;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class IndexController {

    private final String k8sEnvironment;
    private final String version;
    private final List<ChallengeUI> challenges;

    public IndexController(@Value("${K8S_ENV}") String k8sEnvironment, @Value("${APP_VERSION}") String version, List<Challenge> challenges) {
        this.k8sEnvironment = k8sEnvironment;
        this.version = version;
        this.challenges = ChallengeUI.toUI(challenges, k8sEnvironment);
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "index");
        model.addAttribute("challenges", challenges);
        model.addAttribute("version", version);
        model.addAttribute("environment", k8sEnvironment);
        if ("gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("cloud", "enabled");
        }
        if ("k8s-with-vault".equals(k8sEnvironment) || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("vault", "enabled");
        }
        if (k8sEnvironment.contains("k8s") || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("k8s", "enabled");
        }
        return "index";
    }
}
