package models

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.Json

case class AlgoResult(
  algoName: String,
  date: LocalDateTime,
  annReturns: Double,
  annVolatility: Double,
  maxDrawdown: Double,
  sharpe: Double,
  sortino: Double,
  calmar: Option[Double],
  historicalValues: Seq[Double],
  historicalDates: Seq[LocalDate],
  backtestId: String = "foobar"
)

object AlgoResult {
  implicit val AlgoResultFormat = Json.format[AlgoResult]
}

case class BacktestRun(
  id: String,
  date: LocalDateTime,
  algoResults: Seq[AlgoResult]
)

object BacktestRun {
  implicit val BacktestRunFormat = Json.format[BacktestRun]
}
