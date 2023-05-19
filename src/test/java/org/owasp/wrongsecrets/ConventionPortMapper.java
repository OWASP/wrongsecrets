package org.owasp.wrongsecrets;

import org.springframework.security.web.PortMapper;

public class ConventionPortMapper implements PortMapper {

  @Override
  public Integer lookupHttpPort(Integer httpsPort) {
    return (httpsPort != null) ? httpsPort - 1 : null;
  }

  @Override
  public Integer lookupHttpsPort(Integer httpPort) {
    return (httpPort != null) ? httpPort + 1 : null;
  }
}
