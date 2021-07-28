package vn.acgroup.controllers.lightwallet;

import java.math.BigDecimal;

import org.json.JSONObject;

import vn.acgroup.controllers.api.WithdrawRequest;

public class Withdraw {

  private String to;
  private BigDecimal amount;
  private String data = "{\"gasValue\": \"0\"}";
  private String currency = "VNDT";
  private BigDecimal fee = BigDecimal.valueOf(0);
  private String description = "Rút tiền từ daugia.io";

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal fee) {
    this.fee = fee;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Withdraw() {}

  public Withdraw(WithdrawRequest withdrawRequest) {
    this.setTo(withdrawRequest.getTo());
    this.setAmount(withdrawRequest.getAmount());
    this.setDescription(withdrawRequest.getDescription());
    this.setCurrency("VNDT");
    try {
      JSONObject data = new JSONObject();
      data.put("gasValue", "1");
      data.put("note", withdrawRequest.getNote());
      this.data = data.toString();
    } catch (Exception e) {
      System.out.println("put note ex: " + e.getMessage());
    }
  }
}
