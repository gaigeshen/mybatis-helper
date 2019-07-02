package me.gaigeshen.mybatis.helper.spring;

import me.gaigeshen.mybatis.helper.MybatisHelperConfigurer;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;

/**
 * Configurer bean for configure mybatis helper
 *
 * @author gaigeshen
 */
public class MybatisHelperConfigurerProcessorBean implements BeanFactoryPostProcessor, BeanPostProcessor {

  private static final Logger log = LoggerFactory.getLogger(MybatisHelperConfigurerProcessorBean.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    log.info("Configure mybatis helper");
    MybatisHelperConfigurer.create().configure();
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof MapperFactoryBean) {
      log.debug("Initialize result mappings of MapperFactoryBean: {}", beanName);
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
