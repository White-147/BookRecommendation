package com.hytc.util

import java.util.Properties

/**
 * JDBC 回写配置（外置化）
 * 优先级：环境变量 BOOK_DB_* > config.properties 中的 db.* > 本机默认值
 *
 *   - 连接地址 BOOK_DB_URL      默认 jdbc:mysql://localhost:3306/library
 *   - 用户名   BOOK_DB_USER     默认 root
 *   - 密码     BOOK_DB_PASSWORD 默认 root
 *
 * 本机 / Windows 原生链路直接使用默认值即可行为不变；
 * Docker 全链路由 docker-compose 的 bigdata 服务注入
 * BOOK_DB_URL=jdbc:mysql://mysql:3306/library 覆盖主机名。
 */
object JdbcConfig {
  private lazy val props: Properties = PropertiesUtil.load("config.properties")

  val driver: String = "com.mysql.cj.jdbc.Driver"
  val url: String = lookup("BOOK_DB_URL", "db.url", "jdbc:mysql://localhost:3306/library")
  val user: String = lookup("BOOK_DB_USER", "db.user", "root")
  val password: String = lookup("BOOK_DB_PASSWORD", "db.password", "root")

  private def lookup(envKey: String, propKey: String, default: String): String = {
    val fromEnv = sys.env.get(envKey).map(_.trim).filter(_.nonEmpty)
    val fromProps = Option(props.getProperty(propKey)).map(_.trim).filter(_.nonEmpty)
    fromEnv.orElse(fromProps).getOrElse(default)
  }
}
