package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Actual canaryToken as received by <a href="http://canarytokens.com/">canarytokens.com</a> Example
 * content: json: { "manage_url": "http://canarytokens.org/manage?token....", "memo": "debugtoken",
 * "additional_data": { "src_ip": "83.128.90.255", "useragent": "Mozilla/5.0 (Windows NT 10.0;
 * Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36",
 * "referer": null, "location": null }, "channel": "HTTP", "time": "2022-03-07 06:14:36 (UTC)" }
 *
 * @see org.owasp.wrongsecrets.canaries.AdditionalCanaryData
 */
@RequiredArgsConstructor
@Getter
public class CanaryToken {
  @JsonProperty("manage_url")
  private final String manageUrl;

  private final String memo;
  private final String channel;
  private final String time;

  @JsonProperty("additional_data")
  private final AdditionalCanaryData additionalData;
}
