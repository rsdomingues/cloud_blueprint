package checkout.simulation

import checkout.{BaseSimulation, Protocol}
import checkout.scenarios._
import io.gatling.core.Predef._

class EchoSimulation extends BaseSimulation {

  val echo = scenario("Echo").exec(
    Echo.echo
  )

  setUp(
    echo.inject(
      constantUsersPerSec(noOfUsersPerSec) during (durationTime)
    )
  ).assertions(
    global.responseTime.mean.lte(500),
    forAll.failedRequests.percent.is(0.0)
  ).protocols(Protocol.Application);

}
