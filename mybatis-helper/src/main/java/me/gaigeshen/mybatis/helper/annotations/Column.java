package me.gaigeshen.mybatis.helper.annotations;

import java.lang.annotation.*;

/**
 * Table column
 *
 * @author gaigeshen
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
  /**
   * Column name
   *
   * @return Column name, default blank value
   */
  String value() default "";
}
