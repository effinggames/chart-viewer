import controllers.BacktestRunController
import services.BacktestRunService

/**
  * Add all the MacWire DI dependencies here.
  */
trait MainModule {
  import com.softwaremill.macwire._

  lazy val resultController = wire[BacktestRunController]

  lazy val resultService = wire[BacktestRunService]
}
