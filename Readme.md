## Mybatis-Helper

[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/gaigeshen/mybatis-helper.svg?branch=develop)](https://travis-ci.org/gaigeshen/mybatis-helper)
[![Maven Central](https://img.shields.io/maven-central/v/me.gaigeshen.mybatis/mybatis-helper.svg)](http://mvnrepository.com/artifact/me.gaigeshen.mybatis/mybatis-helper)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/me.gaigeshen.mybatis/mybatis-helper.svg)](https://oss.sonatype.org/content/repositories/snapshots/me/gaigeshen/mybatis/mybatis-helper)
[![GitHub last commit](https://img.shields.io/github/last-commit/gaigeshen/mybatis-helper.svg)](https://github.com/gaigeshen/mybatis-helper/commits)

> For Chinese version of this file, please click [Readme_zh_CN.md](Readme_zh_CN.md)
>
> We created intelliJ idea plugin to helps you generate mybatis files, please click  [Readme_idea_plugin.md](Readme_idea_plugin.md) 

### Introduction

Helper tools for mybatis, this tools **TESTed** with mybatis version 3.5.1 and mybatis-spring version 2.0.1, after extends `BaseEntity` and `Dao` interface, you don't have to write mapper xml file if **JUST** use basic CRUD [methods](#Basic-methods).

### About entity

#### Base entity

All entities should extends `BaseEntity`, or no any data access methods for you.

#### Annotations

If you want to customize table name or column name, you can use `Table` and `Column` annotations

- Table

  You can define table name and table id column name by using this annotation

- Column

  You can define column name by using this annotation

### About Dao interface

#### Dao

All `Dao` interfaces **MUST** extends this interface directly

### About mapper xml file

Write your **custom methods** to mapper xml file

> Note: The resultMap id is "*${entity type name}ResultMap*"

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<!-- Basic methods please don't write to this file -->
<!-- The namespace attribute of mapper is not required in some scenarios -->
<mapper>
    <!-- The resultMap id is ${entity type name}ResultMap -->
    <select id="selectByUsername" resultMap="UserResultMap" >
        <!-- Include all columns name -->
        select <include refid="fields" />
        <!-- Include table name -->
        from <include refid="table" />
        where username = #{username}
    </select>
</mapper>
```

### Basic methods

|         Method          |                         Description                          |
| :---------------------: | :----------------------------------------------------------: |
|         saveOne         |                       Save one entity                        |
|          save           |                      Save many entities                      |
|      saveOrUpdate       | Save or update entity, if the entity has id value, then update it |
|        deleteOne        |                     Delete by entity id                      |
|         delete          |                     Delete by conditions                     |
|         findOne         |                      Find entity by id                       |
|        findFirst        |               Find first record by conditions                |
|          find           |                      Find by conditions                      |
|          count          |             Returns records count by conditions              |
|         paging          |             Returns paged entities by conditions             |
|         exists          |           Check exists of entity id or conditions            |
|         update          |                     Update entity by id                      |
|     updateCondition     |                 Update entities by condition                 |
|     updateNullable      | Update entity by id, and null value properties update to null |
| updateConditionNullable | Update entities by condition, and null value properties update to null |

### Condition

Some "dao" methods requires condition object parameter, here are some examples for you:

1. Find by page

   ```java
   // select identity, username from user limit 0, 10;
   userDao.find(new Condition<>(User.class).page(1).size(10));
   ```

2. Find by page and sort

   ```java
   // select identity, username from user order by identity desc limit 0, 10;
   userDao.find(new Condition<>(User.class).page(1).size(10).desc("identity"));
   ```

3. Find by conditions

   ```java
   // select identity, username from user where username = 'jack';
   userDao.find(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

4. Delete by conditions

   ```java
   // delete from user where username like '%ja%';
   userDao.delete(new Condition<>(User.class).where().like("username","ja").end());
   // delete from user where username like 'ja%';
   userDao.delete(new Condition<>(User.class).where().llike("username","ja").end());
   ```

5. Count by conditions

   ```java
   // select count(1) from user where username = 'jack';
   userDao.count(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

6. Exists by conditions

   ```java
   // select count(1) from user where username = 'jack';
   // Translate to boolean value automatically
   userDao.exists(new Condition<>(User.class).where().equalTo("username","jack").end());
   ```

### How to configure without spring support

1. Configuration file `mybatis-config.xml`

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
           <!-- <mapper resource="me/gaigeshen/mybatis/helper/mapper/UserDao.xml" /> -->
           
           <!-- If use url attribute, the namespace of mapper is required -->
           <mapper url="" />
       </mappers>
   </configuration>
   ```

2. Configure helper by using `MybatisHelperConfigurer` before mybatis `SqlSessionFactory` build

   ```java
   MybatisHelperConfigurer configurer = MybatisHelperConfigurer.create().configure();
   InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
   SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
   ```

3. Initialize result mappings by using mybatis `Configuration` object

   ```java
   configurer.initializeResultMappings(sqlSessionFactory.getConfiguration());
   ```

4. Now, you can use mybatis APIs

   ```java
   SqlSession session = sqlSessionFactory.openSession();
   UserDao userDao = session.getMapper(UserDao.class);
   ```

### How to configure with spring support

JUST replace `SqlSessionFactoryBean` to `MybatisHelperSqlSessionFactoryBean`, or add `MybatisHelperConfigurerProcessorBean` bean to your configuration. More information please see TestCase in mybatis-helper-spring module.

```java
@MapperScan("me.gaigeshen.mybatis.helper.spring.mapper")
@Configuration
public class MybatisHelperConfiguration {
  // Replace SqlSessionFactoryBean to MybatisHelperSqlSessionFactoryBean
  // to enable mybatis-helper-spring features
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

