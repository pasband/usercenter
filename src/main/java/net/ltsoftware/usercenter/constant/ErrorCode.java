package net.ltsoftware.usercenter.constant;

public interface ErrorCode {

    int UNCLASSIFIED = -1;

    int SUCCESS = 0;

    int PARM_MISSING = 100100;

    int PARM_FORMAT_WRONG = 100200;

    int PAY_RETURN_VERIFY_FAIL = 200100;

    int PAY_URL_FAIL = 200200;

    int PAY_AMOUNT_MISFIT = 200300;

    int TOKEN_NULL = 300100;

    int INVALID_TOKEN = 300200;

    int NEED_LOGIN = 300300;

    int USER_NOT_FOUND = 300400;

    int SMS_PHONE_FREQ_HIGH = 400100;

    int PHONE_CODE_WRONG = 400200;

    int NEED_PHONE_BIND = 400300;



}
