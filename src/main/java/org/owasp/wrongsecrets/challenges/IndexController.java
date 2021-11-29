package org.owasp.wrongsecrets.challenges;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;
import java.util.Objects;

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
        linkDisableInstruction(model);
        model.addAttribute("version", version);
        model.addAttribute("environment", k8sEnvironment);
        return "index";
    }

    /**
     * Added the cloud, vault, and k8s variables in the model in order ot highlight the challenges which are functional and which are not.
     */
    private void linkDisableInstruction(Model model) {
        if ("gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("cloud", "enabled");
        }
        if ("k8s-with-vault".equals(k8sEnvironment) || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("vault", "enabled");
        }
        if (k8sEnvironment.contains("k8s") || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
            model.addAttribute("k8s", "enabled");
        }
    }
}
