package net.ltsoftware.platform.usercenter.service.impl;

import net.ltsoftware.platform.dao.UserMapper;
import net.ltsoftware.platform.model.User;
import net.ltsoftware.platform.service.UserService;
import net.ltsoftware.platform.usercenter.dao.UserMapper;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByPrimaryKey(Long key) throws Exception {
        return userMapper.selectByPrimaryKey(key);
    }

    @Override
    public Integer updateByPrimaryKey(User order) throws Exception {
        return userMapper.updateByPrimaryKey(order);
    }

    @Override
    public Integer deleteByPrimaryKey(Long key) throws Exception {
        return userMapper.deleteByPrimaryKey(key);
    }

    @Override
    public Integer insert(User user) throws Exception {
        return userMapper.insert(user);
    }

}
