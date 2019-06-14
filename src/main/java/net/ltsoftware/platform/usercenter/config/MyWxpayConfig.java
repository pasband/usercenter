package net.ltsoftware.platform.usercenter.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

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
        return "wxe6624d74e53bd06c";
    }

    @Override
    public String getMchID() {
        return "1309846801";
    }

    @Override
    public String getKey() {
        return "9259327c600b5d04bdaedc168f000711";
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
        return null;
    }
}