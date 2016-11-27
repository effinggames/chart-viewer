package util

import scala.annotation.tailrec

object MathHelper {
  /**
    * Rounds a number to X decimals.
    * @param number Number to round.
    * @param decimalLimit Limit to X decimal digits.
    * @return Returns the rounded number.
    */
  def roundDecimals(number: Double, decimalLimit: Int): Double = {
    val roundingFactor = math.pow(10, decimalLimit)
    math.round(number * roundingFactor) / roundingFactor
  }

  /**
    * Gets the mean for a list of numbers.
    * @return Returns the mean as a float.
    */
  def mean[T](items: Traversable[T])(implicit n: Numeric[T]): Double = {
    n.toDouble(items.sum) / items.size.toDouble
  }

  /**
    * Gets the variance for a list of numbers.
    * @return Returns the variance as a float.
    */
  def variance[T](items: Traversable[T])(implicit n: Numeric[T]): Double = {
    val itemMean = mean(items)
    val sumOfSquares = items.foldLeft(0d)((total, item)=>{
      val itemValue = n.toDouble(item)
      val square = math.pow(itemValue - itemMean, 2)
      total + square
    })
    sumOfSquares / items.size.toDouble
  }

  /**
    * Gets the standard deviation for a list of numbers.
    * @return Returns the standard deviation as a float.
    */
  def stdDeviation[T](items: Traversable[T])(implicit n: Numeric[T]): Double = {
    math.sqrt(variance(items))
  }

  /**
    * Linear time median algorithm.
    * Source: http://stackoverflow.com/questions/4662292/scala-median-implementation
    */
  @tailrec private def findKMedian(arr: Seq[Double], k: Int): Double = {
    val a = arr(scala.util.Random.nextInt(arr.size))
    val (s, b) = arr partition (a >)
    if (s.size == k) a
    // The following test is used to avoid infinite repetition
    else if (s.isEmpty) {
      val (s, b) = arr partition (a ==)
      if (s.size > k) a
      else findKMedian(b, k - s.size)
    } else if (s.size < k) findKMedian(b, k - s.size)
    else findKMedian(s, k)
  }

  /**
    * Gets the median for a list of numbers.
    * @return Returns the median as a float.
    */
  def findMedian[T](items: Traversable[T])(implicit n: Numeric[T]): Double = {
    val indexedArr = items.map(n.toDouble).toVector
    if (indexedArr.size % 2 == 0) {
      (findKMedian(indexedArr, indexedArr.size / 2 - 1) + findKMedian(indexedArr, indexedArr.size / 2)) / 2
    } else {
      findKMedian(indexedArr, (indexedArr.size - 1) / 2)
    }
  }
}
