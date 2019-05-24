package me.gaigeshen.mybatis.helper;

/**
 * @author gaigeshen
 */
public class MybatisHelperConfigurerException extends MybatisHelperException {
  public MybatisHelperConfigurerException() {
  }

  public MybatisHelperConfigurerException(String message) {
    super(message);
  }

  public MybatisHelperConfigurerException(Throwable cause) {
    super(cause);
  }

  public MybatisHelperConfigurerException(String message, Throwable cause) {
    super(message, cause);
  }
}
