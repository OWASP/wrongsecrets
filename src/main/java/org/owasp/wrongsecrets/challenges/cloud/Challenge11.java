package org.owasp.wrongsecrets.challenges.cloud;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AZURE;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.GCP;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
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
import software.amazon.awssdk.services.sts.model.StsException;

/** Cloud challenge which uses IAM privilelge escalation (differentiating per cloud). */
@Component
@Slf4j
@Order(11)
public class Challenge11 extends CloudChallenge {

  private final String awsRoleArn;
  private final String awsRegion;
  private final String tokenFileLocation;
  private final String awsDefaultValue;
  private final String gcpDefaultValue;
  private final String azureDefaultValue;
  private final String challengeAnswer;
  private final String projectId;
  private final String azureVaultUri;
  private final String azureWrongSecret3;

  private final String ctfValue;

  private final boolean ctfEnabled;

  public Challenge11(
      ScoreCard scoreCard,
      @Value("${AWS_ROLE_ARN}") String awsRoleArn,
      @Value("${AWS_WEB_IDENTITY_TOKEN_FILE}") String tokenFileLocation,
      @Value("${AWS_REGION}") String awsRegion,
      @Value("${default_gcp_value}") String gcpDefaultValue,
      @Value("${default_aws_value}") String awsDefaultValue,
      @Value("${default_azure_value}") String azureDefaultValue,
      @Value("${spring.cloud.azure.keyvault.secret.property-sources[0].endpoint}")
          String azureVaultUri,
      @Value("${wrongsecret-3}") String azureWrongSecret3, // Exclusively auto-wired for Azure
      @Value("${GOOGLE_CLOUD_PROJECT}") String projectId,
      @Value("${default_aws_value_challenge_11}") String ctfValue,
      @Value("${ctf_enabled}") boolean ctfEnabled,
      RuntimeEnvironment runtimeEnvironment) {
    super(scoreCard, runtimeEnvironment);
    this.awsRoleArn = awsRoleArn;
    this.tokenFileLocation = tokenFileLocation;
    this.awsRegion = awsRegion;
    this.awsDefaultValue = awsDefaultValue;
    this.gcpDefaultValue = gcpDefaultValue;
    this.azureDefaultValue = azureDefaultValue;
    this.projectId = projectId;
    this.azureVaultUri = azureVaultUri;
    this.azureWrongSecret3 = azureWrongSecret3;
    this.ctfValue = ctfValue;
    this.ctfEnabled = ctfEnabled;
    this.challengeAnswer = getChallenge11Value(runtimeEnvironment);
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(challengeAnswer);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return challengeAnswer.equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(AWS, GCP, AZURE);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EXPERT;
  }

  /** {@inheritDoc} Uses IAM Privilege escalation */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.IAM.id;
  }

  private String getChallenge11Value(RuntimeEnvironment runtimeEnvironment) {
    if (!ctfEnabled) {
      if (runtimeEnvironment != null && runtimeEnvironment.getRuntimeEnvironment() != null) {
        return switch (runtimeEnvironment.getRuntimeEnvironment()) {
          case AWS -> getAWSChallenge11Value();
          case GCP -> getGCPChallenge11Value();
          case AZURE -> getAzureChallenge11Value();
          default -> "please_use_supported_cloud_env";
        };
      }
    } else if (!Strings.isNullOrEmpty(ctfValue)
        && !Strings.isNullOrEmpty(awsDefaultValue)
        && !ctfValue.equals(awsDefaultValue)) {
      return ctfValue;
    }

    log.info("CTF enabled, skipping challenge11");
    return "please_use_supported_cloud_env";
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the tokenFileLocation is based on an Env Var")
  private String getAWSChallenge11Value() {
    log.info("pre-checking AWS data");
    if (!"if_you_see_this_please_use_AWS_Setup".equals(awsRoleArn)) {
      log.info("Getting credentials from AWS");
      try { // based on
        // https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/sts/src/main/java/com/example/sts

        String webIDentityToken =
            Files.readString(Paths.get(tokenFileLocation), StandardCharsets.UTF_8);
        StsClient stsClient = StsClient.builder().region(Region.of(awsRegion)).build();
        AssumeRoleWithWebIdentityRequest webIdentityRequest =
            AssumeRoleWithWebIdentityRequest.builder()
                .roleArn(awsRoleArn)
                .roleSessionName("WrongsecretsApp")
                .webIdentityToken(webIDentityToken)
                .build();
        stsClient.assumeRoleWithWebIdentity(
            webIdentityRequest); // returns a AssumeRoleWithWebIdentityResponse which you can debug
        // with //log.debug("The token value is " +
        // tokenResponse.credentials().sessionToken());
        SsmClient ssmClient =
            SsmClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(
                    StsAssumeRoleWithWebIdentityCredentialsProvider.builder()
                        .stsClient(stsClient)
                        .refreshRequest(webIdentityRequest)
                        .build())
                .build();
        GetParameterRequest parameterRequest =
            GetParameterRequest.builder().name("wrongsecretvalue").withDecryption(true).build();
        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        // log.debug("The parameter value is " + parameterResponse.parameter().value());
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
    log.info("pre-checking GCP data");
    if (isGCP()) {
      log.info("Getting credentials from GCP");
      // Based on https://cloud.google.com/secret-manager/docs/reference/libraries
      try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
        log.info("Fetching secret form Google Secret Manager...");
        SecretVersionName secretVersionName =
            SecretVersionName.of(projectId, "wrongsecret-3", "latest");
        AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
        return response.getPayload().getData().toStringUtf8();
      } catch (ApiException e) {
        log.error("Exception getting secret: ", e);
      } catch (IOException e) {
        log.error("Could not get the web identity token, due to ", e);
      }
    } else {
      log.info("Skipping credentials from GCP");
    }
    return gcpDefaultValue;
  }

  private String getAzureChallenge11Value() {
    log.info("pre-checking Azure data");
    if (isAzure()) {
      log.info(String.format("Using Azure Key Vault URI: %s", azureVaultUri));
      return azureWrongSecret3;
    }
    log.error("Fetching secret from Azure did not work, returning default");
    return azureDefaultValue;
  }

  @Override
  public boolean canRunInCTFMode() {
    return false;
  }
}
