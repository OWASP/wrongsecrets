package com.example.secrettextprinter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleWithWebIdentityCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@EnableConfigurationProperties(Vaultpassword.class)
@Slf4j
public class SecretLeakageController {

    private final Vaultpassword vaultPassword;
    private final ConcurrentHashMap<String, InMemoryScoring> scoringCache;

    public SecretLeakageController(Vaultpassword vaultpassword) {
        scoringCache = new ConcurrentHashMap<>();
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

    @Value("${K8S_ENV}")
    private String k8sEnvironment;

    @Value("${AWS_ROLE_ARN}")
    private String awsRoleArn;

    @Value("${AWS_WEB_IDENTITY_TOKEN_FILE}")
    private String tokenFileLocation;

    @Value("${AWS_REGION}")
    private String awsRegion;

    @Value("${APP_VERSION}")
    private String version;

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
        return getSpoil(model, getAWSChallenge9and10Value("wrongsecret"));
    }

    @GetMapping("/spoil-10")
    public String getAWSChanngelenge2(Model model) {
        return getSpoil(model, getAWSChallenge9and10Value("wrongsecret-2"));
    }

    @GetMapping("/spoil-11")
    public String getAWSChanngelenge3(Model model) {
        return getSpoil(model, getAWSChallenge11Value());
    }

    @GetMapping("/")
    public String rootPage(Model model, HttpSession session) {
        model.addAttribute("version", version);
        model.addAttribute("environment", k8sEnvironment);
        String sessionID = session.getId();
        if (!scoringCache.containsKey(sessionID)) {
            InMemoryScoring newScore = new InMemoryScoring(11);
            scoringCache.put(sessionID, newScore);
            log.info("Number of in memory scoring: {}", scoringCache.size());
        }
        return "index";
    }

    @GetMapping("/challenge/{id}")
    public String challengeForm(@PathVariable String id, Model model, HttpSession session) {
        InMemoryScoring newScore = getInMemoryScoring(session);
        model.addAttribute("challengeForm", new ChallengeForm());
        model.addAttribute("challengeNumber", id);
        model.addAttribute("answerCorrect", null);
        model.addAttribute("answerIncorrect", null);
        model.addAttribute("solution", null);
        includeScoringStatus(newScore, Integer.parseInt(id), model);
        addWarning(Integer.parseInt(id), model);
        return "challenge";
    }

    private InMemoryScoring getInMemoryScoring(HttpSession session) {
        String sessionID = session.getId();
        InMemoryScoring newScore;
        if (!scoringCache.containsKey(sessionID)) {
            newScore = new InMemoryScoring(11);
            scoringCache.put(sessionID, newScore);
        } else {
            newScore = scoringCache.get(sessionID);
        }
        log.info("Number of in memory scoring: {}", scoringCache.size());
        return newScore;
    }


