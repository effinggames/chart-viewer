package controllers

import play.api.mvc.{AnyContent, Action, Controller}

import services.BacktestService

class BacktestController(backtestRunService: BacktestService) extends Controller {
  /**
    * Gets all recent backtests and renders the index page html.
    * @return Returns rendered html for the index page.
    */
  def getAllBacktests: Action[AnyContent] = Action { request =>
    val allBacktests = backtestRunService.getAllBacktests

    Ok(views.html.indexPage(allBacktests))
  }

  /**
    * Gets a specific backtest and renders the result page html.
    * @return Returns rendered html for the results page.
    */
  def getBacktest(displayId: String): Action[AnyContent] = Action { request =>
    val allBacktests = backtestRunService.getAllBacktests
    val selectedBacktest = backtestRunService.getBacktest(displayId)

    selectedBacktest match {
      case Some(backtest) => Ok(views.html.resultPage(allBacktests, backtest))
      case _ => NotFound
    }
  }
}
