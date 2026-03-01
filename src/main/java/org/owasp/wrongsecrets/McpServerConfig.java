package org.owasp.wrongsecrets;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.TomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures an additional HTTP connector so the MCP server endpoint is also available on a
 * dedicated port (default 8090). This simulates the realistic scenario where MCP servers run
 * alongside a main application on a separate port.
 */
@Configuration
public class McpServerConfig {

  @Value("${mcp.server.port:8090}")
  private int mcpPort;

  /** Adds a secondary Tomcat connector on the MCP port when the port value is positive. */
  @Bean
  public WebServerFactoryCustomizer<TomcatWebServerFactory> mcpConnectorCustomizer() {
    return factory -> {
      if (mcpPort > 0) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(mcpPort);
        factory.addAdditionalConnectors(connector);
      }
    };
  }
}
