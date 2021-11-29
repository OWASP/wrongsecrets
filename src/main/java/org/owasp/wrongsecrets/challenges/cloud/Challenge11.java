package org.owasp.wrongsecrets.challenges.cloud;


import com.google.api.gax.rpc.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
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

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;

@Component
@Order(11)
@Slf4j
public class Challenge11 extends Challenge {

    private final String awsRoleArn;
    private final String awsRegion;
    private final String tokenFileLocation;
    private final String awsDefaultValue;
    private final String gcpDefaultValue;
    private final String challengeAnswer;
    private final String k8sEnvironment;
    private final String projectId;

    public Challenge11(ScoreCard scoreCard,
                       @Value("${AWS_ROLE_ARN}") String awsRoleArn,
                       @Value("${AWS_WEB_IDENTITY_TOKEN_FILE}") String tokenFileLocation,
                       @Value("${AWS_REGION}") String awsRegion,
                       @Value("${default_gcp_value}") String gcpDefaultValue,
                       @Value("${default_aws_value}") String awsDefaultValue,
                       @Value("${GCP_PROJECT_ID}") String projectId,
                       @Value("${K8S_ENV}") String k8sEnvironment) {
        super(scoreCard);
        this.awsRoleArn = awsRoleArn;
        this.tokenFileLocation = tokenFileLocation;
        this.awsRegion = awsRegion;
        this.awsDefaultValue = awsDefaultValue;
        this.gcpDefaultValue = gcpDefaultValue;
        this.k8sEnvironment = k8sEnvironment;
        this.projectId = projectId;
        this.challengeAnswer = k8sEnvironment.equals("aws") ? getAWSChallenge11Value() : getGCPChallenge11Value();
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

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(AWS);
    }

    private String getAWSChallenge11Value() {
        if (!"if_you_see_this_please_use_AWS_Setup".equals(awsRoleArn)) {
            log.info("Getting credentials from AWS");
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
        } else {
            log.info("Skipping credentials from AWS");
        }
        return awsDefaultValue;
    }

    private String getGCPChallenge11Value() {
        if ("gcp".equals(k8sEnvironment)) {
            log.info("Getting credentials from GCP");
            // Based on https://cloud.google.com/secret-manager/docs/reference/libraries
            try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
                log.info("Fetching secret form Google Secret Manager...");
                SecretVersionName secretVersionName = SecretVersionName.of(projectId, "wrongsecret-3", "latest");
                AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
                String payload = response.getPayload().getData().toStringUtf8();
                return payload;
            } catch (ApiException e) {
                log.error("Exception getting secret", e);
            } catch (IOException e) {
                log.error("Could not get the web identity token, due to ", e);
            }
        } else {
            log.info("Skipping credentials from GCP");
        }
        return gcpDefaultValue;
    }
}
