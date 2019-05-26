package me.gaigeshen.mybatis.helper.mapper;

import me.gaigeshen.mybatis.helper.Dao;
import me.gaigeshen.mybatis.helper.entity.User;

/**
 * @author gaigeshen
 */
public interface UserDao extends Dao<User, Long> {

  User selectByUsername(String username);

}
