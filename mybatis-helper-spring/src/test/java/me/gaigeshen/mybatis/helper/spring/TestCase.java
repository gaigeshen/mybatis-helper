package me.gaigeshen.mybatis.helper.spring;

import me.gaigeshen.mybatis.helper.spring.entity.User;
import me.gaigeshen.mybatis.helper.spring.mapper.UserDao;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * @author gaigeshen
 */
public class TestCase {

  private AnnotationConfigApplicationContext context;

  @Before
  public void init() {
    context = new AnnotationConfigApplicationContext(MybatisHelperConfiguration.class);
  }

  @After
  public void destroy() {
    if (context != null) {
      context.close();
    }
  }

  @Test
  public void testCreateUser() {
    User user = new User();
    user.setUsername("gaigeshen");
    UserDao userDao = context.getBean(UserDao.class);
    userDao.saveOne(user);

    User found = userDao.findOne(user.getId());
    Assert.assertEquals(user.getUsername(), found.getUsername());
  }

  @Test
  public void testDeleteUser() {
    User user = new User();
    user.setUsername("gaigeshen");
    UserDao userDao = context.getBean(UserDao.class);
    userDao.saveOne(user);

    userDao.deleteOne(user.getId());
    Assert.assertNull(userDao.findOne(user.getId()));
  }

  @Test
  public void testFindUsers() {
    UserDao userDao = context.getBean(UserDao.class);

    User user1 = new User();
    user1.setUsername("gaigeshen1");
    userDao.saveOne(user1);
    User user2 = new User();
    user2.setUsername("gaigeshen2");
    userDao.saveOne(user2);

    List<User> users = userDao.find(null); // Find all users
    Assert.assertEquals(users.size(), 2);
  }

}
