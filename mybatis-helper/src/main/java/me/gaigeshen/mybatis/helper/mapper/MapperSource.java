package me.gaigeshen.mybatis.helper.mapper;

import me.gaigeshen.mybatis.helper.Dao;
import me.gaigeshen.mybatis.helper.Entity;
import me.gaigeshen.mybatis.helper.EntityMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Mapper xml source
 *
 * @author gaigeshen
 */
public class MapperSource {

  private final Class<?> mapperClass;
  private final Class<?> entityClass;
  private final String source;

  /**
   * Create mapper source object with mapper class and entity class and source value
   *
   * @param mapperClass The mapper class
   * @param entityClass The entity class
   * @param source The source value
   */
  private MapperSource(Class<?> mapperClass, Class<?> entityClass, String source) {
    this.mapperClass = mapperClass;
    this.entityClass = entityClass;
    this.source = source;
  }

  /**
   * Returns mapper class
   *
   * @return The mapper class
   */
  public Class<?> getMapperClass() {
    return mapperClass;
  }

  /**
   * Returns entity class
   *
   * @return The entity class
   */
  public Class<?> getEntityClass() {
    return entityClass;
  }

  /**
   * Returns source value
   *
   * @return The source value
   */
  public String getSource() {
    return source;
  }

  /**
   * Create mapper source with mapper class
   *
   * @param mapperClass The mapper class
   * @return The mapper source object
   */
  public static MapperSource create(Class<?> mapperClass) {
    // The entity class of that mapper class
    // MUST not be null
    Class<? extends Entity<?>> entityClass = resolveEntityClass(mapperClass);
    Validate.notNull(entityClass, "Could not resolve entity class by: %s", mapperClass);

    EntityMetadata entityMetadata = EntityMetadata.create(entityClass);
    String idColumn = entityMetadata.getIdColumn();

    // Fields value, result maps
    StringBuilder fields = new StringBuilder();
    fields.append("<id property=\"id\" column=\"").append(idColumn).append("\"></id>");
    entityMetadata.getResultMappings().forEach((p, c) -> {
      if (!idColumn.equals(c)) {
        fields.append("\n").append("<result property=\"").append(p).append("\" column=\"").append(c).append("\"/>");
      }
    });

    return new MapperSource(mapperClass, entityClass,
          MAPPER_SOURCE_TEMPLATE
            .replaceAll("_namespace_", mapperClass.getName())
            .replaceAll("_table_", entityMetadata.getTableName())
            .replaceAll("_fields_", StringUtils.join(entityMetadata.getColumns(), ","))
            .replaceAll("_type_", entityClass.getName())
            .replaceAll("_resultMapId_", entityClass.getSimpleName() + "ResultMap")
            .replaceAll("_properties_", fields.toString())
            .replaceAll("_idColumn_", idColumn));
  }

  /**
   * Resolve entity class of mapper class
   *
   * @param mapperClass The mapper class
   * @return The entity class
   */
  @SuppressWarnings("unchecked")
  private static Class<? extends Entity<?>> resolveEntityClass(Class<?> mapperClass) {
    // The mapper class extends Dao interface
    // And the dao interface has typed parameter of entity
    // So we can resolve actual type of entity class
    Type[] genericInterfaces = mapperClass.getGenericInterfaces();
    if (genericInterfaces != null) {
      for (Type genericInterface : genericInterfaces) {
        if (genericInterface instanceof ParameterizedType) {
          ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
          if (parameterizedType.getRawType().equals(Dao.class)) {
            return (Class<? extends Entity<?>>) parameterizedType.getActualTypeArguments()[0];
          }
        }
      }
    }
    return null;
  }

