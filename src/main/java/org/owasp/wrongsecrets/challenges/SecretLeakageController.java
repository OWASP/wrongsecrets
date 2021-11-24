package org.owasp.wrongsecrets.challenges;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.Constants;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.Spoiler;
import org.owasp.wrongsecrets.Vaultpassword;
import org.owasp.wrongsecrets.challenges.docker.Challenge1;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@EnableConfigurationProperties(Vaultpassword.class)
@Slf4j
@RequiredArgsConstructor
public class SecretLeakageController {

    private final Vaultpassword vaultPassword;
    private final ScoreCard scoreCard;

    //NOTE: Fix this later, needed to do a graceful migration
    private final Challenge1 challenge1;

    @Value("${ARG_BASED_PASSWORD}")
    private String argBasedPassword;

    @Value("${DOCKER_ENV_PASSWORD}")
    private String hardcodedEnvPassword;

    @Value("${SPECIAL_K8S_SECRET}")
    private String configmapK8sSecret;

    @Value("${SPECIAL_SPECIAL_K8S_SECRET}")
    private String secretK8sSecret;

    @Value("${vaultpassword}")
    private String vaultPasswordString;

    @Value("${default_aws_value}")
    private String awsDefaultValue;

    @Value("${secretmountpath}")
    private String filePath;

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
        return getSpoil(model, challenge1.spoiler());
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
    public String getCloudChallenge1(Model model) {
        return getSpoil(model, getCloudChallenge9and10Value("wrongsecret"));
    }

    @GetMapping("/spoil-10")
    public String getCloudChallenge2(Model model) {
        return getSpoil(model, getCloudChallenge9and10Value("wrongsecret-2"));
    }

    @GetMapping("/spoil-11")
    public String getCloudChallenge3(Model model) {
        return getSpoil(model, getAWSChallenge11Value());
    }

    private String getSpoil(Model model, Spoiler spoiler) {
        model.addAttribute("spoil", spoiler);
        model.addAttribute("solution", spoiler.solution());
        return "spoil";
    }

    @Deprecated(forRemoval = true)
    private String getSpoil(Model model, String password) {
        model.addAttribute("solution", password);
        return "spoil";
    }

    @GetMapping("/")
    public String rootPage(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("environment", k8sEnvironment);
        return "index";
    }


    @GetMapping("/challenge/{id:2|3|4|5|6|7|8|9|10|11}")
    public String challengeForm(@PathVariable String id, Model model) {
        model.addAttribute("challengeForm", new ChallengeForm(""));
        model.addAttribute("challengeNumber", id);
        model.addAttribute("answerCorrect", null);
        model.addAttribute("answerIncorrect", null);
        model.addAttribute("solution", null);
        model.addAttribute("environment", k8sEnvironment);
        int challengeNumber = 0;
        try {
            challengeNumber = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            challengeNumber = 12;
        }
        if (challengeNumber > 11) {
            challengeNumber = 1;
            model.addAttribute("runtimeWarning", "There are only 11 challenges, please navigate to another one");
        }
        model.addAttribute("challengeNumber", challengeNumber);

        includeScoringStatus(challengeNumber, model, null);
        addWarning(challengeNumber, model);
        return "challenge";
    }

