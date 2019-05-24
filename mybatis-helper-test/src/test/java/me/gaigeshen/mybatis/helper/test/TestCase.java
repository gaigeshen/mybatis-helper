package me.gaigeshen.mybatis.helper.test;

import me.gaigeshen.mybatis.helper.Condition;
import me.gaigeshen.mybatis.helper.MybatisHelperConfigurer;
import me.gaigeshen.mybatis.helper.PageData;
import me.gaigeshen.mybatis.helper.test.entity.User;
import me.gaigeshen.mybatis.helper.test.mapper.UserDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaigeshen
 */
public class TestCase {

  private SqlSessionFactory sqlSessionFactory;
  private UserDao userDao;

  @Before
  public void init() throws Exception {
    MybatisHelperConfigurer configurer = MybatisHelperConfigurer.create().configure();

    String resource = "mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

    configurer.initializeResultMappings(sqlSessionFactory.getConfiguration());

    SqlSession session = sqlSessionFactory.openSession(true);
    userDao = session.getMapper(UserDao.class);
  }

  @After
  public void destory() {
    userDao = null;
    sqlSessionFactory = null;
  }

  @Test
  public void testSaveOne() {
    User user = new User();
    user.setUsername("gaigeshen");
    userDao.saveOne(user);
  }

  @Test
  public void testSave() {
    User user1 = new User();
    user1.setUsername("gaigeshen1");
    User user2 = new User();
    user2.setUsername("gaigeshen2");
    userDao.save(Arrays.asList(user1, user2));
  }

  @Test
  public void testSaveOrUpdate() {
    User user = new User();
    user.setId(1L);
    user.setUsername("gaigeshen_updated");
    userDao.saveOrUpdate(user);
  }

  @Test
  public void testFindOne() {
    User user = userDao.findOne(1L);
    System.out.println(user);
  }

  @Test
  public void testFindFirst() {
    User user = userDao.findFirst(new Condition<>(User.class).where().equalTo("username", "gaigeshen_updated").end());
    System.out.println(user);
  }

  @Test
  public void testFind() {
    List<User> users = userDao.find(new Condition<>(User.class).where().like("username", "gaigeshen").end());
    System.out.println(users);
  }

  @Test
  public void testCount() {
    long count = userDao.count(null);
    System.out.println(count);
  }

  @Test
  public void testSliceup() {
    PageData<User> users = userDao.sliceup(new Condition<>(User.class).page(1).size(10));
    System.out.println(users.getContent());
  }

  @Test
  public void testExistsWithId() {
    boolean exists = userDao.exists(1L);
    System.out.println(exists);
  }

  @Test
  public void testExists() {
    boolean exists = userDao.exists(new Condition<>(User.class).where().equalTo("username", "gaigeshen").end());
    System.out.println(exists);
  }

  @Test
  public void testUpdate() {
    User user = new User();
    user.setId(2L);
    user.setUsername("gaigeshen2_update");
    userDao.update(user);
  }

  @Test
  public void testUpdateNullable() {
    User user = new User();
    user.setId(2L);
    userDao.updateNullable(user);
  }

  @Test
  public void testDeleteOne() {
    userDao.deleteOne(1L);
  }

  @Test
  public void testDelete() {
    userDao.delete(new Condition<>(User.class).where().equalTo("username", "gaigeshen1").end());
  }

  @Test
  public void testDeleteAll() {
    userDao.delete(null);
  }

}
