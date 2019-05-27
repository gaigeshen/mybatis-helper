package me.gaigeshen.mybatis.helper.idea.plugin.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author gaigeshen
 */
public final class NameUtils {

  private NameUtils() {}
  
  /**
   * 下划线转驼峰
   * 
   * @param param 参数值
   * @return 转换后的值
   */
  public static String underlineToCamel(String param) {

    if (!StringUtils.contains(param, 95)) return param;

    param = param.replaceAll("(_){2,}", "_").replaceAll("^(_){1,}|(_){1,}$", "");
    
    param = param.trim();
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
   * 驼峰转下划线，请确保参数为驼峰格式。
   * 
   * @param param 参数值
   * @return 转换后的值
   */
  public static String camelToUnderline(String param) {
    
    StringBuilder result = new StringBuilder();
    char[] arr = param.toCharArray();
    
    for (char chr : arr) {
      char cur = chr;
      
      if (cur >= 65 && cur <= 90) {
        cur += 32;
        result.append("_");
      }
      
      result.append(cur);
    }
    
    return String.valueOf(result);
  }
  
}
