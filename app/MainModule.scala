import controllers.BacktestController
import services.BacktestService

/**
  * Add all the MacWire DI dependencies here.
  */
trait MainModule {
  import com.softwaremill.macwire._

  lazy val resultController = wire[BacktestController]

  lazy val resultService = wire[BacktestService]
}