    @PostMapping("/challenge/2")
    public String postController2(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 2- serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 2);
        return handleModel(argBasedPassword, challengeForm.solution(), model, 2);
    }

    @PostMapping("/challenge/3")
    public String postController3(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 3 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 3);
        return handleModel(hardcodedEnvPassword, challengeForm.solution(), model, 3);
    }

    @PostMapping("/challenge/4")
    public String postController4(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 4 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 4);
        return handleModel(Constants.password, challengeForm.solution(), model, 4);
    }

    @PostMapping("/challenge/5")
    public String postController5(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 5 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 5);
        return handleModel(configmapK8sSecret, challengeForm.solution(), model, 5);
    }

    @PostMapping("/challenge/6")
    public String postController6(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 6 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 6);
        return handleModel(secretK8sSecret, challengeForm.solution(), model, 6);
    }


    @PostMapping("/challenge/7")
    public String postController7(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 7 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 7);
        if (null != vaultPassword.getPasssword()) {
            return handleModel(vaultPassword.getPasssword(), challengeForm.solution(), model, 7);
        }
        return handleModel(vaultPasswordString, challengeForm.solution(), model, 7);
    }

    @PostMapping("/challenge/8")
    public String postController8(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 8 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 8);
        return handleModel(Constants.newKey, challengeForm.solution(), model, 8);
    }

    @PostMapping("/challenge/9")
    public String postController9(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 9 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 9);
        return handleModel(getCloudChallenge9and10Value("wrongsecret"), challengeForm.solution(), model, 8);
    }

    @PostMapping("/challenge/10")
    public String postController10(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 10 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 10);
        return handleModel(getCloudChallenge9and10Value("wrongsecret-2"), challengeForm.solution(), model, 9);
    }

    @PostMapping("/challenge/11")
    public String postController11(@ModelAttribute ChallengeForm challengeForm, Model model) {
        log.info("POST received at 11 - serializing form: solution: " + challengeForm.solution());
        model.addAttribute("challengeNumber", 11);
        return handleModel(getAWSChallenge11Value(), challengeForm.solution(), model, 10);
    }


    private String handleModel(String targetPassword, String given, Model model, int challenge) {
        if (targetPassword.equals(given)) {
            scoreCard.completeChallenge(challenge);
            model.addAttribute("answerCorrect", "Your answer is correct!");
        } else {
            model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
        }
        includeScoringStatus(challenge, model, null);
        addWarning(challenge, model);
        return "challenge";
    }

    private void addWarning(Challenge challenge, Model model) {
        if (!challenge.environmentSupported())
            model.addAttribute("runtimeWarning", switch (challenge.getEnvironment()) {
                case DOCKER -> "We are running outside of a docker container. Please run this in a container as explained in the README.md.";
                default -> "??";
            });
    }

    @Deprecated
    private void addWarning(int id, Model model) {
        if ("if_you_see_this_please_use_docker_instead".equals(argBasedPassword) && ((1 < id && id < 5) || 8 == id)) {
            model.addAttribute("runtimeWarning", "We are running outside of a docker container. Please run this in a container as explained in the README.md.");
        }
        if ((5 == id || 6 == id) && "if_you_see_this_please_use_k8s".equals(configmapK8sSecret)) {
            model.addAttribute("runtimeWarning", "We are running outside of a K8s cluster. Please run this in the K8s cluster as explained in the README.md.");
        }
        if (7 == id && vaultPassword.getPasssword() == null) {
            model.addAttribute("runtimeWarning", "We are running outside of a K8s cluster with Vault. Please run this in the K8s cluster as explained in the README.md.");
        }
        if ((9 == id || 10 == id) && (!"gcp".equals(k8sEnvironment)) && (!"aws".equals(k8sEnvironment))) {
            model.addAttribute("runtimeWarning", "We are running outside of a properly configured AWS or GCP environment. Please run this in an AWS or GCP environment as explained in the README.md.");
        }
        if ((11 == id) && (!"aws".equals(k8sEnvironment))) {
            model.addAttribute("runtimeWarning", "We are running outside of a properly configured AWS environment. Please run this in an AWS environment as explained in the README.md. GCP is not done yet");
        }
    }

    private void includeScoringStatus(int id, Model model, Challenge challenge) {
        model.addAttribute("version", version);
        model.addAttribute("totalPoints", scoreCard.getTotalReceivedPoints());
        model.addAttribute("progress", "" + scoreCard.getProgress());

        if (challenge == null) { //TODO remove after migration of all challenges
            if (scoreCard.getChallengeCompleted(id)) {
                model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
            }
        } else {
            if (scoreCard.getChallengeCompleted(challenge.getId())) {
                model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
            }
        }
    }

    private String getCloudChallenge9and10Value(String fileName) {
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
