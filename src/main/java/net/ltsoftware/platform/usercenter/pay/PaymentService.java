package net.ltsoftware.platform.usercenter.pay;

import com.alipay.api.AlipayClient;
import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "payment")
public class PaymentService {

    private AlipayClient client;

    private AlipayConf alipayConf;

    private static Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService() {
        init();
    }

    private void init() {
//        client = new DefaultAlipayClient(),
        logger.info(alipayConf.url);

    }

    public int charge(String channel, Integer amount, Long userId) {


        return ErrorCode.SUCCESS;
    }

    class AlipayConf {
        String url;
        String appid;
        String appPrivateKey;
        String format;
        String charset;
        String alipayPublicKey;
        String signType;

    }

}
