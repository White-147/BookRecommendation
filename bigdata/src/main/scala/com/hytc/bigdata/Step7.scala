package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/19 14:40
 * @Description: step7 计算出用户商品推荐值
 */
object Step7 {
  def step7Spark(sparkConf: SparkConf, appName: String): Unit = {
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

    val step6RDD: RDD[(String, String)] = spark
      .table("step6").rdd.map((x: Row) => {
      val certId: String = x.getAs[String]("cert_id")
      val coefficient: String = x.getAs[String]("coefficient")
      (certId, coefficient)
    })

    val user: Array[(String, String, String)] = step2RDD.map((x: (String, String)) => {
      var result: Array[(String, String, String)] = Array.empty
      if (x._2.contains(",")) {
        val userPreference: Array[String] = x._2.split(",")
        val userPreferenceArray: Array[(String, String, String)] = userPreference.map((str: String) => {
          val strings: Array[String] = str.split("_")
          (x._1, strings(0), strings(1))
        })
        result = userPreferenceArray
      } else {
        val strings: Array[String] = x._2.split("_")
        result = Array((x._1, strings(0), strings(1)))
      }
      result
    }).flatMap((x: Array[(String, String, String)]) => x).collect()

    val book: Array[(String, String, String)] = step6RDD.map((x: (String, String)) => {
      val concurrenceMatrix: Array[String] = x._2.split(",")
      val concurrenceMatrixArray: Array[(String, String, String)] =
        concurrenceMatrix.map((str: String) => {
          val strings: Array[String] = str.split("~")
          (x._1, strings(0), strings(1))
        })
      concurrenceMatrixArray
    }).flatMap((x: Array[(String, String, String)]) => x).collect()

    var resultList: List[(String, Double)] = List()
    for (item <- user)
      for (item1 <- book)
        if (item._1 == item1._1)
          resultList = resultList :+ (item._2 + "," + item1._2,
            (BigDecimal(item._3.toDouble) * BigDecimal(item1._3.toDouble)).toDouble)

    resultList.toDF("user", "recommend").rdd.map((x: Row) => {
      val user: String = x.getAs[String]("user")
      val recommend: Double = x.getAs[Double]("recommend")
      (user, recommend)
    }).reduceByKey((_: Double) + (_: Double))
      .map(x => (x._1, x._2, "step7"))
      .toDF("user", "recommend", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step7")
  }
}