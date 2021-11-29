package org.owasp.wrongsecrets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
//        if ("gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("cloud", "enabled");
//        }
//        if ("k8s-with-vault".equals(k8sEnvironment) || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("vault", "enabled");
//        }
//        if (k8sEnvironment.contains("k8s") || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("k8s", "enabled");
//        }
        return "welcome";
    }
}
