package controllers

import play.api.mvc.{Action, Controller}

import services.BacktestRunService

class BacktestRunController(backtestRunService: BacktestRunService) extends Controller {
  def getAllBacktests = Action {
    val allBacktests = backtestRunService.getAllBacktests
    Ok(views.html.indexPage(allBacktests))
  }

  def getBacktestRun(displayId: String) = Action {
    val allBacktests = backtestRunService.getAllBacktests
    val displayedBacktest = backtestRunService.getBacktest(displayId)

    if (displayedBacktest.isDefined) {
      Ok(views.html.resultPage(allBacktests, displayedBacktest.get))
    } else {
      NotFound
    }
  }
}
