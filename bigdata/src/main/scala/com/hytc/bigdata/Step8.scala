package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/20 8:56
 * @Description: step8 剔除用户已借阅的推荐数据
 */
object Step8 {
  def step8Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    var step7RDD: RDD[(String, Double)] = spark
      .table("step7").rdd.map((x: Row) => {
        val user: String = x.getAs[String]("user")
        val recommend: Double = x.getAs[Double]("recommend")
        (user,recommend)
      })

    val logRDD: RDD[(String, String, String)] = spark
      .table("userlog").rdd.map((x: Row) => {
      val certID: String = x.getAs[String]("cert_id")
      val callNo: String = x.getAs[String]("call_no")
      val action: String = x.getAs[String]("action")
      (certID,callNo,action)
    })

    val actionArray: Array[String] =
      logRDD.filter((x: (String, String, String)) => x._3 == "addLend")
        .map((x: (String, String, String)) => (s"${x._1},${x._2}")).collect()

    actionArray.foreach((a: String) => {
      step7RDD = step7RDD.filter((strings: (String, Double)) => {
        strings._1 != a
      })
    })

    step7RDD.distinct().map((x: (String, Double)) => {
        val strings: Array[String] = x._1.split(",")
        (strings(0), strings(1), x._2)
      }).map((x: (String, String, Double)) => {
      val certID: String = x._1.trim
      val callNO: String = x._2.trim
      val recommend: Double = x._3
      (certID, callNO, recommend)
    }).toDF("CERT_ID", "CALL_NO", "recommend")
      .write.format("jdbc")
      .option("url", "jdbc:mysql://hadoopPD:3306/library")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "recommend")
      .mode(SaveMode.Overwrite)
      .save()
  }
}