package org.owasp.wrongsecrets.canaries;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CanaryToken {
    private final String manage_url;
    private final String memo;
    private final String channel;
    private final String time;
    private final AdditionalCanaryData additional_data;
}

/*
{"manage_url": "http://canarytokens.org/manage?token=y0all60b627gzp19ahqh7rl6j&auth=09193ea6b8def3e27a1a41f98d4265d7",
 "memo": "debugtoken",
 "additional_data": {"src_ip": "83.128.90.255", "useragent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36", "referer": null, "location": null},
 "channel": "HTTP", "time": "2022-03-07 06:14:36 (UTC)"}
 */
