package vn.acgroup.service.giftcode;

import java.io.IOException;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.config.VndtConfig;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.NoticeRegisterRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.wallet.WalletService;

@Service
public class ACGiftcodeService implements GiftcodeService {

  @Autowired UserRepository userRepository;
  @Autowired NoticeRegisterRepository noticeRegisterRepository;
  @Autowired AssetRepository assetRepository;
  @Autowired MailService mailService;
  @Autowired WalletService walletService;

  public void createGiftcode(@PathVariable long id)
      throws CustomException, JSONException, IOException {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new CustomException("User address invalid", 400));
    String url = "https://login.acwallet.io/user/api/create-giftcode";
    String token = VndtConfig.acw_tk; // -> token login cuar ac wallet
    JSONObject data = new JSONObject();
    data.put("currencyCode", 12); // currency code cua VNDT la 12
    data.put("note", "KYC");
    data.put("quantity", 50000); // so tien tren moi giftcode
    data.put("number", 1); // so luong giftcode se tao
    JSONObject conditions = new JSONObject();
    conditions.put("type", "fixedReceiver");
    conditions.put("pin", "");
    conditions.put("uid", "");
    conditions.put("address", user.getBonusAddress()); // —> địa chỉ đc fix cứng sẽ nhận Gift
    data.put("conditions", conditions);
    Response res =
        Jsoup.connect(url)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .header("acw_tk", token)
            .header("Content-Type", "application/json")
            .requestBody(data.toString())
            .method(Method.POST)
            .execute();
    if (res.statusCode() != 200) throw new CustomException(res.body(), res.statusCode());
    JSONObject resJS = new JSONObject(res.body());
    String giftcode = new JSONArray(resJS.getString("giftcodes")).getString(0);
    mailService.giftcodeMail(user, giftcode);
  }

  public String enterGiftcode(@PathVariable User user, @PathVariable String giftcode)
      throws JSONException, CustomException, IOException {
    String Vndt_id = "ACA" + Long.toHexString(user.getId() + 1000);
    String url = "https://login.acwallet.io/api/v1/enter-giftcode";
    JSONObject data = new JSONObject();
    data.put("code", giftcode);
    data.put("address", user.getBonusAddress());
    data.put("note", Vndt_id);
    Response res =
        Jsoup.connect(url)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .header("Content-Type", "application/json")
            .requestBody(data.toString())
            .method(Method.POST)
            .execute();
    System.out.println(res.body());
    JSONObject resJS = new JSONObject(res.body());
    int statusCode = resJS.getInt("status");
    System.out.println("statusCode: " + statusCode);
    if (statusCode != 200) {
      throw new CustomException(res.body(), res.statusCode());
    }

    long quantity = resJS.getLong("quantity");
    BigDecimal val = new BigDecimal(quantity);
    bonus(user.getId(), val);
    System.out.println("return quan tity");
    return quantity + "";
  }

  public void bonus(@PathVariable long user_id, @PathVariable BigDecimal quantity) {
    // % tặng thêm: <100k->20%,  100k-> 200k (20%), 200k -> 500k (25%), từ 500k trở lên (100%)
    // tài khoản giftcode max 1 triệu
    try {

      User admin = userRepository.findById(LightwalletConfig.adminId).get();
      User user = userRepository.findById(user_id).get();
      BigDecimal percent = null;
      if (quantity.compareTo(BigDecimal.valueOf(100000)) < 0) percent = BigDecimal.valueOf(15);
      else if (quantity.compareTo(BigDecimal.valueOf(200000)) < 0) percent = BigDecimal.valueOf(20);
      else if (quantity.compareTo(BigDecimal.valueOf(500000)) < 0) percent = BigDecimal.valueOf(25);
      else percent = BigDecimal.valueOf(100);
      WithdrawRequest withdrawRequest =
          new WithdrawRequest(
              user.getBonusAddress(),
              quantity.multiply(percent).divide(new BigDecimal(100)),
              "Bonus " + percent + " percent giftcode");
      walletService.sendBonus(admin, withdrawRequest);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
