package me.gaigeshen.mybatis.helper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The condition class, for find methods
 *
 * @author gaigeshen
 * @param <T> The entity type
 */
public final class Condition<T extends Entity<?>> {
  
  private Class<T> type;

  private int page;
  private int size;
  
  private String orderBy;
  private Sort sort;
  
  private List<Criteria> criterions = new ArrayList<>();

  /**
   * Create condition object with entity type
   *
   * @param type The entity type, must not be null
   */
  public Condition(Class<T> type) {
    if (type == null) {
      throw new IllegalArgumentException("type is required");
    }
    this.type = type;
  }

  public static <E extends Entity<?>> Condition<E> create(Class<E> type) {
    return new Condition<>(type);
  }

  public static <E extends Entity<?>> Condition<E> create(Class<E> type, int page, int size) {
    return new Condition<>(type).page(page).size(size);
  }
  
  public Class<?> getType() { return type; }

  public int getPage() { return page; }

  public int getSize() { return size; }
  
  int getSkip() { return (page - 1) * size; }
  
  String getOrderBy() { return orderBy; }
  
  String getSort() { return sort.name(); }

  /**
   * Set page parameter
   *
   * @param page Page, must great than zero
   * @return This condition
   */
  public Condition<T> page(int page) {
    if (page < 1) {
      throw new IllegalArgumentException("Invalid page");
    }
    this.page = page;
    return this;
  }

  /**
   * Set size parameter
   *
   * @param size Size, must great than zero
   * @return This condition
   */
  public Condition<T> size(int size) {
    if (size < 1) {
      throw new IllegalArgumentException("Invalid size");
    }
    this.size = size;
    return this;
  }

  /**
   * Set order by asc
   *
   * @param property The property name of entity
   * @return This condition
   */
  public Condition<T> asc(String property) {
    if (property != null) {
      this.orderBy = column(property);
      this.sort = Sort.ASC;
    }
    return this;
  }

  /**
   * Set order by desc
   *
   * @param property The property name of entity
   * @return This condition
   */
  public Condition<T> desc(String property) {
    if (property != null) {
      this.orderBy = column(property);
      this.sort = Sort.DESC;
    }
    return this;
  }

  // Internal method for generate sql statements
  boolean isSlice() {
    return this.page != 0 && this.size != 0;
  }
  // Internal method for generate sql statements
  List<Criteria> getCriterions() {
    return criterions;
  }

  /**
   * Returns {@code Criteria} object
   *
   * @return The {@code Criteria}
   */
  public Criteria where() {
    if (!criterions.isEmpty())
      throw new IllegalStateException("Duplicate call this method");
    
    Criteria criteria = new Criteria();
    this.criterions.add(criteria);
    return criteria;
  }

  /**
   * Clear all criterions, page, size, order and sort values
   *
   * @return This condition
   */
  public Condition<T> clear() {
    this.criterions.clear();
    
    this.page = 0;
    this.size = 0;
    
    this.orderBy = null;
    this.sort = null;
    
    return this;
  }

  /**
   * Internal method, returns column name value
   *
   * @param property The property
   * @return The column name
   */
  private String column(String property) {
    return ResultMappings.getColumn(type, property);
  }

  /**
   * Criteria class
   *
   * @author gaigeshen
   */
  public class Criteria {
    
    private List<Criterion> criteria = new ArrayList<>();
    
    public List<Criterion> getCriteria() {
      return this.criteria;
    }
    
    public Criteria or() {
      Criteria criteria = new Criteria();
      criterions.add(criteria);
      return criteria;
    }
    
    public Condition<T> end() {
      return Condition.this;
    }

    public boolean isValid() {
      return this.criteria.size() > 0;
    }
    
    public Criteria isNull(String property) {
      if (StringUtils.isNotBlank(property)) {
        criterion(column(property), " is null");
      }
      return this;
    }
    
    public Criteria isNotNull(String property) {
      if (StringUtils.isNotBlank(property)) {
        criterion(column(property), " is not null");
      }
      return this;
    }
    
    public Criteria equalTo(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " = ", value);
      }
      return this;
    }
    
    public Criteria notEqualTo(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " != ", value);
      }
      return this;
    }
    
    public Criteria like(String property, String value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " like ", "%" + value + "%");
      }
      return this;
    }
    
    public Criteria llike(String property, String value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " like ", "%" + value);
      }
      return this;
    }
    
    public Criteria rlike(String property, String value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " like ", value + "%");
      }
      return this;
    }
    
    public Criteria between(String property, Object value1, Object value2) {
      if (StringUtils.isNotBlank(property) && value1 != null && value2 != null) {
        criterion(column(property), " between ", value1, value2);
      }
      return this;
    }
    
    public Criteria greaterThan(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " > ", value);
      }
      return this;
    }
    
    public Criteria greaterThanOrEqualTo(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " >= ", value);
      }
      return this;
    }
    
    public Criteria lessThan(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " < ", value);
      }
      return this;
    }
    
    public Criteria lessThanOrEqualTo(String property, Object value) {
      if (StringUtils.isNotBlank(property) && value != null) {
        criterion(column(property), " <= ", value);
      }
      return this;
    }

    /**
     * Append criterion with property and values, generate {@code in} sql statement
     *
     * @param property The entity property name value
     * @param value The values
     * @return This criteria
     */
    public Criteria in(String property, List<?> value) {
      if (StringUtils.isNotBlank(property) && value != null && !value.isEmpty()) {
        Object aValue = value.get(0);
        if (aValue instanceof String) {
          criterion(column(property), " in ", value.stream().map(v -> "'" + v + "'").collect(Collectors.toList()));
        } else {
          criterion(column(property), " in ", value);
        }
      }
      return this;
    }
    
    private void criterion(String column, String operator) {
      criteria.add(new Criterion(column, operator));
    }
    
    private void criterion(String column, String operator, Object value) {
      criteria.add(new Criterion(column, operator, value));
    }
    
    private void criterion(String column, String operator, Object value1, Object value2) {
      criteria.add(new Criterion(column, operator, value1, value2));
    }
  }

  /**
   * Criterion class
   *
   * @author gaigeshen
   */
  class Criterion {
    
    private String column;
    private String operator;

    private Object value;
    private Object secondValue;
    
    private boolean noValue;
    private boolean betweenValue;
    private boolean listValue;
    
    Criterion(String column, String operator) {
      this.column = column;
      this.operator = operator;
      this.noValue = true;
    }
    
    Criterion(String column, String operator, Object value) {
      this.column = column;
      this.operator = operator;
      this.value = value;
      
      if (value instanceof List<?>)
        this.listValue = true;
    }
    
    Criterion(String column, String operator, Object value, Object secondValue) {
      this.column = column;
      this.operator = operator;
      this.value = value;
      this.secondValue = secondValue;
      this.betweenValue = true;
    }
    
    public String getColumn() { return column; }

    public String getOperator() { return operator; }
    
    public String getCondition() { return getColumn() + getOperator(); }

    public Object getValue() { return value; }

    public Object getSecondValue() { return secondValue; }

    public boolean isNoValue() { return noValue; }

    public boolean isBetweenValue() { return betweenValue; }

    public boolean isListValue() { return listValue; }
  }

  /**
   * Sort class
   *
   * @author gaigeshen
   */
  enum Sort {
    ASC, DESC
  }
}
