package org.ignast.stockinvesting.quotes.performance.simulation

import io.gatling.core.CoreDsl
import io.gatling.core.Predef.scenario
import io.gatling.core.scenario.Simulation
import io.gatling.http.HttpDsl

class PriceSimulation extends Simulation with CoreDsl with HttpDsl {
    implicit val configuration = io.gatling.core.Predef.configuration

    val priceSimulation = scenario("PriceSimulation")
      .exec(
          http("quotesRoot").get("http://localhost:8080/")
      )

    setUp(priceSimulation.inject(atOnceUsers(1)))
}
