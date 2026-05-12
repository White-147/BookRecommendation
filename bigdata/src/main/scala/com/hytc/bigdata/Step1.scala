package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/14 20:08
 * @Description: step1 获取⽤户对于商品的偏好值
 */

// 偏好=借阅（60%） 收藏（25%） 浏览（15%） 100%
object Step1 {
  private def getCountRDD(lineRDD: RDD[((String, String, String), Int)],
                          action: String,
                          preference: BigDecimal): RDD[((String, String), BigDecimal)] = {
    lineRDD.filter((line: ((String, String, String), Int)) => line._1._3 == action)
      .map((line: ((String, String, String), Int)) => {
        val certID: String = line._1._1
        val callNo: String = line._1._2
        val value: Int = line._2
        val result: BigDecimal = preference - ((preference / 2.0) / BigDecimal(value.toDouble))
        ((certID, callNo), result)
      }).reduceByKey((_: BigDecimal).+(_: BigDecimal))
  }

  def step1Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val logRDD: RDD[((String, String, String), Int)] = spark
      .table("userlog").rdd.map((x: Row) => {
      val certID: String = x.getAs[String]("cert_id")
      val callNo: String = x.getAs[String]("call_no")
      val action: String = x.getAs[String]("action")
      ((certID, callNo, action), 1)
    }).reduceByKey((_: Int) + (_: Int))

    val consultCountRDD: RDD[((String, String), BigDecimal)] =
      getCountRDD(logRDD, "consultBook", BigDecimal(0.15))
    val collectCountRDD: RDD[((String, String), BigDecimal)] =
      getCountRDD(logRDD, "addCollect", BigDecimal(0.25))
    val lendCountRDD: RDD[((String, String), BigDecimal)] =
      getCountRDD(logRDD, "addLend", BigDecimal(0.6))

    val unionRDD: RDD[((String, String), BigDecimal)] = consultCountRDD.union(collectCountRDD).union(lendCountRDD)
    val preferenceRDD: RDD[((String, String), BigDecimal)] = unionRDD.reduceByKey((_: BigDecimal).+(_: BigDecimal))

    preferenceRDD.map((x: ((String, String), BigDecimal))
    => (x._1._1, x._1._2, x._2.toDouble, "step1"))
      .toDF("cert_id", "call_no", "preference", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step1")
  }
}