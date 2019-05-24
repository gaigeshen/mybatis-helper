package me.gaigeshen.mybatis.helper.annotations;

import java.lang.annotation.*;

/**
 * Database table
 *
 * @author gaigeshen
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
  /**
   * Table name
   *
   * @return Table name default blank value
   */
  String value() default "";

  /**
   * The id column name of this table
   *
   * @return Id column name, default is "id"
   */
  String id() default "id";
}
