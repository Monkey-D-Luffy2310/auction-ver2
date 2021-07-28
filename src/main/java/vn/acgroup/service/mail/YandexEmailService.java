package vn.acgroup.service.mail;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import net.bytebuddy.utility.RandomString;
import vn.acgroup.config.YandexConfig;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.LikedAuction;
import vn.acgroup.entities.NoticeRegister;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.NoticeRegisterRepository;
import vn.acgroup.repositories.UserRepository;

@Service
public class YandexEmailService implements MailService {
  private final Logger log = LoggerFactory.getLogger(YandexEmailService.class);

  @Value("${acgroup.email.send:false}")
  Boolean sendEmail;

  @Autowired UserRepository userRepository;
  @Autowired NoticeRegisterRepository noticeRegisterRepository;
  @Autowired AssetRepository assetRepository;

  @PostConstruct
  public void init() {
    log.info("sendEmail: {}", sendEmail.toString());
  }

  public void sendEmail(String to, String subject, String body) {
    try {
      log.info("send email to {}", to);
      //			if (!sendEmail) {
      //				return;
      //			}

      String from = YandexConfig.from;
      String pass = YandexConfig.pass;

      Properties props = System.getProperties();
      String host = "smtp.yandex.com";
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.user", from);
      props.put("mail.smtp.password", pass);
      props.put("mail.smtp.port", "465");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.quitwait", "false");
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.debug", "true");
      props.put("mail.smtp.ssl.protocols", "TLSv1.2");
      // System.out.println(body);
      Session session = Session.getInstance(props);
      session.setDebug(false);

      MimeMessage message = new MimeMessage(session);
      message.setHeader("Content-Type", "text/plain; charset=UTF-8");
      message.setFrom(new InternetAddress("Ac Auction <" + YandexConfig.from + ">"));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject(subject, "UTF-8");
      message.setContent(body, "text/html; charset=UTF-8");
      Transport transport = session.getTransport("smtp");
      transport.connect(host, from, pass);
      for (Address rec : message.getAllRecipients()) {
        log.info(rec.toString());
      }
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return;
  }

  //	public void sendEmail(String to, String subject, String body, String a) {
  //		// Recipient's email ID needs to be mentioned.
  //
  //		// Sender's email ID needs to be mentioned
  //		// String from = "hotrodaugiaac@gmail.com";
  //		String from = "acauction.noreply@gmail.com";
  //		String pass = "abcD123$";
  //
  //		// Get system properties
  //		Properties properties = System.getProperties();
  //
  //		// Setup mail server
  //		properties.put("mail.smtp.host", "smtp.gmail.com");
  //		properties.put("mail.smtp.port", "465");
  //		properties.put("mail.smtp.auth", "true");
  //		properties.put("mail.smtp.starttls.enable", "true");
  //		properties.put("mail.smtp.ssl.enable", "true");
  //		properties.put("mail.smtp.quitwait", "false");
  //		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
  //		properties.put("mail.debug", "true");
  //		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
  //
  //		// Get the Session object.// and pass username and password
  //		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
  //			protected PasswordAuthentication getPasswordAuthentication() {
  //				return new PasswordAuthentication(from, pass);
  //			}
  //		});
  //
  //		// Used to debug SMTP issues
  //		session.setDebug(true);
  //
  //		try {
  //			// Create a default MimeMessage object.
  //			MimeMessage message = new MimeMessage(session);
  //			// Set From: header field of the header.
  //			message.setFrom(new InternetAddress(from));
  //			// Set To: header field of the header.
  //			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
  //			// Set Subject: header field
  //			message.setSubject(subject);
  //			// // Now set the actual message
  //			message.setContent(body, "text/html; charset=UTF-8");
  //			System.out.println("sending...");
  //			// Send message
  //			Transport.send(message);
  //			System.out.println("Sent message successfully....");
  //		} catch (MessagingException mex) {
  //			mex.printStackTrace();
  //		}
  //	}

  public void forgot(@PathVariable String email) throws InterruptedException, ExecutionException {
    User user = userRepository.findByEmailAndIsActive(email, true).get();
    String password = RandomString.make(6);
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color:#000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid#e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\"width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height:34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					Đặt lại mật khẩu thành công!\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color:#000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform:capitalize;\">"
            + user.getName()
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "				  Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu daugia.io của bạn.\r\n"
            + "					<br>\r\n"
            + "					Mật khẩu tạm thời của bạn là: <span style=\"font-weight: 700;color:#000000;\"></span>\r\n"
            + password
            + "					<br>\r\n"
            + " 				Vui lòng đăng nhập với mật khẩu tạm thời, và đổi lại mật khẩu mới của bạn!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\"href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Hỗ  trợ</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\"href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sửdụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\"href=\"http://daugia.io/AC-Auction/lien-he\">liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\"width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(user.getEmail(), "Chào " + user.getName() + "! Đặt lại mật khẩu thành công!", body);
    user.setPassword(password);
    userRepository.save(user);
    return;
  }

  public void noticeRegister(@PathVariable String email) {
    String code = RandomString.make(12);
    NoticeRegister noticeRegister = new NoticeRegister();
    noticeRegister.setEmail(email);
    noticeRegister.setVerify(false);
    noticeRegister.setVerifycode(code);
    noticeRegisterRepository.save(noticeRegister);
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\" width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height: 34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					Xác nhận đăng ký\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color: #000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform: capitalize;\">"
            + email
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Cám ơn bạn đã đăng ký nhận thông báo!\r\n"
            + "					<br>\r\n"
            + "					Xác nhận email của bạn để trải nghiệm dịch vụ của chúng tôi: <span style=\"font-weight: 700;color: #000000;\"></span>\r\n"
            + "					<br>\r\n"
            // TODO link
            + "					<a href='"
            + YandexConfig.link
            + "notice/"
            + email
            + "/"
            + code
            + "' style=\"color: #5b9bd5;text-decoration: underline; text-transform: uppercase;\">Xác nhận email</a>\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Hỗ trợ</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sử dụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/lien-he\">Liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\" width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(email, "Chào " + email + "! Hoàn tất đăng ký!", body);
  }

  public void winMail(@PathVariable User winner, @PathVariable Auction auction) {
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\" width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height: 34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color: #000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform: capitalize;\">"
            + winner.getName()
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Bạn đã thắng đấu giá sản phẩm "
            + auction.getAssest().getName()
            + "\r\n"
            + "          <div>Xin vui lòng nhập thông tin vào đường dẫn dưới đây để bộ phận chăm sóc khách hàng hỗ trợ bạn tốt nhất.</div>\r\n"
            + "          <a href='https://docs.google.com/forms/d/1iEtRCXWzBrUN8XS35yjWtH0tM5B_gg0tQpAcvYelOHg/viewform?edit_requested=true'>Nhập thông tin khách hàng</a>\r\n"
            + "					<br>\r\n"
            + "					Bây giờ bạn có thể đăng nhập tại daugia.io để thanh toán và nhận sản phẩm <span style=\"font-weight: 700;color: #000000;\"></span>\r\n"
            + "					<br>\r\n"
            + "					<a href='"
            + YandexConfig.home
            + "/dang-nhap"
            + "'  +style=\"color: #5b9bd5;text-decoration: underline; text-transform: uppercase;\">Nhấn vào đây để đăng nhập</a>\r\n"
            + "					<br>\r\n"
            + "					Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Trợ giúp</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sử dụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/lien-he\">Liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\" width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(winner.getEmail(), "Xin chào " + winner.getName() + "! Thanh toán sản phẩm!", body);
  }

  public void userRegisterMail(@PathVariable User user, @PathVariable String code) {

    String body =
        "<!DOCTYPE html>\r\n"
            + "<html lang=\"en\">\r\n"
            + "<head>\r\n"
            + "    <meta charset=\"UTF-8\">\r\n"
            + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
            + "    <title>Document</title>\r\n"
            + "</head>\r\n"
            + "<body style=\"background-color: #E5E5E5;\">\r\n"
            + "            <div style=\"background-image: url('https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619148109113-background.png?alt=media&token=2ad9cd11-c724-4156-8811-c995de3f3dcf');\r\n"
            + "            width: 600px;height: 252px;margin:auto\">\r\n"
            + "                <div style=\"\r\n"
            + "                width: 600px;\r\n"
            + "                height: 56px;\r\n"
            + "                top: 0px;\r\n"
            + "                background: #002B5E;\r\n"
            + "                display: flex;\r\n"
            + "                \">\r\n"
            + "                    <div style=\"\r\n"
            + "                    width: 120px;\r\n"
            + "                    height: 32px;\r\n"
            + "                    padding-left: 68px;\r\n"
            + "                    padding-top: 14px;\">\r\n"
            + "                        <img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619148159836-logo.png?alt=media&token=b8475deb-3dff-414a-82c2-454e2baf1af7\" alt=\"\" width=\"130px\" height=\"32px\">\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\"padding-left: 130px;\r\n"
            + "                    padding-top: 28px;\">\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/tro-giup\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Trợ giúp</a>\r\n"
            + "                        <span style=\"color: white;font-family: Roboto;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">|</span>\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Điều khoản sử dụng</a>\r\n"
            + "                        <span style=\"color: white;font-family: Roboto;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">|</span>\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/lien-he\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Liên hệ</a>\r\n"
            + "                    </div>\r\n"
            + "                </div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: bold;\r\n"
            + "                font-size: 28px;\r\n"
            + "                line-height: 33px;\r\n"
            + "                text-align: center;\r\n"
            + "                padding-top:80px;\r\n"
            + "                color: #FFFFFF;\">XÁC THỰC EMAIL</div>\r\n"
            + "            </div>\r\n"
            + "            <div style=\"background-color: white;width: 600px;padding-bottom: 50px;margin: auto;\">\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                color: #444444;\r\n"
            + "                padding-left:65px;\r\n"
            + "                padding-top:45px\">Kính chào "
            + user.getName()
            + ",</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left:65px;\r\n"
            + "                padding-top: 13px;\r\n"
            + "                padding-bottom: 20px;\r\n"
            + "                color: #444444;\">Để hoàn tất tài khoản, vui lòng xác thực email của mình.</div>\r\n"
            + "                <div style=\"text-align:center;\">\r\n"
            + "                    <a href='"
            + YandexConfig.link
            + "register/"
            + user.getEmail()
            + "/"
            + code
            + "'"
            + " style=\"\r\n"
            + "                    padding: 10px 20px;\r\n"
            + "                    margin-top: 20px;\r\n"
            + "                    background: #2E67B1;\r\n"
            + "                    font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    color: #FFFFFF;\r\n"
            + "                    text-align: center;\r\n"
            + "                    text-decoration: none;\">\r\n"
            + "                        Xác thực email\r\n"
            + "                    </a>\r\n"
            + "                </div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                color: #444444;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                padding-top:25px;\">Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                padding-top:35px;\r\n"
            + "                color: #444444;\">Trân trọng,</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                color: #444444;\">Đội ngũ AC Auction.</div>\r\n"
            + "            </div>\r\n"
            + "            <div style=\"width: 600px;  \r\n"
            + "            height: 29px;\r\n"
            + "            background: #012A5E;\r\n"
            + "            padding: 10px 0px 0px 0px;\r\n"
            + "            font-family: Roboto;\r\n"
            + "            font-style: normal;\r\n"
            + "            font-weight: normal;\r\n"
            + "            font-size: 14px;\r\n"
            + "            text-align: center;\r\n"
            + "            color: #FFFFFF;\r\n"
            + "            margin: auto;\">Bản quyền © 2021 Công ty Cổ phần Tập đoàn Đầu tư và Phát triển Dự án Á Châu.</div>\r\n"
            + "\r\n"
            + "</body>\r\n"
            + "</html>";
    sendEmail(user.getEmail(), "Xin chào " + user.getName() + "! Hoàn tất đăng ký!", body);
  }

  public void withdrawMail(@PathVariable User user, @PathVariable String code) {

    String body = code;
    sendEmail(user.getEmail(), "Xin chào " + user.getName() + "! Mã xác minh rút tiền", body);
  }

  public void upcomingAuctionMail(@PathVariable LikedAuction el, @PathVariable Long time) {
    String asset = assetRepository.findAssetByAuctions_Id(el.getAuction().getId()).getName();
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\" width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height: 34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					Đấu giá sản phẩm "
            + asset
            + " sắp bắt đầu"
            + "\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color: #000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform: capitalize;\">"
            + el.getUser().getName()
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Chỉ còn <strong>"
            + time
            + " ngày</strong> nữa "
            + asset
            + " sẽ được đấu giá trực tuyến tại daugia.io."
            + "\r\n"
            + "					<br>\r\n"
            + "					Bạn có thể xem đấu giá "
            + asset
            + "<span style=\"font-weight: 700;color: #000000;\"></span>"
            + "					<br>\r\n"
            + "					<a href='"
            + "http://daugia.io/chi-tiet-dau-gia/"
            + el.getAuction().getId()
            + "'  +style=\"color: #5b9bd5;text-decoration: underline; text-transform: uppercase; target=\"_blank\"\">tại đây</a>\r\n"
            + "					<br>\r\n"
            + "					Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Trợ giúp</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sử dụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/lien-he\">Liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\" width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(
        el.getUser().getEmail(),
        "Xin chào " + el.getUser().getName() + "! ĐẤU GIÁ MÀ BẠN YÊU THÍCH SẮP BẮT ĐẦU",
        body);
  }

