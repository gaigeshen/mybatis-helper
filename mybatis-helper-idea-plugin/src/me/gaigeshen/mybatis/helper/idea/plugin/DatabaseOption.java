package me.gaigeshen.mybatis.helper.idea.plugin;

import org.apache.commons.lang3.StringUtils;

/**
 * The database option
 *
 * @author gaigeshen
 */
public class DatabaseOption {
  private final String url;
  private final String user;
  private final String password;

  public DatabaseOption(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public boolean isValid() {
    return StringUtils.isNotBlank(getUrl()) && StringUtils.isNotBlank(getUser());
  }

  public String getUrl() {
    return url;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
