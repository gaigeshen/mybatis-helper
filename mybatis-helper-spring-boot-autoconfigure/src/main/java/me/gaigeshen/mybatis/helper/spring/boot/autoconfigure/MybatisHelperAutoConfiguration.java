package me.gaigeshen.mybatis.helper.spring.boot.autoconfigure;

import me.gaigeshen.mybatis.helper.MybatisHelperConfigurer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;

/**
 * Mybatis helper auto-configuration
 *
 * @author gaigeshen
 */
@ConditionalOnClass(MybatisHelperConfigurer.class) // Include mybatis-helper lib ?
@Configuration
public class MybatisHelperAutoConfiguration {

  @Bean
  public static MybatisHelperConfigurerProcessor mybatisHelperConfigurerProcessor() {
    return new MybatisHelperConfigurerProcessor();
  }

  /**
   * Configure mybatis helper
   *
   * @author gaigeshen
   */
  static class MybatisHelperConfigurerProcessor implements BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      MybatisHelperConfigurer.create().configure();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
      ApplicationContext applicationContext = event.getApplicationContext();
      if (Objects.isNull(applicationContext.getParent())) {
        SqlSessionFactory sessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        MybatisHelperConfigurer.create().initializeResultMappings(sessionFactory.getConfiguration());
      }
    }
  }

}
