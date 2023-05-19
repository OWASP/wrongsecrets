package org.owasp.wrongsecrets;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTPSessionListener with decorator: adds logging on new sessions started so we can keep track
 * somewhat.
 */
@Configuration
@Slf4j
public class SessionConfiguration {

  private static final AtomicInteger numberOfSessions = new AtomicInteger(0);

  @Bean
  public HttpSessionListener httpSessionListener() {
    return new HttpSessionListener() {
      @Override
      public void sessionCreated(HttpSessionEvent hse) {
        log.info(
            "Session created, currently there are {} sessions active",
            numberOfSessions.incrementAndGet());
      }

      @Override
      public void sessionDestroyed(HttpSessionEvent hse) {
        log.info(
            "Session destroyed, currently there are {} sessions active",
            numberOfSessions.decrementAndGet());
      }
    };
  }

  public AtomicInteger getCounter() {
    return numberOfSessions;
  }
}
