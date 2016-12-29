package services

import util.DatabaseHelper._
import util.DatabaseHelper.stockDB._

import models.{Backtest, BacktestWithResults, AlgoResult}

import scala.concurrent._

class BacktestService {
  def getAllBacktests: Seq[BacktestWithResults] = {
    blocking {
      val backtestQuery = quote {
        query[Backtest]
          .leftJoin(query[AlgoResult])
          .on((backtest, algoResult) => backtest.id == algoResult.backtestId)
      }
      val results = stockDB.run(backtestQuery).groupBy(_._1)
      val backtestWithResults = results.map { case (backtest, list) =>
        val results = list.flatMap(_._2)
        BacktestWithResults(backtest, results)
      }

      backtestWithResults.toSeq
    }
  }

  def getBacktest(displayId: String): Option[BacktestWithResults] = {
    blocking {
      val backtestQuery = quote {
        query[Backtest]
          .filter(_.displayId == lift(displayId))
          .leftJoin(query[AlgoResult])
          .on((backtest, algoResult) => backtest.id == algoResult.backtestId)
      }

      val results = stockDB.run(backtestQuery).groupBy(_._1)
      val backtestWithResults = results.headOption.map { case (backtest, list) =>
          val results = list.flatMap(_._2)
          BacktestWithResults(backtest, results)
      }

      backtestWithResults
    }
  }

}
