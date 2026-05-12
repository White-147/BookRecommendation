package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/20 14:48
 * @Description: step5 计算皮尔逊相关系数
 */
object Step5 {
  private def parseUsers(userText: String): Array[String] = {
    Option(userText).getOrElse("")
      .split(",")
      .map(_.trim)
      .filter(_.nonEmpty)
      .distinct
  }

  private def pearsonCoefficient(firstUsers: Array[String], lastUsers: Array[String]): Double = {
    val unionUsers = (firstUsers ++ lastUsers).distinct
    if (unionUsers.length < 2) {
      0.0
    } else {
      val firstUserSet = firstUsers.toSet
      val lastUserSet = lastUsers.toSet
      val firstMatrix = unionUsers.map(user => if (firstUserSet.contains(user)) 1.0 else 0.0)
      val lastMatrix = unionUsers.map(user => if (lastUserSet.contains(user)) 1.0 else 0.0)
      val firstAverage = firstMatrix.sum / firstMatrix.length
      val lastAverage = lastMatrix.sum / lastMatrix.length

      var numerator = 0.0
      var firstDenominator = 0.0
      var lastDenominator = 0.0

      for (i <- firstMatrix.indices) {
        val firstDeviation = firstMatrix(i) - firstAverage
        val lastDeviation = lastMatrix(i) - lastAverage
        numerator += firstDeviation * lastDeviation
        firstDenominator += scala.math.pow(firstDeviation, 2)
        lastDenominator += scala.math.pow(lastDeviation, 2)
      }

      if (firstDenominator == 0.0 || lastDenominator == 0.0) {
        0.0
      } else {
        val coefficient = numerator / (scala.math.sqrt(firstDenominator) * scala.math.sqrt(lastDenominator))
        if (coefficient.isNaN || coefficient.isInfinity) 0.0 else coefficient
      }
    }
  }

  def step5Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val sqlRDD: RDD[(String, String, String, String)] = spark
      .table("step4").rdd.map((x: Row) => {
      val firstBook = x.getAs[String]("call_no1")
      val firstUser = x.getAs[String]("cert_id1")
      val lastBook = x.getAs[String]("call_no2")
      val lastUser = x.getAs[String]("cert_id2")
      (firstBook, firstUser, lastBook, lastUser)
    })

    val result: RDD[(String, String, BigDecimal)] = sqlRDD.map((x: (String, String, String, String)) => {
      var recommend: BigDecimal = 0.0
      val firstUser: String = x._2
      val lastUser: String = x._4

      val firstUsers: Array[String] = parseUsers(firstUser)
      val lastUsers: Array[String] = parseUsers(lastUser)

      recommend = BigDecimal(pearsonCoefficient(firstUsers, lastUsers))
      (x._1, x._3, recommend)
    })

    result.filter((x: (String, String, BigDecimal)) => x._3 > 0.0)
      .map((x: (String, String, BigDecimal)) => (s"${x._1},${x._2}", x._3.toDouble, "step5"))
      .distinct().toDF("call_no", "coefficient", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step5")
  }
}
