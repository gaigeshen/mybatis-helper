package me.gaigeshen.mybatis.helper.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author gaigeshen
 */
public final class StringNameUtils {

  private StringNameUtils() {}

  /**
   * Translate underline string value to camel string value
   *
   * @param param Underline string value
   * @return The camel string value
   */
  public static String underlineToCamel(String param) {
    if (!StringUtils.contains(param, 95)) return param;

    param = param.replaceAll("(_){2,}", "_")
            .replaceAll("^(_){1,}|(_){1,}$", "");
    
    param = param.trim();
    if (StringUtils.isBlank(param)) {
      return "";
    }
    char[] chrs = param.toCharArray();
    
    boolean flg = false;
    char[] chrs_ = null;
    for (int i = 0; i < chrs.length; i++) {
      char chr = chrs[i];
      if (flg) {
        chrs_ = ArrayUtils.add(chrs_, (char) (chr - 32));
        flg = false;
        continue;
      }
      if (chr == 95) {
        char next = chrs[i + 1];
        if (next >= 97 && next <= 122) {
          flg = true;
          continue;
        }
      }
      chrs_ = ArrayUtils.add(chrs_, chr);
    }
    
    return String.valueOf(chrs_);
  }

  /**
   * Translate camel string value to underline string value
   *
   * @param param Camel string value
   * @return Underline string value
   */
  public static String camelToUnderline(String param) {
    StringBuilder result = new StringBuilder();
    char[] arr = param.toCharArray();
    int index = 0;
    for (char chr : arr) {
      char cur = chr;
      if (cur >= 65 && cur <= 90) {
        cur += 32;
        if (index != 0) {
          result.append("_");
        }
      }
      result.append(cur);
      index++;
    }
    return String.valueOf(result);
  }
}
