package me.gaigeshen.mybatis.helper;

import me.gaigeshen.mybatis.helper.annotations.Column;
import me.gaigeshen.mybatis.helper.annotations.Table;
import me.gaigeshen.mybatis.helper.util.StringNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Metadata of entity
 *
 * @author gaigeshen
 */
public class EntityMetadata {

  private final Class<? extends Entity<?>> entityClass;

  // Field name mapping column name
  private final Map<String, String> resultMappings;
  // Database table name
  private final String tableName;
  // Database table id column name
  private final String idColumn;

  private EntityMetadata(Class<? extends Entity<?>> entityClass,
                         Map<String, String> resultMappings,
                         String tableName,
                         String idColumn) {
    this.entityClass = entityClass;
    this.resultMappings = resultMappings;
    this.tableName = tableName;
    this.idColumn = idColumn;
  }

  /**
   * Create entity metadata by entity class parameter
   *
   * @param entityClass The entity class
   * @return The entity metadata
   */
  public static EntityMetadata create(Class<? extends Entity<?>> entityClass) {
    return new EntityMetadata(entityClass,
            resolveResultMappings(entityClass), resolveTableName(entityClass), resolveIdColumn(entityClass));
  }

  /**
   * Resolve id column name
   *
   * @param entityClass Entity class
   * @return Id column name
   */
  private static String resolveIdColumn(Class<?> entityClass) {
    Table annotation = entityClass.getAnnotation(Table.class);
    if (annotation != null) {
      String id = annotation.id();
      if (StringUtils.isNotBlank(id)) {
        return id;
      }
    }
    return "id";
  }

  /**
   * Resolve table name
   *
   * @param entityClass Entity class
   * @return Table name
   */
  private static String resolveTableName(Class<?> entityClass) {
    Table annotation = entityClass.getAnnotation(Table.class);
    if (annotation != null) {
      String value = annotation.value();
      if (StringUtils.isNotBlank(value)) {
        return value;
      }
    }
    return StringNameUtils.camelToUnderline(entityClass.getSimpleName());
  }

  /**
   * Resolve result mappings, field name mapping column name
   *
   * @param entityClass Entity class
   * @return Result mapping
   */
  private static Map<String, String> resolveResultMappings(Class<?> entityClass) {
    Map<String, String> result = new HashMap<>();
    Table tableAnnotation = entityClass.getAnnotation(Table.class);
    if (tableAnnotation != null) {
      String id = tableAnnotation.id();
      if (StringUtils.isNotBlank(id)) {
        result.put("id", id);
      }
    }
    for (Field field : FieldUtils.getAllFields(entityClass)) {
      if (field.getName().equals("mappingValues")) {
        continue;
      }
      if (field.getName().equals("id") && result.containsKey("id")) {
        continue;
      }
      field.setAccessible(true);
      Column annotation = field.getAnnotation(Column.class);
      if (annotation != null) {
        if (annotation.exclude()) {
          continue;
        }
        String value = annotation.value();
        if (StringUtils.isNotBlank(value)) {
          result.put(field.getName(), value);
          continue;
        }
      }
      result.put(field.getName(), StringNameUtils.camelToUnderline(field.getName()));
    }
    return result;
  }

  public Class<? extends Entity<?>> getEntityClass() {
    return entityClass;
  }

  public Map<String, String> getResultMappings() {
    return Collections.unmodifiableMap(resultMappings);
  }

  public String getTableName() {
    return tableName;
  }

  public String getIdColumn() {
    return idColumn;
  }

  public String[] getColumns() {
    return resultMappings.values().toArray(new String[0]);
  }
}
