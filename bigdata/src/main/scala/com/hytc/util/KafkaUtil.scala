package com.hytc.util

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import java.lang
import java.util.Properties
import scala.collection.mutable

/**
 * @Author: White Jiang
 * @Date: 2023/2/23 17:12
 * @Description: Kafka连接工具
 */
object KafkaUtil {
  private val properties: Properties = PropertiesUtil.load("config.properties")
  private val broker_list: String = properties.getProperty("kafka.broker")

  var consumerConfigs: mutable.Map[String, Object] = collection.mutable.Map(
    // 初始化链接到集群的地址
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> broker_list,
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    // 标识消费者团体
    ConsumerConfig.GROUP_ID_CONFIG -> "userLog",
    // latest自动重置偏移量为最新
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: lang.Boolean)
  )

  def getKafkaSteam(topic: String, ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
    KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(topic), consumerConfigs)
    )
  }
}
