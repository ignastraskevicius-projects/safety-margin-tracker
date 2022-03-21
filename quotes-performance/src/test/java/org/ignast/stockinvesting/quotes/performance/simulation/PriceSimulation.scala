package org.ignast.stockinvesting.quotes.performance.simulation

import io.gatling.core.Predef.intToFiniteDuration
import io.gatling.core.CoreDsl
import io.gatling.core.Predef.scenario
import io.gatling.core.scenario.Simulation
import io.gatling.http.HttpDsl

import scala.util.Random

class PriceSimulation extends Simulation with CoreDsl with HttpDsl {
    implicit val configuration = io.gatling.core.Predef.configuration

    val companiesFeed = Iterator.continually {
        Map("id" -> (Random.nextInt(7) + 1))
    }

    val queryQuotedPrice = feed(companiesFeed)
      .exec(http("RetrieveCompany")
        .get("http://localhost:8080/companies/#{id}")
        .header("Content-Type", "application/vnd.stockinvesting.quotes-v1.hal+json")
        .check(jsonPath("$._links.quotes:quotedPrice.href").saveAs("quotedPrice")))
      .exec(http("RetrieveQuotedPrice")
        .get("#{quotedPrice}")
        .header("Content-Type", "application/vnd.stockinvesting.quotes-v1.hal+json")
      ).pause(2)


    val users = scenario("QueryPriceSimulation").exec(queryQuotedPrice)

    setUp(users.inject(
        rampUsers(10).during(10),
        constantUsersPerSec(10).during(60)))
      .assertions(
        global.responseTime.max.lt(100),
        forAll.failedRequests.percent.lte(5)
      )
}
