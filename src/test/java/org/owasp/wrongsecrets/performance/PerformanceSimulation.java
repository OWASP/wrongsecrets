package org.owasp.wrongsecrets.performance;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class PerformanceSimulation extends Simulation {

  ChainBuilder browse =
      CoreDsl.repeat(50, "i")
          .on(exec(http("Challenge #{i}").get("/challenge/challenge-#{i}")).pause(1));

  HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080");

  ScenarioBuilder users = scenario("Challenges").exec(browse);

  {
    setUp(users.injectOpen(rampUsers(100).during(5))).protocols(httpProtocol);
  }
}
