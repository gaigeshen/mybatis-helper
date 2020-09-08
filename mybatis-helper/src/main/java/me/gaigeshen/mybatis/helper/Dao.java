package me.gaigeshen.mybatis.helper;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Super data access interface, dao interface MUST extends this interface directly
 *
 * @author gaigeshen
 * @param <T> The entity type
 * @param <ID> The identity type of entity
 */
public interface Dao<T extends Entity<ID>, ID extends Serializable> {
  /**
   * Save one entity
   *
   * @param entity Entity
   */
  void saveOne(T entity);

  /**
   * Save many entities
   *
   * @param entities The entities
   */
  void save(List<T> entities);

  /**
   * Save or update entity, if the entity has id value, then update it
   *
   * @param entity Entity
   */
  default void saveOrUpdate(T entity) {
    ID id = entity.getId();
    if (id == null) {
      saveOne(entity);
    } else {
      update(entity);
    }
  }

  /**
   * Delete by entity id
   *
   * @param id Entity id
   */
  void deleteOne(ID id);

  /**
   * Delete by conditions
   *
   * @param condition Conditions
   */
  void delete(Condition<T> condition);

  /**
   * Find entity by id
   *
   * @param id The id
   * @return The entity
   */
  T findOne(ID id);

  /**
   * Find first record by conditions
   *
   * @param condition The conditions
   * @return The first record entity
   */
  T findFirst(Condition<T> condition);

  /**
   * Find all records
   *
   * @return All records
   */
  default List<T> findAll() {
    return find(null);
  }

  /**
   * Find by conditions
   *
   * @param condition The conditions
   * @return Entities
   */
  List<T> find(Condition<T> condition);

  /**
   * Returns records count by conditions
   *
   * @param condition The conditions
   * @return Records count
   */
  long count(Condition<T> condition);

  /**
   * Returns paged entities by conditions
   *
   * @param condition The conditions
   * @return Paged entities
   * @deprecated Please use paging method instead
   */
  @Deprecated
  default PageData<T> sliceup(Condition<T> condition) {
    List<T> data = find(condition);
    if (data == null) {
      data = Collections.emptyList();
    }
    long total = count(condition);
    return new PageData<>(data, condition.getPage(), condition.getSize(), total);
  }

  /**
   * Returns paged entities by conditions
   *
   * @param condition The conditions
   * @return Paged entities
   */
  default PageData<T> paging(Condition<T> condition) {
    List<T> data = find(condition);
    if (data == null) {
      data = Collections.emptyList();
    }
    long total = count(condition);
    return new PageData<>(data, condition.getPage(), condition.getSize(), total);
  }

  /**
   * Check exists of entity id
   *
   * @param id The entity id
   * @return Returns true if this id exists
   */
  default boolean exists(ID id) {
    return findOne(id) != null;
  }

  /**
   * Check exists by conditions
   *
   * @param condition The conditions
   * @return Returns true or false
   */
  boolean exists(Condition<T> condition);

  /**
   * Update entity by id
   *
   * @param entity The entity with id value
   * @return Update result
   */
  boolean update(T entity);

  /**
   * Incremental update by id, can only update number properties
   *
   * @param entity The entity with id value
   * @param canNegative <code>true</code>: Can update to negative value
   * @return Update result
   */
  boolean updateIncremental(@Param("update") T entity, @Param("canNegative") boolean canNegative);

  /**
   * Update entities by condition
   *
   * @param update The values to be update
   * @param condition The condition
   * @return Update result
   */
  boolean updateCondition(@Param("update") T update, @Param("condition") T condition);

  /**
   * Update entity by id, and null value properties update to null
   *
   * @param entity The entity with id value
   * @return Update result
   */
  boolean updateNullable(T entity);

  /**
   * Update entities by condition
   *
   * @param update The values to be update, and null value properties update to null, exclude id property
   * @param condition The condition
   * @return Update result
   */
  boolean updateConditionNullable(@Param("update") T update, @Param("condition") T condition);

}
