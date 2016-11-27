package services

import util.DatabaseHelper._
import util.DatabaseHelper.stockDB._

import models.{BacktestRun, AlgoResult}

import scala.concurrent._

class BacktestRunService {
  def getAllBacktestRuns: Seq[BacktestRun] = {
    val fetchQuery = quote {
      query[AlgoResult]
    }
    val algoResults = blocking {
      stockDB.run(fetchQuery)
    }
    val groupedResults = algoResults.groupBy(_.backtestId)

    groupedResults.mapValues( algoResults =>
      BacktestRun(
        id = algoResults.head.backtestId,
        date = algoResults.head.date,
        algoResults = algoResults
      )
    ).values.toSeq
  }

  def getBacktestRun(id: String): Option[BacktestRun] = {
    val fetchQuery = quote {
      query[AlgoResult].filter(_.backtestId == lift(id))
    }
    val algoResults = blocking {
      stockDB.run(fetchQuery)
    }

    algoResults.headOption.map { i =>
      BacktestRun(
        id = id,
        date = i.date,
        algoResults = algoResults
      )
    }
  }

}
