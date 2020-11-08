package me.gaigeshen.mybatis.helper.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author gaigeshen
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(long[].class)
public class LongArrayTypeHandler extends BaseTypeHandler<long[]> {

  private static final String DELIMITER = ",";

  public static long[] parseArrayFromString(String text) {
    return Arrays.stream(text.split(DELIMITER)).mapToLong(Long::parseLong).toArray();
  }

  public static String toStringFromArray(long[] array) {
    if (Objects.isNull(array)) {
      return null;
    }
    StringJoiner sj = new StringJoiner(DELIMITER);
    for (Long value : array) {
      sj.add(value.toString());
    }
    return sj.toString();
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, long[] parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, toStringFromArray(parameter));
  }

  @Override
  public long[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String values = rs.getString(columnName);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }

  @Override
  public long[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String values = rs.getString(columnIndex);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }

  @Override
  public long[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String values = cs.getString(columnIndex);
    if (values == null) {
      return null;
    }
    return parseArrayFromString(values);
  }
}
