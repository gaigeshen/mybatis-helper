package me.gaigeshen.mybatis.helper.spring.entity;

import me.gaigeshen.mybatis.helper.entity.BaseEntity;
import me.gaigeshen.mybatis.helper.annotations.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author gaigeshen
 */
@Table(id = "identity")
public class User extends BaseEntity<Long> {
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
