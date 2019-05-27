package me.gaigeshen.mybatis.helper.spring.boot.autoconfigure;

import me.gaigeshen.mybatis.helper.MybatisHelperConfigurer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaigeshen
 */
@ConditionalOnClass( { MybatisAutoConfiguration.class, MybatisHelperConfigurer.class } )
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@Configuration
public class MybatisHelperAutoConfiguration implements InitializingBean, BeanPostProcessor {

  @Override
  public void afterPropertiesSet() throws Exception {
    MybatisHelperConfigurer.create().configure();
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
