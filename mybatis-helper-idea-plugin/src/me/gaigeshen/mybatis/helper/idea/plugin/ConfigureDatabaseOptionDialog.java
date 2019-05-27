package me.gaigeshen.mybatis.helper.idea.plugin;

import com.intellij.openapi.ui.DialogWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Configure database options
 *
 * @author gaigeshen
 */
public class ConfigureDatabaseOptionDialog extends DialogWrapper {

  private String url;
  private String user;
  private String password;

  private JTextField txtUrl;
  private JTextField txtUser;
  private JPasswordField txtPassword;

  private ConfigureDatabaseOptionDialog(String url, String user, String password) {
    super(false);
    this.url = url;
    this.user = user;
    this.password = password;
    init();
    setTitle("Configure Database Options");
    setResizable(false);
  }

  public static ConfigureDatabaseOptionDialog createWithEmptyValue() {
    return new ConfigureDatabaseOptionDialog("", "", "");
  }

  public static ConfigureDatabaseOptionDialog createWithValue(@NotNull DatabaseOption option) {
    return new ConfigureDatabaseOptionDialog(option.getUrl(), option.getUser(), option.getPassword());
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Url:"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(txtUrl = new JTextField(url, 30), gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("User:"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(txtUser = new JTextField(user, 30), gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Password:"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add(txtPassword = new JPasswordField(password, 30), gbc);

    return panel;
  }

  /**
   * 返回输入的值是否有效，即确认数据库链接和用户名是否有效
   *
   * @return 输入的值是否有效
   */
  public boolean isOptionsValid() {
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

  @Override
  protected void doOKAction() {
    url = txtUrl.getText().trim();
    user = txtUser.getText().trim();
    password = new String(txtPassword.getPassword());
    super.doOKAction();
  }
}
