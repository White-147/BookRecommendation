package com.hytc.bigdata

import org.scalatest.funsuite.AnyFunSuite

/**
 * Step5（皮尔逊相关系数）核心纯函数单测 —— 全用户空间版本。
 *
 * 语义：在系统全部用户的 0/1 向量上计算皮尔逊，包含"双方均未操作"的 (0,0) 样本点，
 * 因此：共同偏好 → 正相关；完全无共同用户 → 负相关；用户集完全相同 → 1.0（完全相关）。
 */
class Step5Test extends AnyFunSuite {

  private val universe: IndexedSeq[String] = IndexedSeq("u1", "u2", "u3", "u4", "u5", "u6")

  test("parseUsers 按逗号拆分、去空白、过滤空串、去重") {
    assert(Step5.parseUsers("a, b,,a ,c") === Array("a", "b", "c"))
    assert(Step5.parseUsers("x") === Array("x"))
  }

  test("parseUsers 空串 / null 返回空数组") {
    assert(Step5.parseUsers("") === Array.empty[String])
    assert(Step5.parseUsers(null) === Array.empty[String])
  }

  test("皮尔逊：两本书用户集合完全相同 -> 1.0（完全正相关）") {
    assert(Step5.pearsonCoefficient(Array("u1", "u2"), Array("u1", "u2"), universe) === 1.0)
  }

  test("皮尔逊：共同偏好（一书的用户是另一书子集） -> 正相关") {
    val c = Step5.pearsonCoefficient(Array("u1", "u2", "u3"), Array("u1", "u2"), universe)
    assert(c > 0.0, s"期望正相关，实际 $c")
  }

  test("皮尔逊：部分重叠但各有新增用户 -> 正相关") {
    val c = Step5.pearsonCoefficient(Array("u1", "u2", "u3"), Array("u1", "u2", "u4"), universe)
    assert(c > 0.0, s"期望正相关，实际 $c")
  }

  test("皮尔逊：完全无共同用户 -> 负相关（不算相似）") {
    val c = Step5.pearsonCoefficient(Array("u1", "u2"), Array("u3", "u4"), universe)
    assert(c < 0.0, s"期望负相关，实际 $c")
  }

  test("皮尔逊：任一书无行为用户 -> 0.0（无法比较）") {
    assert(Step5.pearsonCoefficient(Array.empty[String], Array("u1"), universe) === 0.0)
    assert(Step5.pearsonCoefficient(Array("u1"), Array.empty[String], universe) === 0.0)
  }

  test("皮尔逊：某用户集覆盖全部用户（向量无方差） -> 0.0（无定义守卫）") {
    assert(Step5.pearsonCoefficient(Array("u1", "u2", "u3", "u4", "u5", "u6"), Array("u1", "u2"), universe) === 0.0)
  }
}
