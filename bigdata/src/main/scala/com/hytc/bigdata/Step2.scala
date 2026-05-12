package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/19 9:59
 * @Description: step2 获取商品的偏好矩阵
 */
object Step2 {
  def step2Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val sqlRDD: RDD[(String, String)] = spark
      .table("step1").rdd.map((x: Row) => {
      val certID: String = x.getAs[String]("cert_id")
      val callNo: String = x.getAs[String]("call_no")
      val preference: Double = x.getAs[Double]("preference")
      (certID, callNo, preference)
    }).map((x: (String, String, Double)) => (x._2, x._1 + "_" + x._3))

    sqlRDD.reduceByKey((_: String) + "," + (_: String))
      .map(x => (x._1, x._2, "step2"))
      .toDF("call_no", "preference", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step2")
  }
}