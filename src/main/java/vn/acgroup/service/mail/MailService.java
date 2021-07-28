package vn.acgroup.service.mail;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.LikedAuction;
import vn.acgroup.entities.User;

public interface MailService {

  public void sendEmail(String to, String subject, String body);

  public void forgot(String email) throws InterruptedException, ExecutionException;

  public void noticeRegister(String email);

  public void winMail(User user, Auction auction);

  public void userRegisterMail(User user, String code);

  public void withdrawMail(User user, String code);

  public void upcomingAuctionMail(LikedAuction el, Long time);

  public void giftcodeMail(User user, String giftcode);

  public void completedRegisterMail(User user);

  public void refuseMail(User user, Auction auction);

  public void expiresMail(User user, Auction auction, BigDecimal warranty);

  public void winAuctionMail(User user, Auction auction);
}
