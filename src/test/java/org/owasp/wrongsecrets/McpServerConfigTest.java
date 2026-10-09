package org.owasp.wrongsecrets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.tomcat.TomcatWebServerFactory;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

class McpServerConfigTest {

  @Test
  void additionalConnectorUsesVirtualThreadsWhenEnabled() {
    var connector = additionalConnector(true);
    assertInstanceOf(VirtualThreadExecutor.class, connector.getProtocolHandler().getExecutor());
  }

  @Test
  void additionalConnectorKeepsDefaultExecutorWhenDisabled() {
    var connector = additionalConnector(false);
    assertFalse(connector.getProtocolHandler().getExecutor() instanceof VirtualThreadExecutor);
  }

  private Connector additionalConnector(boolean virtualThreadsEnabled) {
    var config = new McpServerConfig();
    ReflectionTestUtils.setField(config, "mcpPort", 8090);
    var environment =
        new MockEnvironment()
            .withProperty("spring.threads.virtual.enabled", Boolean.toString(virtualThreadsEnabled));
    var factory = new TomcatWebServerFactory() {};
    config.mcpConnectorCustomizer(environment).customize(factory);
    return factory.getAdditionalConnectors().getFirst();
  }
}
