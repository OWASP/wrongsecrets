package com.example.secrettextprinter;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@EnableConfigurationProperties(Vaultpassword.class)
@Slf4j
public class SecretLeakageController {

    private final Vaultpassword vaultPassword;

    public SecretLeakageController(Vaultpassword vaultpassword) {
        this.vaultPassword = vaultpassword;
    }

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

    @Value("${vaultpassword}")
    String vaultPasswordString;

    @Value("${default_aws_value}")
    String awsDefaultValue;

    @Value("${secretmountpath}")
    String filePath;

    @GetMapping("/spoil-1")
    public String getHardcodedSecret(Model model) {
        return getSpoil(model, hardcodedPassword);
    }

    private String getSpoil(Model model, String password) {
        model.addAttribute("spoil", new Spoil());
        model.addAttribute("solution", password);
        return "spoil";
    }

    @GetMapping("/spoil-2")
    public String getEnvArgBasedSecret(Model model) {
        return getSpoil(model, argBasedPassword);
    }

    @GetMapping("/spoil-3")
    public String getEnvStaticSecret(Model model) {
        return getSpoil(model, hardcodedEnvPassword);
    }

    @GetMapping("/spoil-4")
    public String getOldSecret(Model model) {
        return getSpoil(model, Constants.password);
    }

    @GetMapping("/spoil-5")
    public String getK8sSecret(Model model) {
        return getSpoil(model, configmapK8sSecret);
    }

    @GetMapping("/spoil-6")
    public String getSecretK8sSecret(Model model) {
        return getSpoil(model, secretK8sSecret);
    }

    @GetMapping("/spoil-7")
    public String getVaultPassword(Model model) {
        if (null != vaultPassword.getPasssword()) {
            return getSpoil(model, vaultPassword.getPasssword());
        }
        return getSpoil(model, vaultPasswordString);
    }

    @GetMapping("/spoil-8")
    public String getRandCode(Model model) {
        return getSpoil(model, Constants.newKey);
    }

    @GetMapping("/spoil-9")
    public String getAWSChanngelenge1(Model model) {
        return getSpoil(model, getAWSChallengeValue("wrongsecret"));
    }

    @GetMapping("/spoil-10")
    public String getAWSChanngelenge2(Model model) {
        return getSpoil(model, getAWSChallengeValue("wrongsecret-2"));
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


    @PostMapping("/challenge/7")
    public String postController7(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 7 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 7);
        if (null != vaultPassword.getPasssword()) {
            return handleModel(vaultPassword.getPasssword(), challengeForm.getSolution(), model);
        }
        return handleModel(vaultPasswordString, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/8")
    public String postController8(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 8 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 8);
        return handleModel(Constants.newKey, challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/9")
    public String postController9(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 9 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 9);
        return handleModel(getAWSChallengeValue("wrongsecret"), challengeForm.getSolution(), model);
    }

    @PostMapping("/challenge/10")
    public String postController10(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 10 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 10);
        return handleModel(getAWSChallengeValue("wrongsecret-2"), challengeForm.getSolution(), model);
    }


    private String handleModel(String targetPassword, String given, Model model) {
        if (targetPassword.equals(given)) {
            model.addAttribute("answerCorrect", "Your answer is correct!");
        } else {
            model.addAttribute("answerCorrect", "Your answer is incorrect, try harder ;-)");
        }
        return "challenge";
    }

    private String getAWSChallengeValue(String fileName) {

        try {
            Path filePath = Paths.get(this.filePath, fileName);
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Exception during file reading, defaulting to default without aWS", e);
            return awsDefaultValue;
        }

    }

}
