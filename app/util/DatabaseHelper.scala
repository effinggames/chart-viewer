package util

import java.time.LocalDate

import io.getquill.{JdbcContext, PostgresDialect, SnakeCase}

object DatabaseHelper {
  val stockDB = new JdbcContext[PostgresDialect, SnakeCase]("stockDB")

  import stockDB._

  implicit val StringSeqDecoder: Decoder[Seq[String]] = decoder[Seq[String]] { resultSet => {
      index => {
        resultSet.getArray(index).getArray.asInstanceOf[Array[String]]
      }
    }
  }
  implicit val StringSeqEncoder: Encoder[Seq[String]] = encoder[Seq[String]]( resultSet =>
    (index, seq) => {
      val conn = resultSet.getConnection
      resultSet.setArray(index, conn.createArrayOf("TEXT", seq.toArray))
    },
    java.sql.Types.ARRAY
  )

  implicit val DoubleSeqDecoder: Decoder[Seq[Double]] = decoder[Seq[Double]] { resultSet => {
      index => {
        resultSet.getArray(index).getArray.asInstanceOf[Array[java.math.BigDecimal]].map(_.doubleValue())
      }
    }
  }
  implicit val DoubleSeqEncoder: Encoder[Seq[Double]] = encoder[Seq[Double]]( resultSet =>
    (index, seq) => {
      val conn = resultSet.getConnection
      resultSet.setArray(index, conn.createArrayOf("FLOAT8", seq.map(_.toString).toArray))
    },
    java.sql.Types.ARRAY
  )

  implicit val LocalDateSeqDecoder: Decoder[Seq[LocalDate]] = decoder[Seq[LocalDate]] { resultSet => {
      index => {
        resultSet.getArray(index).getArray.asInstanceOf[Array[java.sql.Date]].map(_.toLocalDate)
      }
    }
  }
  implicit val LocalDateSeqEncoder: Encoder[Seq[LocalDate]] = encoder[Seq[LocalDate]]( resultSet =>
    (index, seq) => {
      val conn = resultSet.getConnection
      resultSet.setArray(index, conn.createArrayOf("DATE", seq.map(_.toString).toArray))
    },
    java.sql.Types.ARRAY
  )
}
