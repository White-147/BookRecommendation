package com.hytc.bigdata

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @Author: White Jiang
 * @Date: 2023/2/20 14:48
 * @Description: step5 计算皮尔逊相关系数
 *
 * 算法说明（对应论文 2.3 / 5.1.3）：
 * 本方法在**全用户空间**上为每本书构造 0/1 行为向量 U，即对系统中每一个用户，
 * 若用户对该书产生过行为则记为 1，否则记为 0。这样向量内存在(0,0)（双方都未操作）的样本点，
 * 皮尔逊系数可正可负：两本书被同一批用户共同偏好且被其余用户共同忽略时为正相关，
 * 从而"共同偏好 → 相似度更高"的协同过滤语义成立（论文图2-4 用户-图书向量矩阵的构建方式）。
 *
 * 注意：若只在两本书的用户并集上构造向量（无(0,0)样本），皮尔逊恒 ≤ 0，
 * 配合 `> 0.0` 过滤会导致推荐链路产出为空（此为之前的缺陷）。
 */
object Step5 {
  /** 将逗号拼接的用户串解析为用户数组（拆分、去空白、过滤空串、去重） */
  private[bigdata] def parseUsers(userText: String): Array[String] = {
    Option(userText).getOrElse("")
      .split(",")
      .map(_.trim)
      .filter(_.nonEmpty)
      .distinct
  }

  /**
   * 全用户空间下的皮尔逊相关系数。
   * @param firstUsers 对图书 A 产生过行为的用户集合
   * @param lastUsers  对图书 B 产生过行为的用户集合
   * @param universe   系统全部用户（含对 A、B 均未操作的用户，提供 (0,0) 样本点）
   */
  private[bigdata] def pearsonCoefficient(firstUsers: Array[String], lastUsers: Array[String], universe: IndexedSeq[String]): Double = {
    val firstUserSet = firstUsers.toSet
    val lastUserSet = lastUsers.toSet
    if (firstUserSet.isEmpty || lastUserSet.isEmpty) {
      // 任一书无行为用户，无法比较
      0.0
    } else if (firstUserSet == lastUserSet) {
      // 两本书的用户集合完全相同 => 行为向量全同 => 完全正相关（避免分母为 0 被误判为 0）
      1.0
    } else {
      val n = universe.length
      var covariance = 0.0
      var firstDenominator = 0.0
      var lastDenominator = 0.0
      var mean1 = 0.0
      var mean2 = 0.0
      // 先算均值
      var i = 0
      while (i < n) {
        if (firstUserSet.contains(universe(i))) mean1 += 1.0
        if (lastUserSet.contains(universe(i))) mean2 += 1.0
        i += 1
      }
      mean1 = mean1 / n
      mean2 = mean2 / n
      // 再算协方差与标准差平方
      i = 0
      while (i < n) {
        val firstMatrix = if (firstUserSet.contains(universe(i))) 1.0 else 0.0
        val lastMatrix = if (lastUserSet.contains(universe(i))) 1.0 else 0.0
        val firstDeviation = firstMatrix - mean1
        val lastDeviation = lastMatrix - mean2
        covariance += firstDeviation * lastDeviation
        firstDenominator += scala.math.pow(firstDeviation, 2)
        lastDenominator += scala.math.pow(lastDeviation, 2)
        i += 1
      }

      if (firstDenominator == 0.0 || lastDenominator == 0.0) {
        // 某向量无方差（例如该书被全部用户或无人偏好），皮尔逊无定义
        0.0
      } else {
        val coefficient = covariance / (scala.math.sqrt(firstDenominator) * scala.math.sqrt(lastDenominator))
        if (coefficient.isNaN || coefficient.isInfinity) 0.0 else coefficient
      }
    }
  }

  def step5Spark(sparkConf: SparkConf, appName: String): Unit = {
    sparkConf.setAppName(appName)
    val spark: SparkSession = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    // 用户全集（用于构造含 (0,0) 样本点的全空间向量）；演示规模直接 collect，大数据高频场景可用广播/Join
    val universe: IndexedSeq[String] = spark
      .table("userlog").select("cert_id").distinct()
      .collect().map(_.getAs[String]("cert_id")).toIndexedSeq
    val universeBc = spark.sparkContext.broadcast(universe)

    val sqlRDD: RDD[(String, String, String, String)] = spark
      .table("step4").rdd.map((x: Row) => {
      val firstBook = x.getAs[String]("call_no1")
      val firstUser = x.getAs[String]("cert_id1")
      val lastBook = x.getAs[String]("call_no2")
      val lastUser = x.getAs[String]("cert_id2")
      (firstBook, firstUser, lastBook, lastUser)
    })

    val result: RDD[(String, String, BigDecimal)] = sqlRDD.map((x: (String, String, String, String)) => {
      val firstUsers: Array[String] = parseUsers(x._2)
      val lastUsers: Array[String] = parseUsers(x._4)
      val coefficient: Double = pearsonCoefficient(firstUsers, lastUsers, universeBc.value)
      (x._1, x._3, BigDecimal(coefficient))
    })

    result.filter((x: (String, String, BigDecimal)) => x._3 > 0.0)
      .map((x: (String, String, BigDecimal)) => (s"${x._1},${x._2}", x._3.toDouble, "step5"))
      .distinct().toDF("call_no", "coefficient", "part")
      .write.partitionBy("part")
      .mode(SaveMode.Overwrite).format("hive")
      .saveAsTable("step5")
  }
}
