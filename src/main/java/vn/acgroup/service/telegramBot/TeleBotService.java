package vn.acgroup.service.telegramBot;

import java.io.IOException;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.User;

@Service
public class TeleBotService {

  static Logger log = Logger.getLogger(TeleBotService.class.getName());

  private static final String BOT_TOKEN = "1800727175:AAGsM6_Q42QWU7-wBUOWtcngjFw4SXqTGnM";
  private static final String GROUP_DAU_GIA_ID = "-451456663";

  //	public  void main(String[] args) throws Exception {
  ////		String webhook =
  // "https://login.acwallet.io/api/public/v1/tele-bot-webhook?token="+TOKEN_WEBHOOK;
  ////		setWebhook(webhook);
  //		sendMessage(GROUP_DAU_GIA_ID, "Phiên đấu giá thắng mới! Mã đấu giá: 123456. Sản phẩm: Nồi cơm
  // điện. Người thắng: xuanmai@acgroup.vn");
  //	}

  public String notifiWinAuction(Auction auction, User winner) {
    try {
      String message =
          "Phiên đấu giá thắng mới! Mã đấu giá: "
              + auction.getId()
              + ". Sản phẩm: "
              + auction.getAssest().getName()
              + ". Người thắng: "
              + winner.getName()
              + ". Email: "
              + winner.getEmail();
      return sendMessage(GROUP_DAU_GIA_ID, message);
    } catch (Exception e) {
      System.out.println("notifi win aution tele ex: " + e.getMessage());
      return "";
    }
  }

  public String sendMessage(long chat_id, String message) throws IOException {
    return sendMessage(chat_id + "", message);
  }

  public String sendMessage(String chat_id, String message) throws IOException {
    String url =
        "https://api.telegram.org/bot"
            + BOT_TOKEN
            + "/sendMessage?chat_id="
            + chat_id
            + "&text="
            + message;
    Document doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).get();
    System.out.println(doc.body().text());
    return doc.body().text();
  }

  public String setWebhook(String urlWebhook) throws IOException {
    String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/setWebhook?url=" + urlWebhook;
    Document doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).get();
    System.out.println(doc.body().text());
    return doc.body().text();
  }
}
