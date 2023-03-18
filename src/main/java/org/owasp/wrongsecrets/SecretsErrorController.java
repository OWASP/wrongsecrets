package org.owasp.wrongsecrets;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecretsErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        return "error";
    }
}
