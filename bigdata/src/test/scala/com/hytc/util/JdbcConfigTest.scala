package com.hytc.util

import org.scalatest.funsuite.AnyFunSuite

/**
 * JdbcConfig（JDBC 回写外置配置）默认值单测。
 * 注：断言基于本机默认值（config.properties 的 db.* / 内置默认），
 * 前提是执行环境未设置 BOOK_DB_URL / BOOK_DB_USER / BOOK_DB_PASSWORD 环境变量。
 */
class JdbcConfigTest extends AnyFunSuite {
  test("JdbcConfig 默认值符合本机配置") {
    assert(JdbcConfig.url == "jdbc:mysql://localhost:3306/library")
    assert(JdbcConfig.driver == "com.mysql.cj.jdbc.Driver")
    assert(JdbcConfig.user == "root")
    assert(JdbcConfig.password == "root")
  }
}
