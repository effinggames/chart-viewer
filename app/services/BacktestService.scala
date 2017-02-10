package services

import util.DatabaseHelper._
import util.DatabaseHelper.stockDB._

import models.{Backtest, BacktestWithResults, AlgoResult}

import scala.concurrent._

class BacktestService {
  /**
    * Gets all the available backtests.
    * @return Returns a seq of backtest objects, along with their algo results.
    */
  def getAllBacktests: Seq[BacktestWithResults] = {
    //Query for all the backtests, along with matching algo results.
    val allBacktestQuery = quote {
      query[Backtest]
        .leftJoin(query[AlgoResult])
        .on((backtest, algoResult) => backtest.id == algoResult.backtestId)
    }
    val results = blocking {
      stockDB.run(allBacktestQuery).groupBy(_._1)
    }
    val backtestWithResults = results.map { case (backtest, list) =>
      val results = list.flatMap(_._2)
      BacktestWithResults(backtest, results)
    }

    backtestWithResults.toSeq
  }

  /**
    * Gets the specified backtest if available.
    * @param displayId Display id of the backtest to look for.
    * @return Returns a backtest option, along with it's algo result.
    */
  def getBacktest(displayId: String): Option[BacktestWithResults] = {
    //Query for specified backtest, along with matching algo results.
    val backtestQuery = quote {
        query[Backtest]
          .filter(_.displayId == lift(displayId))
          .leftJoin(query[AlgoResult])
          .on((backtest, algoResult) => backtest.id == algoResult.backtestId)
      }

      val results = blocking {
        stockDB.run(backtestQuery).groupBy(_._1)
      }

      val backtestWithResults = results.headOption.map { case (backtest, list) =>
          val results = list.flatMap(_._2)
          BacktestWithResults(backtest, results)
      }

      backtestWithResults
  }

}
