package me.gaigeshen.mybatis.helper;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity with create time
 *
 * @author gaigeshen
 */
public abstract class BaseAuditEntity<ID extends Serializable> extends BaseEntity<ID> {
  private Date createTime;

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
}
