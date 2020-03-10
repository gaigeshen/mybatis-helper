package me.gaigeshen.mybatis.helper;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity with create time and update time field
 *
 * @author gaigeshen
 */
public abstract class BaseAuditEntity<ID extends Serializable> extends BaseEntity<ID> {
  private Date createTime;
  private Date updateTime;

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }
}
