package me.gaigeshen.mybatis.helper.entity;

import java.io.Serializable;

/**
 * Entity class
 *
 * @author gaigeshen
 * @param <ID> The id type of entity
 */
public interface Entity<ID extends Serializable> {
  /**
   * Returns id value
   *
   * @return The id value
   */
  ID getId();

  /**
   * Set id value
   *
   * @param id Id value
   */
  void setId(ID id);
}
