package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.model.User;
import net.ltsoftware.usercenter.model.UserExample;

public interface UserService extends BaseService<User, UserExample> {

    public User selectByQqOpenId(String openId);

    public User selectByWxOpenId(String openId);

    public int bindPhone(String phone, String code, String userId);

    public int addBalance(Long userId, Integer chargeMoney);
}
