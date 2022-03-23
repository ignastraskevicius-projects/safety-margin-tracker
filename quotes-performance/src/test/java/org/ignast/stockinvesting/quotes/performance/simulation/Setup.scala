package org.ignast.stockinvesting.quotes.performance.simulation

import io.gatling.core.CoreDsl
import io.gatling.http.HttpDsl

class Setup extends CoreDsl with HttpDsl {
  implicit val configuration = io.gatling.core.Predef.configuration

  val companiesFeed = Array(
    Map("id" -> "1",
      "json" -> """{"id":1,"name":"Amazon","listings":[{"marketIdentifier":"XNAS", "stockSymbol":"AMZN"}]}"""),
    Map("id" -> "2",
      "json" -> """{"id":2,"name":"Microsoft","listings":[{"marketIdentifier":"XNAS", "stockSymbol":"MSFT"}]}"""),
    Map("id" -> "3",
      "json" -> """{"id":3,"name":"Alibaba","listings":[{"marketIdentifier":"XHKG", "stockSymbol":"9988"}]}"""),
    Map("id" -> "4",
      "json" -> """{"id":4,"name":"Over The Wire","listings":[{"marketIdentifier":"XASX", "stockSymbol":"OTW"}]}"""),
    Map("id" -> "5",
      "json" -> """{"id":5,"name":"Boeing","listings":[{"marketIdentifier":"XNYS", "stockSymbol":"BA"}]}"""),
    Map("id" -> "6",
      "json" -> """{"id":6,"name":"Astrazeneca","listings":[{"marketIdentifier":"XLON", "stockSymbol":"AZN"}]}"""),
    Map("id" -> "7",
      "json" -> """{"id":7,"name":"Constallation Software","listings":[{"marketIdentifier":"XTSE", "stockSymbol":"CSU"}]}"""),
    Map("id" -> "8",
      "json" -> """{"id":8,"name":"Volkswagen","listings":[{"marketIdentifier":"XFRA", "stockSymbol":"VOW3"}]}"""),
  )

  val createCompanies = feed(companiesFeed)
    .exec(http("RetrieveRoot")
      .get("http://localhost:8081/")
      .check(jsonPath("$._links.quotes:companies.href").saveAs("companies")))
    .exec(http("Create Company")
      .put("#{companies}")
      .header("Content-Type", "application/vnd.stockinvesting.quotes-v1.hal+json")
      .body(StringBody("#{json}")))

  val adminSetsUpCompaniesForUsers = scenario("SetUpCompaniesForUsers").exec(createCompanies)
}
