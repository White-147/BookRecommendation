package com.hytc.function

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/3/1 10:35
 * @Description: 相似图书
 */
object RelatedBookRecommend {
  def relatedBookRecommend(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val step5RDD: RDD[(String, String)] = spark
      .table("step5").rdd.map((x: Row) => {
      val callNo: String = x.getAs[String]("call_no")
      callNo
    }).map((x: String) => {
      val strings: Array[String] = x.split(",")
      (strings(0), strings(1))
    })

    step5RDD.distinct().
      toDF("CALL_NO1", "CALL_NO2")
      .write.format("jdbc")
      .option("url", "jdbc:mysql://hadoopPD:3306/library")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "relatedbook")
      .mode(SaveMode.Overwrite)
      .save()
  }
}
