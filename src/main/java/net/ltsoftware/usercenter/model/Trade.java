package net.ltsoftware.usercenter.model;

public class Trade {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.user_id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.orig_amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Long origAmount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Long amount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.create_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.update_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.sub_system
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String subSystem;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.status
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.valid_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String validTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.is_valid
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Integer isValid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.is_deleted
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private Integer isDeleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.expired_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String expiredTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.deleted_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String deletedTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.trade_no
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String tradeNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.trade_no_3rd
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String tradeNo3rd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column acc_trade.pay_channel
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    private String payChannel;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.id
     *
     * @return the value of acc_trade.id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.id
     *
     * @param id the value for acc_trade.id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.user_id
     *
     * @return the value of acc_trade.user_id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.user_id
     *
     * @param userId the value for acc_trade.user_id
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.orig_amount
     *
     * @return the value of acc_trade.orig_amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Long getOrigAmount() {
        return origAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.orig_amount
     *
     * @param origAmount the value for acc_trade.orig_amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setOrigAmount(Long origAmount) {
        this.origAmount = origAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.amount
     *
     * @return the value of acc_trade.amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.amount
     *
     * @param amount the value for acc_trade.amount
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.create_time
     *
     * @return the value of acc_trade.create_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.create_time
     *
     * @param createTime the value for acc_trade.create_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.update_time
     *
     * @return the value of acc_trade.update_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.update_time
     *
     * @param updateTime the value for acc_trade.update_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.sub_system
     *
     * @return the value of acc_trade.sub_system
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getSubSystem() {
        return subSystem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.sub_system
     *
     * @param subSystem the value for acc_trade.sub_system
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.status
     *
     * @return the value of acc_trade.status
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.status
     *
     * @param status the value for acc_trade.status
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.valid_time
     *
     * @return the value of acc_trade.valid_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getValidTime() {
        return validTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.valid_time
     *
     * @param validTime the value for acc_trade.valid_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.is_valid
     *
     * @return the value of acc_trade.is_valid
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Integer getIsValid() {
        return isValid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.is_valid
     *
     * @param isValid the value for acc_trade.is_valid
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.is_deleted
     *
     * @return the value of acc_trade.is_deleted
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.is_deleted
     *
     * @param isDeleted the value for acc_trade.is_deleted
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.expired_time
     *
     * @return the value of acc_trade.expired_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getExpiredTime() {
        return expiredTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.expired_time
     *
     * @param expiredTime the value for acc_trade.expired_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.deleted_time
     *
     * @return the value of acc_trade.deleted_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getDeletedTime() {
        return deletedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.deleted_time
     *
     * @param deletedTime the value for acc_trade.deleted_time
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setDeletedTime(String deletedTime) {
        this.deletedTime = deletedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.trade_no
     *
     * @return the value of acc_trade.trade_no
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getTradeNo() {
        return tradeNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.trade_no
     *
     * @param tradeNo the value for acc_trade.trade_no
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.trade_no_3rd
     *
     * @return the value of acc_trade.trade_no_3rd
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getTradeNo3rd() {
        return tradeNo3rd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.trade_no_3rd
     *
     * @param tradeNo3rd the value for acc_trade.trade_no_3rd
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setTradeNo3rd(String tradeNo3rd) {
        this.tradeNo3rd = tradeNo3rd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column acc_trade.pay_channel
     *
     * @return the value of acc_trade.pay_channel
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public String getPayChannel() {
        return payChannel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column acc_trade.pay_channel
     *
     * @param payChannel the value for acc_trade.pay_channel
     *
     * @mbg.generated Mon Sep 09 18:26:50 CST 2019
     */
    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}