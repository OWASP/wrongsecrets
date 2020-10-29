package com.example.secrettextprinter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

    @PostMapping("/challenge/1")
    public ResponseEntity postControler(@RequestBody ChallengeForm challengeForm) {
        log.info("POST received - serializing form: solution: " + challengeForm.getSolution());
        return setResponse(hardcodedPassword, challengeForm.getSolution());
    }

    @PostMapping("/challenge/2")
    public ResponseEntity postControler2(@RequestBody ChallengeForm challengeForm) {
        log.info("POST received - serializing form: solution: " + challengeForm.getSolution());
        return setResponse(argBasedPassword, challengeForm.getSolution());
    }

    @PostMapping("/challenge/3")
    public ResponseEntity postControler3(@RequestBody ChallengeForm challengeForm) {
        log.info("POST received - serializing form: solution: " + challengeForm.getSolution());
        return setResponse(hardcodedEnvPassword, challengeForm.getSolution());
    }

    @PostMapping("/challenge/4")
    public ResponseEntity postControler4(@RequestBody ChallengeForm challengeForm) {
        log.info("POST received - serializing form: solution: " + challengeForm.getSolution());
        return setResponse(Constants.password, challengeForm.getSolution());
    }

    @PostMapping("/challenge/5")
    public ResponseEntity postControler5(@RequestBody ChallengeForm challengeForm) {
        log.info("POST received - serializing form: solution: " + challengeForm.getSolution());
        return setResponse(configmapK8sSecret, challengeForm.getSolution());
    }

    private ResponseEntity setResponse(String target, String providedSolution) {
        if (target.equals(providedSolution)) {
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.badRequest().body("Wrong anser!");
        }
    }


}
