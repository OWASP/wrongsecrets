package org.owasp.wrongsecrets.challenges.cloud;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
import java.nio.file.Paths;

@Component
@ChallengeNumber("11")
@Slf4j
public class Challenge11 extends Challenge {

    private final String awsRoleArn;
    private final String awsRegion;
    private final String tokenFileLocation;
    private final String awsDefaultValue;
    private final String challengeAnswer;
    private final String k8sEnvironment;

    public Challenge11(ScoreCard scoreCard,
                       @Value("${AWS_ROLE_ARN}") String awsRoleArn,
                       @Value("${AWS_WEB_IDENTITY_TOKEN_FILE}") String tokenFileLocation,
                       @Value("${AWS_REGION}") String awsRegion,
                       @Value("${default_aws_value}") String awsDefaultValue,
                       @Value("${K8S_ENV}") String k8sEnvironment) {
        super(scoreCard, ChallengeEnvironment.CLOUD);
        this.awsRoleArn = awsRoleArn;
        this.tokenFileLocation = tokenFileLocation;
        this.awsRegion = awsRegion;
        this.awsDefaultValue = awsDefaultValue;
        this.challengeAnswer = getAWSChallenge11Value();
        this.k8sEnvironment = k8sEnvironment;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(challengeAnswer);
    }

    @Override
    public String getExplanationFileIdentifier() {
        if ("gcp".equals(k8sEnvironment)) {
            return "11-gcp";
        }
        return "11";
    }

    @Override
    public boolean answerCorrect(String answer) {
        return challengeAnswer.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return k8sEnvironment.equals("gcp") || k8sEnvironment.contains("aws");
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
