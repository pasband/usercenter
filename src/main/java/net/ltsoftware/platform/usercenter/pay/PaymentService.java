package net.ltsoftware.platform.usercenter.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import net.ltsoftware.platform.usercenter.constant.AlipayConstants;
import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private AlipayClient client;


    private static Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService() {
        init();
    }

    private void init() {
        client = new DefaultAlipayClient(AlipayConstants.PAY_URL,);


    }

    public int charge(String channel, Integer amount, Long userId) {


        return ErrorCode.SUCCESS;
    }


}
