package org.owasp.wrongsecrets.challenges.cloud.challenge11;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
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
import software.amazon.awssdk.services.sts.model.StsException;

/** Cloud challenge which uses IAM privilelge escalation (differentiating per cloud). */
@Component
@Slf4j
public class Challenge11Aws extends FixedAnswerChallenge {

  private final String awsRoleArn;
  private final String awsRegion;
  private final String tokenFileLocation;
  private final String awsDefaultValue;
  private final String ctfValue;
  private final boolean ctfEnabled;

  public Challenge11Aws(
      @Value("${AWS_ROLE_ARN}") String awsRoleArn,
      @Value("${AWS_WEB_IDENTITY_TOKEN_FILE}") String tokenFileLocation,
      @Value("${AWS_REGION}") String awsRegion,
      @Value("${default_aws_value}") String awsDefaultValue,
      @Value("${default_aws_value_challenge_11}") String ctfValue,
      @Value("${ctf_enabled}") boolean ctfEnabled) {
    this.awsRoleArn = awsRoleArn;
    this.tokenFileLocation = tokenFileLocation;
    this.awsRegion = awsRegion;
    this.awsDefaultValue = awsDefaultValue;
    this.ctfValue = ctfValue;
    this.ctfEnabled = ctfEnabled;
  }

  @Override
  public String getAnswer() {
    return getChallenge11Value();
  }

  private String getChallenge11Value() {
    if (!ctfEnabled) {
      return getAWSChallenge11Value();
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
}
