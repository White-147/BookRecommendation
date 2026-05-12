package com.hytc.bigdata

import com.hytc.function.RelatedBookRecommend
import com.hytc.util.KafkaUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Minutes, StreamingContext}

/**
 * @Author: White Jiang
 * @Date: 2023/2/22 11:21
 * @Description: SparkStreaming实时推荐启动
 */
object SparkStreamingRunner {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[*]")
      .setAppName("streamingApp")
      .set("hive.exec.dynamic.partition.mode", "nonstrict")

    val ssc = new StreamingContext(sparkConf, Minutes(1))
    val topic = "userLog"

    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtil.getKafkaSteam(topic, ssc)
    val logDStream: DStream[String] = kafkaDStream.map((_: ConsumerRecord[String, String]).value())

    logDStream.foreachRDD(foreachFunc = (rdd: RDD[String]) => {
      val spark: SparkSession = SparkSession
        .builder()
        .config(sparkConf)
        .enableHiveSupport()
        .getOrCreate()

      import spark.implicits._

      val logRDD: RDD[(String, String, String, String)] = rdd.map((x: String) => {
        val strings: Array[String] = x.split(",")
        (strings(0), strings(1), strings(2), "userlog")
      })

      logRDD.toDF("cert_id", "call_no", "action", "part")
        .write.partitionBy("part")
        .mode(SaveMode.Append).format("hive")
        .saveAsTable("userlog")

      Step1.step1Spark(sparkConf, "calculatePreference")
      Step2.step2Spark(sparkConf, "preferenceMatrix")
      Step3.step3Spark(sparkConf, "concurrenceRelation")
      Step4.step4Spark(sparkConf, "concurrenceTimes")
      Step5.step5Spark(sparkConf, "pearsonCoefficient")
      Step6.step6Spark(sparkConf, "concurrenceMatrix")
      Step7.step7Spark(sparkConf, "recommendMatrix")
      Step8.step8Spark(sparkConf, "rejectLent")
      RelatedBookRecommend.relatedBookRecommend(sparkConf, "relatedBookRecommend")
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