  public void giftcodeMail(@PathVariable User user, @PathVariable String giftcode) {
    String body =
        "<!DOCTYPE html>\r\n"
            + "<html lang=\"en\">\r\n"
            + "\r\n"
            + "<head>\r\n"
            + "    <meta charset=\"UTF-8\">\r\n"
            + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
            + "    <title>Giftcode mail</title>\r\n"
            + "</head>\r\n"
            + "\r\n"
            + "<body style=\"        \r\n"
            + "width: 600px;\r\n"
            + "border: 1px solid EAF0F2;\r\n"
            + "background:#EAF0F2\">\r\n"
            + "    <div style=\"background-color: white !important;\">\r\n"
            + "        <div style=\"background-color: #26639F;\r\n"
            + "        padding: 15px 52px;\r\n"
            + "        display: flex;\">\r\n"
            + "            <div style=\"\r\n"
            + "            background-color: #26639F;\r\n"
            + "            width: 120px;\r\n"
            + "            height: 32px;\r\n"
            + "            padding-top: 16px;\">\r\n"
            + "                <img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619148159836-logo.png?alt=media&token=b8475deb-3dff-414a-82c2-454e2baf1af7\"\r\n"
            + "                    alt=\"\">\r\n"
            + "            </div>\r\n"
            + "            <div style=\"display: flex;\r\n"
            + "            color: white;\r\n"
            + "            padding-left: 120px;\r\n"
            + "            padding-top: 16px;\r\n"
            + "            \">\r\n"
            + "                <a href=\"http://daugia.io/AC-Auction/tro-giup\" style=\" color: white;\r\n"
            + "                text-decoration: none;font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 13px;\r\n"
            + "                line-height: 15px;\">Trợ giúp</a>\r\n"
            + "                <span style=\"padding: 0px 10px;\">|</span>\r\n"
            + "                <a href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" style=\" color: white;\r\n"
            + "                text-decoration: none;font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 13px;\r\n"
            + "                line-height: 15px;\">Điều khoản sử dụng</a>\r\n"
            + "                <span style=\"padding: 0px 10px;\">|</span>\r\n"
            + "                <a href=\"http://daugia.io/AC-Auction/tro-giup\" style=\" color: white;\r\n"
            + "                text-decoration: none;font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 13px;\r\n"
            + "                line-height: 15px;\">Liên hệ</a>\r\n"
            + "            </div>\r\n"
            + "        </div>\r\n"
            + "        <div style=\"padding: 20px 75px 40px 75px;\">\r\n"
            + "            <div>\r\n"
            + "                <div>\r\n"
            + "                    <img style=\" height: 190px;\r\n"
            + "                    width: 451px;\"\r\n"
            + "                        src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1622280839347-giftcode.png?alt=media&token=f9a5ec12-4ce0-4d49-9284-0713bdcdd6f8\"\r\n"
            + "                        alt=\"\">\r\n"
            + "                </div>\r\n"
            + "                <div>\r\n"
            + "                    <div style=\"  font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: bold;\r\n"
            + "                    font-size: 24px;\r\n"
            + "                    line-height: 28px;\r\n"
            + "                    text-align: center;\r\n"
            + "                    color: #2E67B1;\r\n"
            + "                    margin: 15px 0px 10px 0px;\">Chúc mừng\r\n"
            + "                        <span>"
            + user.getName()
            + "</span> !\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\" font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    text-align: center;\r\n"
            + "                    margin-bottom: 20px;\">Bạn đã nhận được 1 Giftcode trị giá <span style=\"color:#2E67B1;\">50.000\r\n"
            + "                            VNDT</span></br>\r\n"
            + "                        từ hệ thống sàn daugia.io</div>\r\n"
            + "                    <div style=\" background: #EAF0F2;\r\n"
            + "                    border-radius: 5px;\r\n"
            + "                    padding: 12px 26px;\r\n"
            + "                    font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: bold;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    text-align: center;\r\n"
            + "                    color: #2E67B1;\">"
            + giftcode
            + "</div>\r\n"
            + "                    <div style=\"font-family: Roboto;\r\n"
            + "                    font-style: italic;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    text-align: center;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    margin: 10px 0px 29px 0px;\">Mã giftcode</div>\r\n"
            + "                </div>\r\n"
            + "                <div style=\"text-align:center\">\r\n"
            + "                    <a href=\"https://daugia.io\" style=\"padding: 13.5px 50px;\r\n"
            + "                    background-color: #2E67B1;\r\n"
            + "                    text-decoration: none;\r\n"
            + "                    font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: bold;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    color: #FFFFFF;\r\n"
            + "                    margin: auto;\">TRẢI NGHIỆM NGAY</a>\r\n"
            + "                </div>\r\n"
            + "            </div>\r\n"
            + "            <div style=\"margin-top: 73.5px;\">\r\n"
            + "                <div class=\"content-text-use\">\r\n"
            + "                    <div style=\"font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: bold;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    color: #002F5E;\r\n"
            + "                    margin-bottom: 15px;\">CÁCH SỬ DỤNG GIFTCODE:</div>\r\n"
            + "                    <div style=\"font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    margin-bottom: 20px;\">Cách 1: Hãy nhập mã <a href=\"https://daugia.io/nhap-giftcode\">TẠI ĐÂY.</a>\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\"font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    margin-bottom: 20px;\">Cách 2: Làm theo hướng dẫn dưới đây.\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\"width: 400px;\r\n"
            + "                    height: 224px;\r\n"
            + "                    margin-bottom: 15px;\r\n"
            + "                    margin-left: 25px;\r\n"
            + "                    background: #000000;\">\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\"font-family: Roboto;\r\n"
            + "                    font-style: italic;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    text-align: center;\r\n"
            + "                    margin-bottom: 35px;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    \">Hướng dẫn nạp tiền và dùng giftcode</div>\r\n"
            + "                </div>\r\n"
            + "                <div class=\"content-text-sp\">\r\n"
            + "                    <div style=\"  font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: bold;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #002F5E;\">Bạn có thể làm gì với GIFTCODE này?</div>\r\n"
            + "                    <div style=\" font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    margin: 10px 0;\">1. Với Với 50.000 VNDT, bạn có thể tham gia tới 10 phiên đấu giá.\r\n"
            + "                    </div>\r\n"
            + "                    <a style=\" font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #0084FE;\r\n"
            + "                    text-decoration: none;\">Tìm hiểu thêm về Ticket đấu giá Daugia.io</a>\r\n"
            + "                    <div style=\" font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #4D4D4D;\r\n"
            + "                    margin: 10px 0;\">2. Đặt giá & sở hữu sản phẩm gấp 100 lần giá trị Giftcode.</div>\r\n"
            + "                    <a style=\" font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 14px;\r\n"
            + "                    line-height: 16px;\r\n"
            + "                    color: #0084FE;\r\n"
            + "                    text-decoration: none;\" href=\"\">Tìm hiểu về Đặt cọc phiên Daugia.io</a>\r\n"
            + "                </div>\r\n"
            + "            </div>\r\n"
            + "        </div>\r\n"
            + "    </div>\r\n"
            + "    <div class=\"under\">\r\n"
            + "        <div style=\"margin-top: 35px;\r\n"
            + "        border: 2px solid #4D4D4D;\"></div>\r\n"
            + "        <div style=\" font-family: Varta;\r\n"
            + "        font-style: normal;\r\n"
            + "        font-weight: bold;\r\n"
            + "        font-size: 14px;\r\n"
            + "        line-height: 20px;\r\n"
            + "\r\n"
            + "        color: #4D4D4D;\">CÔNG TY CỔ PHẦN TẬP ĐOÀN ĐẦU TƯ VÀ PHÁT TRIỂN DỰ ÁN Á CHÂU</div>\r\n"
            + "        <div style=\"font-family: Varta;\r\n"
            + "        font-style: normal;\r\n"
            + "        font-weight: normal;\r\n"
            + "        font-size: 13px;\r\n"
            + "        line-height: 19px;\">Địa chỉ: Tầng 4, TTTM V+, Số 505 Minh Khai, P. Vĩnh Tuy, Q. Hai Bà Trưng, Tp. Hà\r\n"
            + "            Nội.<br>\r\n"
            + "            Điện thoại: 024.368.66666 - số máy lẻ 204<br>\r\n"
            + "            Email: contact@daugia.io<br>\r\n"
            + "            Website: <a href=\"https://daugia.io\" style=\"color: black;\">https://daugia.io</a></div>\r\n"
            + "    </div>\r\n"
            + "</body>\r\n"
            + "\r\n"
            + "</html>";
    sendEmail(
        user.getEmail(),
        "Xin chào " + user.getName() + "! Giftcode 50K trải nghiệm tại Daugia.io [Hạn nhập mã 24H]",
        body);
  }