  /**
   * Mapper source template, include sql statements and variables
   */
  private static final String MAPPER_SOURCE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
          "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
          "\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
          "<mapper namespace=\"_namespace_\">\n" +
          "  <sql id=\"table\">_table_</sql>\n" +
          "  <sql id=\"fields\">_fields_</sql>\n" +
          "  <resultMap type=\"_type_\" id=\"_resultMapId_\">\n" +
          "    _properties_\n" +
          "  </resultMap>\n" +
          "  <sql id=\"criteria\">\n" +
          "    <where>\n" +
          "      <foreach collection=\"criterions\" item=\"criteria\" separator=\"or\">\n" +
          "        <if test=\"criteria.valid\">\n" +
          "          <trim prefix=\"(\" suffix=\")\" prefixOverrides=\"and\">\n" +
          "            <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
          "\t\t\t  <choose>\n" +
          "\t\t\t    <when test=\"criterion.noValue\">\n" +
          "\t\t\t      and ${criterion.condition}\n" +
          "\t\t\t    </when>\n" +
          "\t\t\t    <when test=\"criterion.listValue\">\n" +
          "\t\t\t      and ${criterion.condition}\n" +
          "\t\t\t      <foreach collection=\"criterion.value\" item=\"listItem\" open=\"(\" close=\")\" separator=\",\">\n" +
          "\t\t\t        #{listItem}\n" +
          "\t\t\t      </foreach>\n" +
          "\t\t\t    </when>\n" +
          "\t\t\t    <when test=\"criterion.betweenValue\">\n" +
          "\t\t\t      and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
          "\t\t\t    </when>\n" +
          "\t\t\t    <otherwise>\n" +
          "\t\t\t      and ${criterion.condition} #{criterion.value}\n" +
          "\t\t\t    </otherwise>\n" +
          "\t\t\t  </choose>              \n" +
          "            </foreach>\n" +
          "          </trim>\n" +
          "        </if>\n" +
          "      </foreach>\n" +
          "    </where>\n" +
          "  </sql>\n" +
          "  <insert id=\"saveOne\" keyProperty=\"id\">\n" +
          "    insert into<include refid=\"table\" />\n" +
          "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
          "      <foreach collection=\"values\" item=\"value\">\n" +
          "        ${value.mapping.column},\n" +
          "      </foreach>\n" +
          "    </trim>\n" +
          "    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n" +
          "      <foreach collection=\"values\" item=\"value\">\n" +
          "        #{value.value},\n" +
          "      </foreach>\n" +
          "    </trim>\n" +
          "  </insert>\n" +
          "  <insert id=\"save\" keyProperty=\"id\">\n" +
          "    insert into<include refid=\"table\" />\n" +
          "    <trim prefix=\"(\" suffix=\") values\" suffixOverrides=\",\">\n" +
          "      <foreach collection=\"list[0].values\" item=\"value\">\n" +
          "        ${value.mapping.column},\n" +
          "      </foreach>\n" +
          "    </trim>\n" +
          "    <foreach collection=\"list\" item=\"model\" separator=\",\">\n" +
          "      <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
          "\t    <foreach collection=\"model.values\" item=\"value\">\n" +
          "\t      #{value.value},\n" +
          "\t    </foreach>\n" +
          "\t  </trim>\n" +
          "    </foreach>\n" +
          "  </insert>\n" +
          "  <delete id=\"deleteOne\">\n" +
          "    delete from<include refid=\"table\" />where _idColumn_ = #{id}  \n" +
          "  </delete>\n" +
          "  <delete id=\"delete\">\n" +
          "    delete from<include refid=\"table\" />\n" +
          "    <if test=\"_parameter != null\">\n" +
          "      <include refid=\"criteria\" />\n" +
          "    </if>\n" +
          "  </delete>\n" +
          "  <select id=\"findOne\" resultMap=\"_resultMapId_\">\n" +
          "    select<include refid=\"fields\" />\n" +
          "    from<include refid=\"table\" />where _idColumn_ = #{id}\n" +
          "  </select>\n" +
          "  <select id=\"find\" resultMap=\"_resultMapId_\">\n" +
          "    select<include refid=\"fields\" />\n" +
          "    from<include refid=\"table\" />\n" +
          "    <if test=\"_parameter != null\">\n" +
          "      <include refid=\"criteria\" />\n" +
          "    </if>\n" +
          "    <if test=\"orderBy != null and orderBy != ''\">\n" +
          "      order by ${orderBy} ${sort}\n" +
          "    </if>\n" +
          "    <if test=\"slice\">\n" +
          "      limit #{skip}, #{size}\n" +
          "    </if>\n" +
          "  </select>\n" +
          "  <select id=\"findFirst\" resultMap=\"_resultMapId_\">\n" +
          "    select<include refid=\"fields\" />\n" +
          "    from<include refid=\"table\" />\n" +
          "    <if test=\"_parameter != null\">\n" +
          "      <include refid=\"criteria\" />\n" +
          "    </if>\n" +
          "    limit 0, 1\n" +
          "  </select>\n" +
          "  <select id=\"count\" resultType=\"long\">\n" +
          "    select count(1)\n" +
          "    from<include refid=\"table\" />\n" +
          "    <if test=\"_parameter != null\">\n" +
          "      <include refid=\"criteria\" />\n" +
          "    </if>\n" +
          "  </select>\n" +
          "  <select id=\"exists\" resultType=\"boolean\">\n" +
          "    select count(1)\n" +
          "    from<include refid=\"table\" />\n" +
          "    <if test=\"_parameter != null\">\n" +
          "      <include refid=\"criteria\" />\n" +
          "    </if>\n" +
          "  </select>\n" +
          "  <update id=\"update\">\n" +
          "    update<include refid=\"table\" />\n" +
          "\t<set>\n" +
          "\t  <foreach collection=\"values\" item=\"value\">\n" +
          "\t    <if test=\"value.value != null\">\n" +
          "\t      ${value.mapping.column} = #{value.value},\n" +
          "\t    </if>\n" +
          "\t  </foreach>\n" +
          "\t</set>where _idColumn_ = #{id}\n" +
          "  </update>\n" +
          "  <update id=\"updateNullable\">\n" +
          "    update<include refid=\"table\" />\n" +
          "\t<set>\n" +
          "\t  <foreach collection=\"values\" item=\"value\">\n" +
          "\t    ${value.mapping.column} = #{value.value},\n" +
          "\t  </foreach>\n" +
          "\t</set>where _idColumn_ = #{id}\n" +
          "  </update>\n" +
          "</mapper>";
}
