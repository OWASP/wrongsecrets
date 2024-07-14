package org.owasp.wrongsecrets.challenges.cloud.challenge11;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.common.base.Strings;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Cloud challenge which uses IAM privilelge escalation (differentiating per cloud). */
@Component
@Slf4j
public class Challenge11Gcp extends FixedAnswerChallenge {

  private final String gcpDefaultValue;
  private final String projectId;
  private final String ctfValue;
  private final boolean ctfEnabled;

  public Challenge11Gcp(
      @Value("${default_gcp_value}") String gcpDefaultValue,
      @Value("${GOOGLE_CLOUD_PROJECT}") String projectId,
      @Value("${default_aws_value_challenge_11}") String ctfValue,
      @Value("${ctf_enabled}") boolean ctfEnabled) {
    this.gcpDefaultValue = gcpDefaultValue;
    this.projectId = projectId;
    this.ctfValue = ctfValue;
    this.ctfEnabled = ctfEnabled;
  }

  @Override
  public String getAnswer() {
    return getChallenge11Value();
  }

  private String getChallenge11Value() {
    if (!ctfEnabled) {
      return getGCPChallenge11Value();
    } else if (!Strings.isNullOrEmpty(ctfValue)
        && !Strings.isNullOrEmpty(gcpDefaultValue)
        && !ctfValue.equals(gcpDefaultValue)) {
      return ctfValue;
    }

    log.info("CTF enabled, skipping challenge11");
    return "please_use_supported_cloud_env";
  }

  private String getGCPChallenge11Value() {
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
    return gcpDefaultValue;
  }
}
