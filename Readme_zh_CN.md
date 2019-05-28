## Mybatis-Helper

[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/gaigeshen/mybatis-helper.svg?branch=develop)](https://travis-ci.org/gaigeshen/mybatis-helper)
[![Maven Central](https://img.shields.io/maven-central/v/me.gaigeshen.mybatis/mybatis-helper.svg)](http://mvnrepository.com/artifact/me.gaigeshen.mybatis/mybatis-helper)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/me.gaigeshen.mybatis/mybatis-helper.svg)](https://oss.sonatype.org/content/repositories/snapshots/me/gaigeshen/mybatis/mybatis-helper)
[![GitHub last commit](https://img.shields.io/github/last-commit/gaigeshen/mybatis-helper.svg)](https://github.com/gaigeshen/mybatis-helper/commits)

### 简介

mybatis 帮助工具， 这个工具已经经过 mybatis3.5.1 和 mybatis-spring2.0.1 的集成测试，只需要将实体类继承 `BaseEntity` 以及将数据访问对象接口继承 `Dao` 即可拥有基本的单表增删查改操作。

这个工具尽量减少所有可能会造成运行时错误的点，管理映射文件无疑是在 mybatis 开发过程当中最大的风险。现在，你仅仅需要将自定义语句写入映射文件，而将那些其他千篇一律的内容抛之脑后，将会使得开发过程简单而且高效的同时，又能降低出错的概率。

如果你仅仅是使用到那些基本的增删查改操作，那么你完全不必写那些很烦躁的映射文件了。

### 开发工具插件

我们另外开发了 IntelliJ idea 的插件，以便帮助生成 mybatis 的文件： [Readme_idea_plugin_zh_CN.md](Readme_idea_plugin_zh_CN.md) 

### 关于实体类

#### 基本实体抽象类

所有的实体类都应该继承 `BaseEntity`，否则那些基本的增删查改操作不会生效。

#### 注解

如果想自定义数据库表名称和列名称，可以使用 `Table` 和 `Column` 注解

- Table

  这个注解可以自定义表名称和表的主键列名称

- Column

  这个注解可以自定义列名称

### 关于映射文件

将自定义的映射方法写入文件，基本的方法不要写入到这个文件

> 注意， 查询结果映射编号固定格式为 "*${类型名称}ResultMap*"

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper>
    <select id="selectByUsername" resultMap="UserResultMap" >
        select identity, username from user where username = #{username}
    </select>
</mapper>
```

### 基本的增删查改方法

|      方法      |                        描述                        |
| :------------: | :------------------------------------------------: |
|    saveOne     |                  保存单个实体对象                  |
|      save      |                  保存多个实体对象                  |
|  saveOrUpdate  |         保存或者更新，这取决于是否给主键值         |
|   deleteOne    |                  根据主键的值删除                  |
|     delete     |                    根据条件删除                    |
|    findOne     |                  根据主键的值查询                  |
|   findFirst    |             查询符合条件的首个实体对象             |
|      find      |                    根据条件查询                    |
|     count      |                    根据条件计数                    |
|    sliceup     |                      分页查询                      |
|     exists     |          查询指定的主键值或者条件是否存在          |
|     update     |             根据主键值更新单个实体对象             |
| updateNullable | 根据主键值更新单个实体对象，为空的属性将更新为空值 |

### 条件

有些方法需要传入条件对象，以下是些许例子帮助理解：

1. 分页

   ```java
   // select identity, username from user limit 0, 10;
   userDao.find(new Condition<>(User.class).page(1).size(10));
   ```

2. 分页和排序

   ```java
   // select identity, username from user order by identity desc limit 0, 10;
   userDao.find(new Condition<>(User.class).page(1).size(10).desc("identity"));
   ```

3. 条件查询

   ```java
   // select identity, username from user where username = 'jack';
   userDao.find(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

4. 按条件删除

   ```java
   // delete from user where username like '%ja%';
   userDao.delete(new Condition<>(User.class).where().like("username","ja").end());
   // delete from user where username like 'ja%';
   userDao.delete(new Condition<>(User.class).where().llike("username","ja").end());
   ```

5. 按条件计数

   ```java
   // select count(1) from user where username = 'jack';
   userDao.count(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

6. 按条件查询是否存在

   ```java
   // select count(1) from user where username = 'jack';
   // Translate to boolean value automatically
   userDao.exists(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

### 如何在没有 spring 的环境中进行配置

1. 准备配置文件 `mybatis-config.xml`

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <settings>
           <setting name="useGeneratedKeys" value="true"/>
       </settings>
       <environments default="development">
           <environment id="development">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="com.mysql.jdbc.Driver"/>
                   <property name="url" value="Your database url"/>
                   <property name="username" value="Your database username"/>
                   <property name="password" value="Your database password"/>
               </dataSource>
           </environment>
       </environments>
       <mappers>
           <mapper class="me.gaigeshen.mybatis.helper.mapper.UserDao" />
       </mappers>
   </configuration>
   ```

2. 配置 `MybatisHelperConfigurer` 在 `SqlSessionFactory` 构建之前

   ```java
   MybatisHelperConfigurer configurer = MybatisHelperConfigurer.create().configure();
   InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
   SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
   ```

3. 初始化映射结果，需要传入 `Configuration` 对象

   ```java
   configurer.initializeResultMappings(sqlSessionFactory.getConfiguration());
   ```

4. 现在，你就可以像往常一样使用 mybatis 的接口了

   ```java
   SqlSession session = sqlSessionFactory.openSession();
   UserDao userDao = session.getMapper(UserDao.class);
   ```

### 如何在 spring 的支持下进行配置

仅仅替换 `SqlSessionFactoryBean` 为 `MybatisHelperSqlSessionFactoryBean`, 或者添加`MybatisHelperConfigurerProcessorBean` 到你的配置中去，如果选择第二种方案，则原先的`SqlSessionFactoryBean` 不要去替换它。

```java
@MapperScan("me.gaigeshen.mybatis.helper.spring.mapper")
@Configuration
public class MybatisHelperConfiguration {
  @Bean
  public MybatisHelperSqlSessionFactoryBean mybatisHelperSqlSessionFactoryBean() throws Exception {
    MybatisHelperSqlSessionFactoryBean factoryBean = new MybatisHelperSqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource());

    org.apache.ibatis.session.Configuration cfg = new org.apache.ibatis.session.Configuration();
    cfg.setUseGeneratedKeys(true);
    factoryBean.setConfiguration(cfg);

    return factoryBean;
  }

  @Bean
  public DataSource dataSource() throws Exception {
    JDBCDataSource dataSource = new JDBCDataSource();
    dataSource.setUrl("jdbc:hsqldb:mem:testdb");
    dataSource.setUser("SA");
    dataSource.setPassword("");
    initializeDatabase(dataSource);
    return dataSource;
  }
}
```

