package net.ltsoftware.platform.usercenter.service;

import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.model.UserExample;

public interface UserService extends BaseService<User, UserExample> {

    public User selectByQqOpenId(String openId);

    public int bindPhone(String phone, String code, String userId);

    public int addBalance(Long userId, Integer chargeMoney);
}
