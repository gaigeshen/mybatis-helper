package me.gaigeshen.mybatis.helper.idea.plugin.util;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author gaigeshen
 */
public enum JdbcTypeMapping {

  BIT(Boolean.class),

  TINYINT(Integer.class),
  INT(Integer.class),
  BIGINT(Long.class),
  FLOAT(Double.class),
  DOUBLE(Double.class),
  
  DECIMAL(BigDecimal.class),
  
  VARCHAR(String.class),
  TEXT(String.class),
  LONGTEXT(String.class),
  
  DATE(Date.class),
  DATETIME(Date.class),
  
  UNKNOWN(String.class)
  ;
  
  private Class<?> javaType;
  
  private JdbcTypeMapping(Class<?> javaType) {
    this.javaType = javaType;
  }

  public Class<?> getJavaType() { return javaType; }
  
  public static JdbcTypeMapping fromJdbcType(String typeName) {
    try {
      return JdbcTypeMapping.valueOf(typeName);
    } catch (Exception e) {
      // throw new IllegalArgumentException("Invalid jdbc type or not supported yet, " + typeName);
      return UNKNOWN;
    }
  }
  
}
