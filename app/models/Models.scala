package models

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.Json

trait BacktestTrait {
  val date: LocalDateTime
  val displayId: String
  val id: Int
}

//Database model
case class Backtest(
  date: LocalDateTime,
  displayId: String,
  id: Int = 0
) extends BacktestTrait

//Database model
case class AlgoResult(
  algoName: String,
  annReturns: Double,
  annVolatility: Double,
  maxDrawdown: Double,
  sharpe: Double,
  sortino: Double,
  calmar: Option[Double],
  historicalValues: Seq[Double],
  historicalDates: Seq[LocalDate],
  backtestId: Int = 0
)

object AlgoResult {
  implicit val AlgoResultFormat = Json.format[AlgoResult]
}

case class BacktestWithResults(
  date: LocalDateTime,
  displayId: String,
  id: Int,
  algoResults: Seq[AlgoResult]
) extends BacktestTrait

object BacktestWithResults {
  def apply(backtest: Backtest, algoResults: Seq[AlgoResult]): BacktestWithResults = {
    new BacktestWithResults(
      date = backtest.date,
      displayId = backtest.displayId,
      id = backtest.id,
      algoResults = algoResults
    )
  }
  implicit val BacktestWithResultsFormat = Json.format[BacktestWithResults]
}
