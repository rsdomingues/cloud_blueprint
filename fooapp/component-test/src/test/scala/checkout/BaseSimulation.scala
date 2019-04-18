package checkout

import io.gatling.core.Predef._
import scala.concurrent.duration._

trait BaseSimulation extends Simulation {

  val durationTime = 30 seconds
  val noOfUsersPerSec = 1

}