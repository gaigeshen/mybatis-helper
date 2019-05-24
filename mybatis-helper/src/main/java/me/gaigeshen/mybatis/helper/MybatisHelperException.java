package me.gaigeshen.mybatis.helper;

/**
 * @author gaigeshen
 */
public class MybatisHelperException extends RuntimeException {
  public MybatisHelperException() {
    super();
  }

  public MybatisHelperException(String message) {
    super(message);
  }

  public MybatisHelperException(Throwable cause) {
    super(cause);
  }

  public MybatisHelperException(String message, Throwable cause) {
    super(message, cause);
  }
}
