package net.ltsoftware.platform.usercenter.service.impl;

import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import net.ltsoftware.platform.usercenter.constant.SmsConstants;
import net.ltsoftware.platform.usercenter.dao.UserMapper;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.model.UserExample;
import net.ltsoftware.platform.usercenter.service.UserService;
import net.ltsoftware.platform.usercenter.util.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisClient redisClient;

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

    @Override
    public int bindPhone(String phone, String code, String userId) {
        String code1 = redisClient.get(SmsConstants.PREFIX + phone);
        if (code.equals(code1)) {
            User user = userMapper.selectByPrimaryKey(Long.parseLong(userId));
            user.setPhone(phone);
            user.setStatus("2");
            userMapper.updateByPrimaryKey(user);
            return ErrorCode.SUCCESS;
        }

        return ErrorCode.PHONE_CODE_WRONG;
    }

    @Override
    public int addBalance(Long userId, Integer chargeMoney) {
        User user = userMapper.selectByPrimaryKey(userId);
        int balance = user.getBalance() + chargeMoney;

        user.setBalance(balance);
        userMapper.updateByPrimaryKey(user);
        return ErrorCode.SUCCESS;
    }

}
