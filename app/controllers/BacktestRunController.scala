package controllers

import play.api.mvc.{Action, Controller}

import services.BacktestRunService

class BacktestRunController(backtestRunService: BacktestRunService) extends Controller {
  def getAllBacktests = Action {
    val allBacktests = backtestRunService.getAllBacktestRuns
    Ok(views.html.indexPage(allBacktests))
  }

  def getBacktestRun(id: String) = Action {
    val allBacktests = backtestRunService.getAllBacktestRuns
    val displayedBacktest = backtestRunService.getBacktestRun(id)

    if (displayedBacktest.isDefined) {
      Ok(views.html.resultPage(allBacktests, displayedBacktest.get))
    } else {
      NotFound
    }
  }
}
