package com.example.secrettextprinter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SecretLeakageController {

    private static Logger log = LoggerFactory.getLogger(SecretLeakageController.class);


    @Value("${password}")
    String hardcodedPassword;

    @Value("${ARG_BASED_PASSWORD}")
    String argBasedPassword;

    @Value("${DOCKER_ENV_PASSWORD}")
    String hardcodedEnvPassword;

    @Value("${SPECIAL_K8S_SECRET}")
    String configmapK8sSecret;

    @Value("${SPECIAL_SPECIAL_K8S_SECRET}")
    String secretK8sSecret;

    @GetMapping("/spoil-1")
    public String getHardcodedSecret() {
        return hardcodedPassword;
    }

    @GetMapping("/spoil-2")
    public String getEnvArgBasedSecret() {
        return argBasedPassword;
    }

    @GetMapping("/spoil-3")
    public String getEnvStaticSecret() {
        return hardcodedEnvPassword;
    }

    @GetMapping("/spoil-4")
    public String getOldSecret() {
        return Constants.password;
    }

    @GetMapping("/spoil-5")
    public String getK8sSecret() {
        return configmapK8sSecret;
    }

    @GetMapping("/spoil-6")
    public String getSecretK8sSecret() {
        return secretK8sSecret;
    }

    @GetMapping("/challenge/{id}")
    public String challengeForm(@PathVariable String id, Model model) {
        model.addAttribute("challengeForm", new ChallengeForm());
        model.addAttribute("challengeNumber", id);
        return "challenge";
    }

    @PostMapping("/challenge/1")
    public String postController(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 1 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 1);
        return handleModel(hardcodedPassword, challengeForm.getSolution(), model);

    }

    @PostMapping("/challenge/2")
    public String postController2(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 2- serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 2);
        return handleModel(argBasedPassword, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/3")
    public String postController3(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 3 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 3);
        return handleModel(hardcodedEnvPassword, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/4")
    public String postController4(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 4 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 4);
        return handleModel(Constants.password, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/5")
    public String postController5(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 5 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 5);
        return handleModel(configmapK8sSecret, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/6")
    public String postController6(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 6 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 6);
        return handleModel(secretK8sSecret, challengeForm.getSolution(), model);
    }


    private String handleModel(String targetPassword, String given, Model model){
        if (targetPassword.equals(given)) {
            model.addAttribute("answerCorrect", "You're answer is correct!");
        } else {
            model.addAttribute("answerCorrect", "You're answer is incorrect, try harder ;-)");
        }
        return "challenge";
    }


}
