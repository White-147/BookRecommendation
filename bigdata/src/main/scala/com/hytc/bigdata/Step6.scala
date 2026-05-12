package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/19 14:26
 * @Description: step6 获取商品相似度矩阵
 */
object Step6 {
  def step6Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val sqlRDD: RDD[(String, Double)] = spark
      .table("step5").rdd.map((x: Row) => {
      val callNo: String = x.getAs[String]("call_no")
      val coefficient: Double = x.getAs[Double]("coefficient")
      (callNo, coefficient)
    })

    val matrixRDD: RDD[(String, String, String)] = sqlRDD.map((x: (String, Double)) => {
      val ids: Array[String] = x._1.split(",")
      (ids(0), ids(1) + "~" + x._2)
    }).reduceByKey((_: String) + "," + (_: String))
      .map((x: (String, String)) => (x._1, x._2, "step6"))

    matrixRDD.toDF("cert_id", "coefficient","part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step6")
  }
}