package net.ltsoftware.platform.usercenter.service.impl;

import net.ltsoftware.platform.usercenter.dao.UserMapper;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.model.UserExample;
import net.ltsoftware.platform.usercenter.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
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

    @Override
    public List<User> selectByExample(UserExample userExample) throws Exception {
        return userMapper.selectByExample(userExample);
    }

    @Override
    public User selectByQqOpenId(String openId) {

        User user = null;

        UserExample example = new UserExample();
        example.createCriteria().andQqOpenidEqualTo(openId);
        List<User> list = userMapper.selectByExample(example);

        if (list != null && list.size() == 1) {
            user = list.get(0);
        }
        return user;

    }

}
