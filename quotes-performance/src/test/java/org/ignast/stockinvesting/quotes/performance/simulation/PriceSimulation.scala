package org.ignast.stockinvesting.quotes.performance.simulation

import io.gatling.core.Predef.intToFiniteDuration
import io.gatling.core.{CoreDsl}
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
        .get("http://localhost:8081/companies/#{id}")
        .header("Content-Type", "application/vnd.stockinvesting.quotes-v1.hal+json")
        .check(jsonPath("$._links.quotes:getQuotedPrice.href").saveAs("quotedPrice")))
      .exec(http("RetrieveQuotedPrice")
        .get("#{quotedPrice}")
        .header("Content-Type", "application/vnd.stockinvesting.quotes-v1.hal+json")
      ).exec( session => { println(session); session }).pause(2)


    val users = scenario("QueryPriceSimulation").exec(queryQuotedPrice)

    setUp(new Setup().adminSetsUpCompaniesForUsers.inject(atOnceUsers(8)),
        users.inject(
        nothingFor(4),
        rampUsers(10).during(10),
        constantUsersPerSec(10).during(60 * 5)))
      .assertions(
        details("RetrieveCompany").responseTime.max.lt(1000),
        details("RetrieveQuotedPrice").responseTime.max.lt(1000),
        details("RetrieveCompany").failedRequests.percent.lte(1),
        details("RetrieveQuotedPrice").failedRequests.percent.lte(1)
      )
}
