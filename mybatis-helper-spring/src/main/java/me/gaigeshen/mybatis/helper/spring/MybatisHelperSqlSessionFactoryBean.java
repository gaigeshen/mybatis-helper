package me.gaigeshen.mybatis.helper.spring;

import me.gaigeshen.mybatis.helper.MybatisHelperConfigurer;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextException;

/**
 * Using this class instead, enable mybatis helper
 *
 * @author gaigeshen
 */
public class MybatisHelperSqlSessionFactoryBean extends SqlSessionFactoryBean implements BeanPostProcessor {

  @Override
  public void afterPropertiesSet() throws Exception {
    // Before sqlSessionFactory build
    MybatisHelperConfigurer.create().configure();
    // Build sqlSessionFactory
    super.afterPropertiesSet();
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    // We invoke initialize result mappings method
    // after every mapper factory bean initialization
    if (bean instanceof MapperFactoryBean) {
      try {
        MybatisHelperConfigurer.create().initializeResultMappings(
                ((MapperFactoryBean<?>) bean).getSqlSessionFactory().getConfiguration());
      } catch (Exception e) {
        throw new ApplicationContextException("Could not initialize result mappings", e);
      }
    }
    return bean;
  }
}
