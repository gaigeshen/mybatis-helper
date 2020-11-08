package me.gaigeshen.mybatis.helper.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

/**
 * @author gaigeshen
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String[].class)
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

  private static final String DELIMITER = ",";

  public static String[] parseArrayFromString(String text) {
    return text.split(DELIMITER);
  }

  public static String toStringFromArray(String[] array) {
    StringJoiner sj = new StringJoiner(DELIMITER);
    for (String value : array) {
      sj.add(value);
    }
    return sj.toString();
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, toStringFromArray(parameter));
  }

  @Override
  public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String values = rs.getString(columnName);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }

  @Override
  public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String values = rs.getString(columnIndex);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }

  @Override
  public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String values = cs.getString(columnIndex);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }
}
