package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/19 11:02
 * @Description: step3 得到商品的共现关系
 */
object Step3 {
  def step3Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val sqlRDD: RDD[(String, String)] = spark
      .table("step1").rdd.map((x: Row) => {
      val certId: String = x.getAs[String]("cert_id")
      val callNo: String = x.getAs[String]("call_no")
      (certId, callNo)
    })

    val bookIdList: List[String] = sqlRDD.map((_: (String, String))._2).collect().toList

    var resultList: List[String] = List()
    for (item <- bookIdList)
      for (item1 <- bookIdList)
        resultList = resultList :+ item + "\t" + item1

    resultList.map((x: String) => {
      val strings: Array[String] = x.split("\t")
      (strings(0), strings(1), "step3")
    }).filter((x: (String, String, String)) => x._1 != x._2)
      .toDF("call_no1", "call_no2", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step3")
  }
}