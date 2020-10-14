package me.gaigeshen.mybatis.helper;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity with update time and create time
 *
 * @author gaigeshen
 */
public abstract class BaseModifiableEntity<ID extends Serializable> extends BaseAuditEntity<ID> {
  private Date updateTime;

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }
}
