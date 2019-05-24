package me.gaigeshen.mybatis.helper;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.util.*;

/**
 * This class is thread safe
 *
 * @author ecmybatis
 */
final class ResultMappings {

  private ResultMappings() {}
  
  // All properties mapping include id
  private final static Map<Class<?>, List<ResultMapping>> mappings = new HashMap<>();

  /**
   * MUST initialize with mybatis configuration
   *
   * @param configuration The mybatis configuration
   */
  static void initialize(Configuration configuration) {
    Collection<ResultMap> resultMaps = configuration.getResultMaps();
    resultMaps.forEach(rm -> {
      mappings.put(rm.getType(), rm.getResultMappings());
    });
  }

  /**
   * Returns column name
   *
   * @param type The entity type
   * @param property The property name
   * @return Column name
   */
  static String getColumn(Class<?> type, String property) {
    return getMapping(type, property).getColumn();
  }

  private static ResultMapping getMapping(Class<?> type, String property) {
    List<ResultMapping> mappings = getMappings(type);
    for (ResultMapping mapping : mappings) {
      if (mapping.getProperty().equals(property)) {
        return mapping;
      }
    }
    throw new IllegalStateException("No such mapping of type " + type.getName() + " map property " + property);
  }
  
  static List<ResultMapping> getMappings(Class<?> type) {
    List<ResultMapping> mappings1 = mappings.get(type);
    if (mappings1 == null)
    	return Collections.emptyList();
    return Collections.unmodifiableList(mappings1);
  }
}
