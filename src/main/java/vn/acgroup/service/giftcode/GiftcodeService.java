package vn.acgroup.service.giftcode;

import java.io.IOException;
import java.math.BigDecimal;
import org.json.JSONException;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;

public interface GiftcodeService {

  public void createGiftcode(long id) throws CustomException, JSONException, IOException;

  public String enterGiftcode(User user, String giftcode)
      throws JSONException, CustomException, IOException;

  public void bonus(long user_id, BigDecimal quantity);
}
