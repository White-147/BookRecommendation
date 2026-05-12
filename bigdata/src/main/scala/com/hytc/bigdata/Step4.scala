package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/19 13:40
 * @Description: step4 统计商品的共现次数
 */
object Step4 {
  def step4Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val step2RDD: RDD[(String, String)] = spark
      .table("step2").rdd.map((x: Row) => {
      val callNo: String = x.getAs[String]("call_no")
      val preference: String = x.getAs[String]("preference")
      (callNo, preference)
    })

    val step3RDD: RDD[(String, String)] = spark
      .table("step3").rdd.map((x: Row) => {
      val str1: String = x.getAs[String]("call_no1")
      val str2: String = x.getAs[String]("call_no2")
      (str1, str2)
    })

    val step2Array: Array[(String, String)] = step2RDD.collect()

    val result: RDD[(String, String, String, String, String)] =
      step3RDD.map((bookId: (String, String)) => {
      val line: Array[String] = new Array[String](4)
      step2Array.foreach((strings: (String, String)) => {
        var userid = ""
        if (strings._2.contains(",")) {
          val ids: Array[String] = strings._2.split(",")
          val users: Array[String] = ids.map((id: String) => {
            val str: String = id.split("_")(0)
            str
          })
          users.foreach((u: String) => {
            userid += u + ","
          })
          userid = userid.substring(0, userid.length - 1)
        } else
          userid += strings._2.split("_")(0)
        if (bookId._1 == strings._1) {
          if (line(0) == null && line(1) == null) {
            line(0) = bookId._1
            line(1) = userid
          } else {
            line(2) = bookId._1
            line(3) = userid
          }
        }
        if (bookId._2 == strings._1) {
          if (line(0) == null && line(1) == null) {
            line(0) = bookId._2
            line(1) = userid
          } else {
            line(2) = bookId._2
            line(3) = userid
          }
        }
      })
      (line(0), line(1), line(2), line(3), "step4")
    })

    result.toDF("call_no1", "cert_id1", "call_no2", "cert_id2", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step4")
  }
}