    @PostMapping("/challenge/1")
    public String postController(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 1 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 1);
        return handleModel(session, hardcodedPassword, challengeForm.getSolution(), model, 1);

    }

    @PostMapping("/challenge/2")
    public String postController2(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 2- serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 2);
        return handleModel(session, argBasedPassword, challengeForm.getSolution(), model, 2);
    }

    @PostMapping("/challenge/3")
    public String postController3(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 3 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 3);
        return handleModel(session, hardcodedEnvPassword, challengeForm.getSolution(), model, 3);
    }

    @PostMapping("/challenge/4")
    public String postController4(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 4 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 4);
        return handleModel(session, Constants.password, challengeForm.getSolution(), model, 4);
    }

    @PostMapping("/challenge/5")
    public String postController5(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 5 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 5);
        return handleModel(session, configmapK8sSecret, challengeForm.getSolution(), model, 5);
    }

    @PostMapping("/challenge/6")
    public String postController6(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 6 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 6);
        return handleModel(session, secretK8sSecret, challengeForm.getSolution(), model, 6);
    }


    @PostMapping("/challenge/7")
    public String postController7(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 7 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 7);
        if (null != vaultPassword.getPasssword()) {
            return handleModel(session, vaultPassword.getPasssword(), challengeForm.getSolution(), model, 7);
        }
        return handleModel(session, vaultPasswordString, challengeForm.getSolution(), model, 7);
    }

    @PostMapping("/challenge/8")
    public String postController8(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 8 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 8);
        return handleModel(session, Constants.newKey, challengeForm.getSolution(), model, 8);
    }

    @PostMapping("/challenge/9")
    public String postController9(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 9 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 9);
        return handleModel(session, getAWSChallenge9and10Value("wrongsecret"), challengeForm.getSolution(), model, 8);
    }

    @PostMapping("/challenge/10")
    public String postController10(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 10 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 10);
        return handleModel(session, getAWSChallenge9and10Value("wrongsecret-2"), challengeForm.getSolution(), model, 9);
    }

    @PostMapping("/challenge/11")
    public String postController11(@ModelAttribute ChallengeForm challengeForm, Model model, HttpSession session) {
        log.info("POST received at 11 - serializing form: solution: " + challengeForm.getSolution());
        model.addAttribute("challengeNumber", 11);
        return handleModel(session, getAWSChallenge11Value(), challengeForm.getSolution(), model, 10);
    }


    private String handleModel(HttpSession session, String targetPassword, String given, Model model, int challenge) {
        InMemoryScoring newScore = getInMemoryScoring(session);
        if (targetPassword.equals(given)) {
            newScore.completeChallenge(challenge);
            model.addAttribute("answerCorrect", "Your answer is correct!");
        } else {
            model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
        }
        includeScoringStatus(newScore, challenge, model);
        addWarning(challenge, model);
        return "challenge";
    }

    private void addWarning(int id, Model model) {
        if ("if_you_see_this_please_use_docker_instead".equals(argBasedPassword) && (id < 5 || 8 == id)) {
            model.addAttribute("runtimeWarning", "We are running outside of a docker container. Please run this in a container as explained in the README.md.");
        }
        if ((5 == id || 6 == id) && "if_you_see_this_please_use_k8s".equals(configmapK8sSecret)) {
            model.addAttribute("runtimeWarning", "We are running outside of a K8s cluster. Please run this in the K8s cluster as explained in the README.md.");
        }
        if (7 == id && vaultPassword.getPasssword() == null) {
            model.addAttribute("runtimeWarning", "We are running outside of a K8s cluster with Vault. Please run this in the K8s cluster as explained in the README.md.");
        }
        if ((9 == id || 10 == id || 11 == id) && "if_you_see_this_please_use_AWS_Setup".equals(awsRoleArn)) {
            model.addAttribute("runtimeWarning", "We are running outside of a properly configured AWS environment. Please run this in an AWS environment as explained in the README.md.");
        }
    }

    private void includeScoringStatus(InMemoryScoring scoring, int id, Model model) {
        model.addAttribute("version", version);
        model.addAttribute("totalPoints", scoring.getTotalReceivedPoints());
        model.addAttribute("progress", "" + scoring.getProgress());
        if (scoring.getChallengeCompleted(id)) {
            model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
        }
    }

    private String getAWSChallenge9and10Value(String fileName) {
        try {
            Path filePath = Paths.get(this.filePath, fileName);
            return Files.readString(filePath);
        } catch (Exception e) {
            log.error("Exception during file reading, defaulting to default without aWS", e);
            return awsDefaultValue;
        }
    }

    private String getAWSChallenge11Value() {
        log.info("Getting credentials");
        if (!"if_you_see_this_please_use_AWS_Setup".equals(awsRoleArn)) {

            try { //based on https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sts/src/main/java/com/example/sts
                String webIDentityToken = Files.readString(Paths.get(tokenFileLocation));
                StsClient stsClient = StsClient.builder()
                        .region(Region.of(awsRegion))
                        .build();
                AssumeRoleWithWebIdentityRequest webIdentityRequest = AssumeRoleWithWebIdentityRequest.builder()
                        .roleArn(awsRoleArn)
                        .roleSessionName("WrongsecretsApp")
                        .webIdentityToken(webIDentityToken)
                        .build();

                AssumeRoleWithWebIdentityResponse tokenResponse = stsClient.assumeRoleWithWebIdentity(webIdentityRequest);
                log.info("The token value is " + tokenResponse.credentials().sessionToken());
                SsmClient ssmClient = SsmClient.builder()
                        .region(Region.of(awsRegion))
                        .credentialsProvider(StsAssumeRoleWithWebIdentityCredentialsProvider.builder()
                                .stsClient(stsClient)
                                .refreshRequest(webIdentityRequest)
                                .build())
                        .build();
                GetParameterRequest parameterRequest = GetParameterRequest.builder()
                        .name("wrongsecretvalue")
                        .withDecryption(true)
                        .build();
                GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
                log.info("The parameter value is " + parameterResponse.parameter().value());
                ssmClient.close();
                return parameterResponse.parameter().value();
            } catch (StsException e) {
                log.error("Exception with getting credentials", e);
            } catch (SsmException e) {
                log.error("Exception with getting parameter", e);
            } catch (IOException e) {
                log.error("Could not get the web identity token, due to ", e);
            }
        }
        return awsDefaultValue;
    }

}
