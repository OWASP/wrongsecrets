package org.owasp.wrongsecrets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TomcatVirtualThreadsTest {

  @Autowired private ServletWebServerApplicationContext context;

  @Test
  void mainConnectorUsesVirtualThreads() {
    var webServer = assertInstanceOf(TomcatWebServer.class, context.getWebServer());
    assertInstanceOf(
        VirtualThreadExecutor.class,
        webServer.getTomcat().getConnector().getProtocolHandler().getExecutor());
  }
}
