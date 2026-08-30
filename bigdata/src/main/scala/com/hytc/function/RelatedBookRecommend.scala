package com.hytc.function

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import com.hytc.util.JdbcConfig

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
      .option("url", JdbcConfig.url)
      .option("driver", JdbcConfig.driver)
      .option("user", JdbcConfig.user)
      .option("password", JdbcConfig.password)
      .option("dbtable", "relatedbook")
      .mode(SaveMode.Overwrite)
      .save()
  }
}
