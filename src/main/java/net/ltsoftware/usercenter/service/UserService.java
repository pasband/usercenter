package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.SessionConstants;
import net.ltsoftware.usercenter.constant.SmsConstants;
import net.ltsoftware.usercenter.controller.PayController;
import net.ltsoftware.usercenter.dao.UserMapper;
import net.ltsoftware.usercenter.model.User;
import net.ltsoftware.usercenter.model.UserExample;
import net.ltsoftware.usercenter.util.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService implements BaseService<User, UserExample> {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisClient redisClient;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public User selectByPrimaryKey(Long key) throws Exception {
        return userMapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer updateByPrimaryKey(User order) throws Exception {
        return userMapper.updateByPrimaryKey(order);
    }

    @Override
    @Transactional
    public Integer deleteByPrimaryKey(Long key) throws Exception {
        return userMapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer insert(User user) throws Exception {
        return userMapper.insert(user);
    }

    @Override
    public List<User> selectByExample(UserExample userExample) throws Exception {
        return userMapper.selectByExample(userExample);
    }

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

    public User selectByWxOpenId(String openId) {
        User user = null;
        UserExample example = new UserExample();
        example.createCriteria().andWxOpenidEqualTo(openId);
        List<User> list = userMapper.selectByExample(example);
        if (list != null && list.size() == 1) {
            user = list.get(0);
        }
        return user;
    }

    @Transactional
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

    @Transactional
    public int addBalance(Long userId, Integer chargeMoney) {
        User user = userMapper.selectByPrimaryKey(userId);
        int balance = user.getBalance() + chargeMoney;
        user.setBalance(balance);
        userMapper.updateByPrimaryKey(user);
        return ErrorCode.SUCCESS;
    }

    public User getUserByToken(String token) {
        String val = redisClient.get(SessionConstants.LOGIN_TOKEN_PREF+token);
        if(val==null)
            return null;
        Long key = Long.valueOf(val);
        logger.info("user id:"+key);
        User user = userMapper.selectByPrimaryKey(key);
        return user;
    }

}
