package services

import util.DatabaseHelper._
import util.DatabaseHelper.stockDB._

import models.{Backtest, BacktestWithResults, AlgoResult}

import scala.concurrent._

class BacktestRunService {
  def getAllBacktests: Seq[BacktestWithResults] = {
    blocking {
      val backtestQuery = quote {
        query[Backtest]
      }
      val algoResultsQuery = quote {
        query[AlgoResult]
      }
      val backtests = stockDB.run(backtestQuery).toVector
      val algoResults = stockDB.run(algoResultsQuery).toVector
      val algoResultsMap = algoResults.groupBy(_.backtestId)
      backtests.map( i =>
        BacktestWithResults(i, algoResultsMap.getOrElse(i.id, Seq.empty))
      )
    }
  }

  def getBacktest(displayId: String): Option[BacktestWithResults] = {
    blocking {
      val backtestQuery = quote {
        query[Backtest].filter(_.displayId == lift(displayId))
      }
      val backtestRun = stockDB.run(backtestQuery).headOption.map { backtest =>
        val algoResultsQuery = quote {
          query[AlgoResult].filter(_.backtestId == lift(backtest.id))
        }
        val algoResults = stockDB.run(algoResultsQuery).toVector
        BacktestWithResults(backtest, algoResults)
      }

      backtestRun
    }
  }

}