  public void completedRegisterMail(@PathVariable User user) {
    String body =
        "<!DOCTYPE html>\r\n"
            + "<html lang=\"en\">\r\n"
            + "<head>\r\n"
            + "    <meta charset=\"UTF-8\">\r\n"
            + "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
            + "    <title>Document</title>\r\n"
            + "</head>\r\n"
            + "<body style=\"background-color: #E5E5E5;\">\r\n"
            + "            <div style=\"background-image: url('https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619148109113-background.png?alt=media&token=2ad9cd11-c724-4156-8811-c995de3f3dcf');\r\n"
            + "            width: 600px;height: 252px;margin:auto\">\r\n"
            + "                <div style=\"\r\n"
            + "                width: 600px;\r\n"
            + "                height: 56px;\r\n"
            + "                top: 0px;\r\n"
            + "                background: #002B5E;\r\n"
            + "                display: flex;\r\n"
            + "                \">\r\n"
            + "                    <div style=\"\r\n"
            + "                    width: 120px;\r\n"
            + "                    height: 32px;\r\n"
            + "                    padding-left: 68px;\r\n"
            + "                    padding-top: 14px;\">\r\n"
            + "                        <img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619148159836-logo.png?alt=media&token=b8475deb-3dff-414a-82c2-454e2baf1af7\" alt=\"\" width=\"130px\" height=\"32px\">\r\n"
            + "                    </div>\r\n"
            + "                    <div style=\"padding-left: 130px;\r\n"
            + "                    padding-top: 28px;\">\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/tro-giup\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Trợ giúp</a>\r\n"
            + "                        <span style=\"color: white;font-family: Roboto;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">|</span>\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Điều khoản sử dụng</a>\r\n"
            + "                        <span style=\"color: white;font-family: Roboto;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">|</span>\r\n"
            + "                        <a href=\"http://daugia.io/AC-Auction/lien-he\" style=\"color: white;font-family: Roboto;\r\n"
            + "                        text-decoration: none;\r\n"
            + "                        font-style: normal;\r\n"
            + "                        font-weight: normal;\r\n"
            + "                        font-size: 13px;\r\n"
            + "                        line-height: 15px;\r\n"
            + "                        text-align: center;\">Liên hệ</a>\r\n"
            + "                    </div>\r\n"
            + "                </div>\r\n"
            + "                <div style=\"width: 86px;\r\n"
            + "                height: 86px;\r\n"
            + "                padding-left: 257px;\r\n"
            + "                padding-top: 36px;\">\r\n"
            + "                    <img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1619161647246-akar-icons_circle-check.png?alt=media&token=b7fed844-b6b7-4c08-875b-d61e6a9c467a\" alt=\"\">\r\n"
            + "                </div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: bold;\r\n"
            + "                font-size: 28px;\r\n"
            + "                line-height: 33px;\r\n"
            + "                text-align: center;\r\n"
            + "                padding-top:17px;\r\n"
            + "                color: #FFFFFF;\">Tài khoản được kích hoạt thành công</div>\r\n"
            + "            </div>\r\n"
            + "            <div style=\"background-color: white;width: 600px;padding-bottom: 50px;margin: auto;\">\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                color: #444444;\r\n"
            + "                padding-left:65px;\r\n"
            + "                padding-top:45px\">Kính chào "
            + user.getName()
            + ",</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left:65px;\r\n"
            + "                padding-top: 13px;\r\n"
            + "                color: #444444;\">Bạn đã xác nhận email của mình thành công.</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left:65px;\r\n"
            + "                padding-bottom: 20px;\r\n"
            + "                color: #444444;\">Bây giờ bạn có thể đăng nhập tại daugia.io bằng email của mình.</div>\r\n"
            + "                <div style=\"text-align:center\">\r\n"
            + "                    <a href=\"https://daugia.io/dang-nhap\" style=\"\r\n"
            + "                    padding: 10px 20px;\r\n"
            + "                    margin-top: 20px;\r\n"
            + "                    background: #2E67B1;\r\n"
            + "                    font-family: Roboto;\r\n"
            + "                    font-style: normal;\r\n"
            + "                    font-weight: normal;\r\n"
            + "                    font-size: 18px;\r\n"
            + "                    line-height: 21px;\r\n"
            + "                    color: #FFFFFF;\r\n"
            + "                    text-align: center;\r\n"
            + "                    text-decoration: none;\">\r\n"
            + "                        Đăng nhập ngay\r\n"
            + "                    </a>\r\n"
            + "                </div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                color: #444444;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                padding-top:25px;\">Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                padding-top:35px;\r\n"
            + "                color: #444444;\">Trân trọng,</div>\r\n"
            + "                <div style=\"font-family: Roboto;\r\n"
            + "                font-style: normal;\r\n"
            + "                font-weight: normal;\r\n"
            + "                font-size: 18px;\r\n"
            + "                line-height: 21px;\r\n"
            + "                padding-left: 65px;\r\n"
            + "                color: #444444;\">Đội ngũ AC Auction.</div>\r\n"
            + "            </div>\r\n"
            + "            <div style=\"width: 600px;  \r\n"
            + "            height: 29px;\r\n"
            + "            background: #012A5E;\r\n"
            + "            padding: 10px 0px 0px 0px;\r\n"
            + "            font-family: Roboto;\r\n"
            + "            font-style: normal;\r\n"
            + "            font-weight: normal;\r\n"
            + "            font-size: 14px;\r\n"
            + "            text-align: center;\r\n"
            + "            color: #FFFFFF;\r\n"
            + "            margin: auto;\">Bản quyền © 2021 Công ty Cổ phần Tập đoàn Đầu tư và Phát triển Dự án Á Châu.</div>\r\n"
            + "\r\n"
            + "</body>\r\n"
            + "</html>";
    sendEmail(
        user.getEmail(),
        "Xin chào " + user.getName() + "! Tài khoản được kích hoạt thành công!",
        body);
  }

