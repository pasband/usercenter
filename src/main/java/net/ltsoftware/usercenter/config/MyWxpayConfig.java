package net.ltsoftware.usercenter.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MyWxpayConfig extends WXPayConfig {

    private byte[] certData;

    public MyWxpayConfig() throws Exception {
        String certPath = "/usr/local/usercenter/cer/apiclient_cert.p12";
//        String certPath = "/Users/apple/ltsoft/_platform/1309846801_20190614_cert/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    public String getAppID() {
//        return "wxe6624d74e53bd06c";
        return "wxed61c321c91d5d92";
    }

    @Override
    public String getMchID() {
        return "1309846801";
    }

    @Override
    public String getKey() {
//        return "7231d0955d2a4c85b2d038d39aa5588d";
//        return "a1e821f5632a0265485f60e65dde643d";
        return "9fad261d66fbbd0df8bf0f3a0aff2db1";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;
    }

}