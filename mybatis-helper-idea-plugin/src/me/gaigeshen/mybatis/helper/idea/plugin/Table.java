package me.gaigeshen.mybatis.helper.idea.plugin;

import java.util.StringJoiner;

/**
 * 数据库表
 *
 * @author gaigeshen
 */
public final class Table {

  private final String name;
  private final String[] fields;

  /**
   * 
   * @param name 名称
   * @param fields 列集合
   */
  public Table(String name, String[] fields) {
    this.name = name != null ? name : "";
    this.fields = fields != null ? fields : new String[0];
  }

  public String getName() {
    return name;
  }

  public String[] getFields() {
    return fields;
  }
  
  public String getFieldsDescription() {
    StringJoiner delimiter = new StringJoiner(", ");
    for (String field : fields) {
      delimiter.add(field);
    }
    return delimiter.toString();
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    
    Table other = (Table) obj;
    return name.equals(other.name);
  }

  @Override
  public String toString() {
    return name;
  }
}
