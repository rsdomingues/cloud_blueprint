package checkout.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Echo {

  val URI = "/api/v1/echo"

  val echo = exec(
      http("Echo")
        .post(URI)
        .header("Content-Type", "application/json")
        .body(ElFileBody("echoRequest.json"))
        .check(status.is(200))
  )
}
