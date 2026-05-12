package com.hytc.function

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

import java.text.SimpleDateFormat
import java.util.Date
import scala.util.matching.Regex

/**
 * @Author: White Jiang
 * @Date: 2023/2/28 16:01
 * @Description: 新书推荐
 */
object NewBookRecommend {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local").setAppName("newBookRecommend")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val bookRDD: RDD[(String, String)] = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://hadoopPD:3306/library")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "book")
      .load().rdd.map((x: Row) => {
      val callNo: String = x.getAs[String]("M_CALL_NO")
      val publishYear: String = x.getAs[String]("M_PUB_YEAR")
      (callNo, publishYear)
    })

    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy")
    val date: Int = Integer.parseInt(dateFormat.format(now))

    val dateR1: Regex = "\\d{4}".r
    val dateR2: Regex = "\\d{4}.\\d{2}".r
    val dateR3: Regex = "\\d{4}.\\d{2}.\\d{2}".r
    val dateRDD: RDD[(String, String)] =
      bookRDD.filter((_: (String, String))._2 != null)
        .filter((x: (String, String)) => {
          dateR1.matches(x._2) ||
            dateR2.matches(x._2) ||
            dateR3.matches(x._2)
        })

    var minYear: Int = 10
    dateRDD.collect().foreach((d: (String, String)) => {
      val year: Int = Integer.parseInt(d._2.substring(0, 4))
      if (date - year < minYear && year < date)
        minYear = date - year
    })

    val resultRDD: RDD[(String, String)] = dateRDD.filter(x => {
      val year: Int = x._2.substring(0, 4).toInt
      date - year == minYear
    })

    resultRDD.map((_: (String, String))._1)
      .toDF("CALL_NO")
      .write.format("jdbc")
      .option("url", "jdbc:mysql://hadoopPD:3306/library")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "newbook")
      .mode(SaveMode.Overwrite)
      .save()
  }
}