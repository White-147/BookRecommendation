package com.hytc.util

import org.apache.hadoop.shaded.com.nimbusds.jose.util.StandardCharset
import org.apache.hadoop.shaded.org.jline.utils.InputStreamReader

import java.util.Properties

/**
 * @Author: White Jiang
 * @Date: 2023/2/23 16:58
 * @Description: 获取配置工具
 */
object PropertiesUtil {
  def load(propertiesName: String): Properties = {
    val prop = new Properties()
    prop.load(new InputStreamReader
    (Thread.currentThread().getContextClassLoader.getResourceAsStream(propertiesName),
      StandardCharset.UTF_8))
    prop
  }
}
