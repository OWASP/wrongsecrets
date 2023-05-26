package org.owasp.wrongsecrets.canaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Canarytokens used to communicate with <a href="http://canarytokens.com/">canarytokens.com</a>.
 * canarytokens.com will send a CanaryToken with this AdditionalCanaryData.
 *
 * @see org.owasp.wrongsecrets.canaries.CanaryToken
 */
@RequiredArgsConstructor
@Getter
public class AdditionalCanaryData {

  @JsonProperty("src_ip")
  private final String srcIp;

  private final String useragent;
  private final String referer;
  private final String location;
}
/*
{"manage_url": "http://canarytokens.org/manage?token=y0all60b627gzp19ahqh7rl6j&auth=09193ea6b8def3e27a1a41f98d4265d7",
 "memo": "debugtoken",
 "additional_data": {"src_ip": "83.128.90.255", "useragent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36", "referer": null, "location": null},
 "channel": "HTTP", "time": "2022-03-07 06:14:36 (UTC)"}
 */
