package me.gaigeshen.mybatis.helper.idea.plugin;

import com.intellij.ide.util.PropertiesComponent;

/**
 * For store database option
 *
 * @author gaigeshen
 */
public class DatabaseOptionStore {

  public static final String OPTION_URL = "mybatis-helper.db.url";
  public static final String OPTION_USER = "mybatis-helper.db.user";
  public static final String OPTION_PASSWORD = "mybatis-helper.db.password";

  /**
   *
   * Returns current database option
   *
   * @return Current database option
   */
  public static DatabaseOption get() {
    PropertiesComponent properties = PropertiesComponent.getInstance();
    return new DatabaseOption(
            properties.getValue(OPTION_URL, ""),
            properties.getValue(OPTION_USER, ""),
            properties.getValue(OPTION_PASSWORD, ""));
  }

  /**
   * Set database options
   *
   * @param option The database option
   */
  public static void set(DatabaseOption option) {
    PropertiesComponent properties = PropertiesComponent.getInstance();
    properties.setValue(OPTION_URL, option.getUrl());
    properties.setValue(OPTION_USER, option.getUser());
    properties.setValue(OPTION_PASSWORD, option.getPassword());
  }
}
