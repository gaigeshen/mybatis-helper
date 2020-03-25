package me.gaigeshen.mybatis.helper.entity;

import me.gaigeshen.mybatis.helper.BaseEntity;
import me.gaigeshen.mybatis.helper.annotations.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author gaigeshen
 */
@Table(id = "identity")
public class User extends BaseEntity<Long> {
  private String username;
  private Integer age;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
