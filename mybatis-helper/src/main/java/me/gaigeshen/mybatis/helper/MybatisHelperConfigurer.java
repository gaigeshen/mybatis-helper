package me.gaigeshen.mybatis.helper;

import javassist.*;
import me.gaigeshen.mybatis.helper.entity.ResultMappings;
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
      configureXmlConfigBuilder();
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
   * Configure {@link org.apache.ibatis.builder.xml.XMLConfigBuilder}, insert logic code at 372 and 377 lines.
   * We changed logic of load xml mapper file, because we can generate the content of mapper by using mapper and
   * entity class objects.
   *
   * @throws NotFoundException If {@link org.apache.ibatis.builder.xml.XMLConfigBuilder} class not found
   * @throws CannotCompileException Could not compile class after we changed
   */
  private void configureXmlConfigBuilder() throws NotFoundException, CannotCompileException {
    ClassPool classPool = ClassPool.getDefault();
    classPool.insertClassPath(new ClassClassPath(getClass()));
    CtClass aClass = classPool.get("org.apache.ibatis.builder.xml.XMLConfigBuilder");
    CtMethod method = aClass.getDeclaredMethod("mapperElement");

    // Url mapper location
    // The namespace in user defined mapper xml file is required
    method.insertAt(377, "java.lang.String mapperSource = " +
            // Resolve mapper class name by namespace in mapper xml content
            "me.gaigeshen.mybatis.helper.mapper.MapperSource.create((java.lang.Class) null, inputStream).getSource();" +
            // Change the input stream object to our input stream
            "inputStream = new java.io.ByteArrayInputStream(mapperSource.getBytes());");

    // Resource location
    method.insertAt(372, "String mapperClassName = resource.replaceAll(\"/\", \".\").replaceAll(\".xml\", \"\");" +
            // The mapper interface name is same as the mapper resource location name
            // Create mapper source object with mapper interface name and mapper resource
            "java.lang.String mapperSource = me.gaigeshen.mybatis.helper.mapper.MapperSource.create(mapperClassName, inputStream).getSource();" +
            // Change the input stream object to our input stream
            "inputStream = new java.io.ByteArrayInputStream(mapperSource.getBytes());");
    aClass.toClass();
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
    classPool.insertClassPath(new ClassClassPath(getClass()));
    CtClass aClass = classPool.get("org.apache.ibatis.builder.annotation.MapperAnnotationBuilder");
    CtMethod method = aClass.getDeclaredMethod("loadXmlResource");
    method.insertAt(179, "java.lang.String mapperSource = inputStream != null" +
            "? me.gaigeshen.mybatis.helper.mapper.MapperSource.create(type, inputStream).getSource()" +
            ": me.gaigeshen.mybatis.helper.mapper.MapperSource.create(type).getSource();" +
            "new org.apache.ibatis.builder.xml.XMLMapperBuilder(" +
            "    new java.io.ByteArrayInputStream(mapperSource.getBytes())," +
            "      assistant.getConfiguration()," +
            "      xmlResource," +
            "      configuration.getSqlFragments()," +
            "      type.getName())" +
            "   .parse();" +
            "inputStream = null;");

    aClass.toClass();
  }
}
