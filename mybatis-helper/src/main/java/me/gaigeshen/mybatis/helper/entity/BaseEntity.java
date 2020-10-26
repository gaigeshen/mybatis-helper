package me.gaigeshen.mybatis.helper.entity;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.mapping.ResultMapping;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * All entities should extends this class
 *
 * @author gaigeshen
 */
public abstract class BaseEntity<ID extends Serializable> implements Entity<ID> {

  private ID id;

  // Includes all result mappings but id properties
  private final transient List<ResultMappingValue> mappingValues;

  public BaseEntity() {
    mappingValues = new ArrayList<>();
    List<ResultMapping> mappings = ResultMappings.getMappings(getClass());
    for (ResultMapping mapping : mappings) {
      if (!mapping.getProperty().equals("id")) {
        mappingValues.add(new ResultMappingValue(mapping));
      }
    }
  }

  /**
   * Internal method for generate sql statements, do not delete it
   *
   * @return The result mappings value
   */
  List<ResultMappingValue> getValues() {
    try {
      for (ResultMappingValue mapping : mappingValues) {
        ResultMapping rm = mapping.getMapping();
        String property = rm.getProperty();
        Field field = FieldUtils.getField(getClass(), property, true);
        mapping.value(field.get(this));
      }
      return Collections.unmodifiableList(mappingValues);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Cannot get field value of type " + getClass().getName());
    }
  }

  @Override
  public ID getId() {
    return this.id;
  }

  @Override
  public void setId(ID id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    if (id == null) {
      return 0;
    }
    return this.id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (id == null)
      return false;
    if (obj == null || this.getClass() != obj.getClass())
      return false;
    BaseEntity<?> that = (BaseEntity<?>) obj;
    return this.id.equals(that.id);
  }

  /**
   *
   * @author gaigeshen
   */
  class ResultMappingValue {
    private ResultMapping mapping;
    private Object value;

    public ResultMappingValue(ResultMapping mapping) {
      this.mapping = mapping;
    }

    public String property() {
      return mapping.getProperty();
    }
    public String column() {
      return mapping.getColumn();
    }
    public Object getValue() {
      return value;
    }
    public void value(Object value) {
      this.value = value;
    }
    public ResultMapping getMapping() {
      return mapping;
    }

  }
}
