package me.gaigeshen.mybatis.helper.idea.plugin.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库工具类
 * 
 * @author gaigeshen
 */
public final class DatabaseUtils {
  
  private DatabaseUtils() {}

  static {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Could not register mysql driver", e);
    }
  }

  /**
   * 根据链接地址分析数据库名称
   *
   * @param url 链接地址
   * @return 数据库名称
   */
  public static String databaseName(String url) {
    // jdbc:mysql://1.2.3.4:3306/tsleasing?useSSL=false
    if (StringUtils.isBlank(url)) {
      throw new IllegalArgumentException("Url is blank");
    }
    int slashIndex = StringUtils.lastIndexOf(url, "/");
    int paramIndex = StringUtils.lastIndexOf(url, "?");
    if (paramIndex != -1) {
      return StringUtils.mid(url, slashIndex + 1, paramIndex - slashIndex - 1);
    } else {
      return StringUtils.mid(url, slashIndex, 999);
    }
  }

  /**
   * 获取指定数据库表的列
   * 
   * @param url 数据库链接
   * @param user 用户名
   * @param password 密码
   * @param tableName 数据库表
   * @return 列名称和列数据类型的映射
   * @throws SQLException 发生数据库异常
   */
  public static Map<String, String[]> columnTypes(String url, String user, String password, String tableName) throws SQLException {

    Map<String, String[]> columnTypes = new LinkedHashMap<>();

    try (Connection conn = DriverManager.getConnection(url, user, password);
         Statement pstmt = conn.createStatement()) {
      
      ResultSet result = pstmt.executeQuery("show full fields from " + tableName);
      
      while (result.next()) {
        columnTypes.put(result.getString("Field"),
            new String[] {
                result.getString("Comment"),
                result.getString("Type")});
      }
    }

    return columnTypes;

  }
  
  /**
   * 获取所有的数据库表名称和对应的字段列表
   * 
   * @param url 数据库链接
   * @param user 用户名
   * @param password 密码
   * @return 数据库表名称和对应的字段列表
   * @throws SQLException 发生数据库异常
   */
  public static Map<String, List<String>> tableFields(String url, String user, String password) throws SQLException {
    
    Map<String, List<String>> tableFields = new LinkedHashMap<>();
    
    try (Connection conn = DriverManager.getConnection(url, user, password);
         Statement pstmt = conn.createStatement()) {

      DatabaseMetaData metaData = conn.getMetaData();
      ResultSet result = metaData.getTables(null, null, null, new String[] { "table" });

      while (result.next()) {
        String tableName = result.getString("table_name");
        
        ResultSet columns = pstmt.executeQuery("desc " + tableName);
        
        List<String> fields = new ArrayList<>();
        while (columns.next()) {
          fields.add(columns.getString("Field"));
        }
        
        tableFields.put(tableName, fields);
      }
    }

    return tableFields;
  }

  /**
   * 获取所有的数据库表名称
   * 
   * @param url 数据库链接
   * @param user 用户名
   * @param password 密码
   * @return 数据库表名称的集合
   * @throws SQLException 发生数据库异常
   */
  public static List<String> tableNames(String url, String user, String password) throws SQLException {
    
    List<String> tables = new ArrayList<>();

    try (Connection conn = DriverManager.getConnection(url, user, password)) {

      DatabaseMetaData metaData = conn.getMetaData();
      ResultSet result = metaData.getTables(null, null, null, new String[] { "table" });

      while (result.next()) {
        tables.add(result.getString("table_name"));
      }
    }

    return tables;
  }
  
}
