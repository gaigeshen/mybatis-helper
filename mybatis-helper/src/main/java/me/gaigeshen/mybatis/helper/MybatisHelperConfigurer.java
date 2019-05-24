package me.gaigeshen.mybatis.helper;

import org.apache.ibatis.javassist.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Helper configurer
 *
 * @author gaigeshen
 */
public class MybatisHelperConfigurer {

  private MybatisHelperConfigurer() { }

  /**
   * Create {@link MybatisHelperConfigurer}
   * @return The configurer
   */
  public static MybatisHelperConfigurer create() {
    return new MybatisHelperConfigurer();
  }

  /**
   * This method <b>MUST</b> called first before mybatis {@link org.apache.ibatis.session.SqlSessionFactory} build
   *
   * @return This configurer
   * @throws MybatisHelperConfigurerException May throws this exception if could not configure
   */
  public MybatisHelperConfigurer configure() {
    try {
      configureMapperAnnotationBuilder();
    } catch (NotFoundException | CannotCompileException e) {
      throw new MybatisHelperConfigurerException("Could not configure", e);
    }
    return this;
  }

  /**
   * This method <b>MUST</b> called after mybatis {@link org.apache.ibatis.session.SqlSessionFactory} build
   *
   * @param configuration Need mybatis {@link Configuration}, can returns by {@link SqlSessionFactory#getConfiguration()}
   */
  public void initializeResultMappings(Configuration configuration) {
    ResultMappings.initialize(configuration);
  }

  /**
   * Configure {@link org.apache.ibatis.builder.annotation.MapperAnnotationBuilder}, insert logic code at 179 line.
   * We changed logic of load xml mapper file, because we can generate the content of mapper by using mapper and
   * entity class objects.
   *
   * @throws NotFoundException If {@link org.apache.ibatis.builder.annotation.MapperAnnotationBuilder} class not found
   * @throws CannotCompileException Could not compile class after we changed
   */
  private void configureMapperAnnotationBuilder() throws NotFoundException, CannotCompileException {
    ClassPool classPool = ClassPool.getDefault();
    CtClass aClass = classPool.get("org.apache.ibatis.builder.annotation.MapperAnnotationBuilder");
    CtMethod method = aClass.getDeclaredMethod("loadXmlResource");
    method.insertAt(179, "new org.apache.ibatis.builder.xml.XMLMapperBuilder(\n" +
            "    new java.io.ByteArrayInputStream(" +
            "me.gaigeshen.mybatis.helper.mapper.MapperSource.create(type).getSource().getBytes()),\n" +
            "      assistant.getConfiguration(),\n" +
            "      xmlResource,\n" +
            "      configuration.getSqlFragments(),\n" +
            "      type.getName())" +
            "   .parse();");

    aClass.toClass();
  }
}
