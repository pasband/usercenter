package net.ltsoftware.usercenter.service;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
//import com.ssh.biz.IProperty;
//import com.ssh.biz.commonsUtil.ABSValidator;
//import com.ssh.biz.mail.IMail;
//import com.ssh.biz.mail.MyAuthenticator;
/**
 * @author Administrator
 * @version 1.0
 * @created 23-����-2012 21:36:55
 */
public class HWMailImpl {
	Logger log = Logger.getLogger(this.getClass());
	private static final String USERNAME = "sun.shidong@highwaytourism.net";
	private static final String PASSWORD = "Bastaki@2023";
	private static final String HOSTNAME = "mail.highwaytourism.net";
	private static final String HOSTPORT = "25";

	public boolean sendHtmlMail(String address, String msg,String signName,String htmlContent) {
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		try {
			Message mailMessage = this.getInstance(address);
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			StringBuffer bf= new StringBuffer();
			bf.append(msg).append(htmlContent);
			html.setContent(bf.toString(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setSubject(signName);
			mailMessage.setContent(mainPart);
			Transport.send(mailMessage);
			//发送成功添加扣款记录
			return true;
		} catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}

	public String getMailBottom(){
		StringBuffer bf = new StringBuffer();
		bf.append("<br/>");
		bf.append("<div style=\"width:100%\">国内首个自助旅游平台<br/><img style=\"border:0;\" src=\"http://www.sanshak.com/images/loge1.jpg\"/>")
		.append("<br/><a href=\"http://www.sanshak.com\" target=\"_blank\">www.sanshak.com</a></div>");
		return bf.toString();
	}
	
	public boolean sendTextMail(String address, String msg,String signName) {
		try {
			Message mailMessage = this.getInstance(address);
			mailMessage.setSubject(signName);
			mailMessage.setText(msg);
			// 设置邮件消息发送的时间
			Transport.send(mailMessage);
		} catch (Exception ex) {
			log.error(ex);
			return false;
		}
		return true;
	}

	private Message getInstance(String address) {
		// 判断是否需要身份认证
//		if (!ABSValidator.MAIL_VALID.matcher(address)) {
//			return null;
//		}
//		MyAuthenticator authenticator = null;
		Properties p = new Properties();
		p.put("mail.smtp.host", HOSTNAME);
		p.put("mail.smtp.port", HOSTPORT);
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.starttls.enable","true");
		// 如果需要身份认证，则创建一个密码验证器
//		authenticator = new MyAuthenticator(USERNAME, PASSWORD);
//		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(p, null);
		Message mailMessage = null;
		try {
			// 根据session创建一个邮件消息
			mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(this.USERNAME);
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(address);
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			mailMessage.setSentDate(new Date());

		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
		return mailMessage;
	}

//	public Map<String, String> getMailConfiguration(IProperty systemProperty) {
//		// TODO Auto-generated method stub
//		return null;
//	}

		public static void main(String[] args) {
			// 邮件发送者的邮箱账号和密码
//			String senderEmail = "your-email@example.com";
//			String senderPassword = "your-password";

			// 邮件接收者的邮箱地址
			String recipientEmail = "1226785489@qq.com";

			// 设置邮件的属性
			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", HOSTNAME);
			properties.put("mail.smtp.port", "587");
			properties.put("mail.smtp.ssl.trust","mail.highwaytourism.net");

			// 创建一个会话(Session)对象
			Session session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME, PASSWORD);
				}
			});

			try {
				// 创建一个邮件消息
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(USERNAME));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
				message.setSubject("JavaMail API Test");
				message.setText("This is a test email sent using JavaMail API.");

				// 发送邮件
				Transport.send(message);

				System.out.println("Email sent successfully!");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	
}