  public void refuseMail(@PathVariable User winner, @PathVariable Auction auction) {
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\" width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height: 34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color: #000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform: capitalize;\">"
            + winner.getName()
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Bạn đã từ chối thanh toán sản phẩm "
            + auction.getAssest().getName()
            + "\r\n"
            + "					<br>\r\n"
            + "					Bạn có thể đăng nhập tại daugia.io để kiểm tra <span style=\"font-weight: 700;color: #000000;\"></span>\r\n"
            + "					<br>\r\n"
            + "					<a href='"
            + YandexConfig.home
            + "/dang-nhap"
            + "'  +style=\"color: #5b9bd5;text-decoration: underline; text-transform: uppercase;\">Nhấn vào đây để đăng nhập</a>\r\n"
            + "					<br>\r\n"
            + "					Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Trợ giúp</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sử dụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/lien-he\">Liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\" width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(
        winner.getEmail(),
        "Xin chào " + winner.getName() + "! Bạn đã từ chối thanh toán sản phẩm",
        body);
  }

  public void expiresMail(
      @PathVariable User winner, @PathVariable Auction auction, @PathVariable BigDecimal warranty) {
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">\r\n"
            + "	<tbody>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center; padding: 10px 0 ;\">\r\n"
            + "				<img src=\"http://auction.biso.vn/img/logo-big.png\" alt=\"\" width=\"300\">\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr>\r\n"
            + "			<td style=\"font-family: 'Roboto', sans-serif;padding: 25px;\">\r\n"
            + "				<h3 style=\"font-family: 'Roboto', sans-serif;font-size: 22px;line-height: 34px;color:#000000;font-weight: 500;margin: 0 0 30px;\">\r\n"
            + "					\r\n"
            + "				</h3>\r\n"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size: 15px;color: #000;line-height: 25px;text-align: left;\">\r\n"
            + "					Kính chào <span style=\"font-weight: 700;color: #000000; text-transform: capitalize;\">"
            + winner.getName()
            + "</span>,\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Đấu giá thắng sản phẩm "
            + auction.getAssest().getName()
            + " của bạn đã hết hạn thanh toán. Bạn đã bị trừ số tiền đặt cọc là : "
            + warranty.floatValue()
            + " VNDT."
            + "\r\n"
            + "					<br>\r\n"
            + "					Bạn có thể đăng nhập tại daugia.io để kiểm tra <span style=\"font-weight: 700;color: #000000;\"></span>\r\n"
            + "					<br>\r\n"
            + "					<a href='"
            + YandexConfig.home
            + "/dang-nhap"
            + "'  +style=\"color: #5b9bd5;text-decoration: underline; text-transform: uppercase;\">Nhấn vào đây để đăng nhập</a>\r\n"
            + "					<br>\r\n"
            + "					Chúc bạn có trải nghiệm tốt trên nền tảng của chúng tôi!\r\n"
            + "					<br>\r\n"
            + "					<br>\r\n"
            + "					Trân trọng, \r\n"
            + "					<br>\r\n"
            + "					Đội ngũ AC Auction\r\n"
            + "					<font size=\"2\" style=\"color: #818181;\">\r\n"
            + "						<br>\r\n"
            + "						<br>\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/tro-giup\" target=\"_blank\">Trợ giúp</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/dieu-khoan-su-dung\" target=\"_blank\">Điều khoản sử dụng</a>\r\n"
            + "						&nbsp;|&nbsp;\r\n"
            + "						<a style=\"color:#5b9bd5; text-decoration: underline;\" href=\"http://daugia.io/AC-Auction/lien-he\">Liên hệ chúng tôi</a>\r\n"
            + "					</font>\r\n"
            + "				</div>\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "		<tr style=\"background-color: #0063c3;\r\n"
            + "		background-image: linear-gradient(-135deg, #0063c3,#a4e3f3);\r\n"
            + "	  \">\r\n"
            + "			<td style=\"text-align: center;padding: 7px 0 ;\">\r\n"
            + "				<img src=\"https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/1617807158841-daugia.io.png?alt=media&token=ca7d035a-2d51-4488-b1b6-6f9222136686\" width=\"300\" />\r\n"
            + "			</td>\r\n"
            + "		</tr>\r\n"
            + "	</tbody>\r\n"
            + "</table>";
    sendEmail(
        winner.getEmail(),
        "Đấu giá thắng sản phẩm "
            + auction.getAssest().getName()
            + " của bạn đã hết hạn thanh toán",
        body);
  }

  public void winAuctionMail(@PathVariable User winner, @PathVariable Auction auction) {
    String body =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#fff\" style=\"color: #000;background: #fff;align-self: center;margin: 0;width: 100%;max-width: 600px;border: 5px solid #e7e8ef;\">"
            + "	<tbody>"
            + "		<tr style=\"background-color: #0063c3;background-image: linear-gradient(-15deg, #0063c3,#a4e3f3); \">"
            + "			<td style=\"padding: 1rem 2rem;background: rgba(0,43,94,1);\">"
            + "				<img src=\"http://daugia.io/images/logo-big.png\" alt=\"\" width=\"150px\">"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td>"
            + "				<div class=\"v321_54\" style=\"width: 600px;height: 203px;background: url(http://daugia.io/images/bg-mail.png);background-repeat: no-repeat;background-position: center center;"
            + "    				background-size: cover;opacity: 1;position: relative;top: 0px;left: 0px;overflow: hidden;\">"
            + "				</div>"
            + "				<div style=\"width: 109px;height: 109px;background: url(http://daugia.io/images/congratulation-logo.png);background-repeat: no-repeat;background-position: center center;background-size: cover;opacity: 1;"
            + "    				position: absolute; top: 86px;left: 246px;overflow: hidden;\">"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td>"
            + "				<h3 style=\"font-family:'Roboto',sans-serif;font-size:22px;line-height:34px;font-weight:500;margin:0 0 30px;text-align: center;color: rgba(32,128,223,1);\">"
            + "					Bạn đã đấu giá thành công"
            + "				</h3>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td style=\"padding: 1rem 2rem;\">"
            + "				<div style=\"font-family:'Roboto',sans-serif;font-size:15px;color:#000;line-height:25px;text-align:left\">"
            + "					Kính gửi <span style=\"font-weight:700;color:#000000;text-transform:capitalize\">"
            + winner.getEmail()
            + "</span>"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td style=\"padding: 1rem 2rem;\">"
            + "				<div>"
            + "					Sàn daugia.io xin gửi tới quý khách lời chào trân trọng! Cảm ơn quý khách đã quan tâm và tham gia đấu giá sản phẩm của chúng tôi.\r\n"
            + "            		Xin chúc mừng bạn đã đấu giá thành công sản phẩm: "
            + auction.getAssest().getName()
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td>"
            + "				<div style=\"width: 480px;height: 118px;background: rgba(246,246,246,1);opacity: 1;position: absolute;top: 503px;left: 520px;overflow: hidden;\">"
            + "				</div>"
            + "				<div style=\"width: 480px;height: 118px;background: rgba(246,246,246,1);opacity: 1;position: absolute;top: 503px;left: 520px;overflow: hidden;\">"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td style=\"padding: 1rem 2rem;\">"
            + "				<div>"
            + "					Đây là thư thông báo đính kèm, quý khách vui lòng cung cấp thông tin nhận hàng tại đây:"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td>"
            + "				<div style=\"width: 160px;height: 20px;background: rgba(46,103,177,1);padding: 1rem 12.5rem;margin: 10px;opacity: 1;"
            + "    				position: absolute;top: 728px;left: 662px;overflow: hidden;\">"
            + "					<span style=\"width: 156px;color: rgba(255,255,255,1);position: absolute;top: 10px;left: 20px;font-family: Roboto;"
            + "						font-weight: Regular;font-size: 14px;opacity: 1; text-align: left;\">Điền thông tin nhận hàng</span>"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td style=\"padding: 1rem 2rem;\">"
            + "				<div>"
            + "					<span >Mọi thắc mắc vui lòng phản hồi email dưới đây hoặc gọi vào đường dây nóng 1900638012 (Cước phí 1,000 đ/phút) để được hỗ trợ.\r\n"
            + "						Còn rất nhiều sản phẩm hấp dẫn khác đang chờ bạn đấu giá!</span>"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td style=\"padding: 1rem 2rem;\">"
            + "				<div>"
            + "					<span >Trân trọng!Daugia.io</span>"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "		<tr>"
            + "			<td>"
            + "				<div style=\"background: rgba(1,42,94,1);padding: 1rem 3rem;\">"
            + "					<span style=\"color: rgba(255,255,255,1);position: absolute;font-family: Roboto;font-weight: Regular;font-size: 14px;"
            + "    					opacity: 1;text-align: center;\">Bản quyền © 2021 Công ty Cổ phần Tập đoàn Đầu tư và Phát triển Dự án Á Châu.</span>"
            + "				</div>"
            + "			</td>"
            + "		</tr>"
            + "</body>"
            + "</table>";
    sendEmail(winner.getEmail(), "Xin chào " + winner.getName() + "! Bạn đã thắng đấu giá", body);
  }
}
