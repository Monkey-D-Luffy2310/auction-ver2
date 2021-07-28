package vn.acgroup.controllers.api;

import java.math.BigDecimal;

public class WithdrawRequest {
  private String to;
  private BigDecimal amount;
  private String description;
  private String note = "";

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public WithdrawRequest() {}

  public WithdrawRequest(String to, BigDecimal amount, String description, String note) {
    this.setAmount(amount);
    this.setDescription(description);
    this.setTo(to);
    this.setNote(note);
  }

  public WithdrawRequest(String to, BigDecimal amount, String description) {
    this.setAmount(amount);
    this.setDescription(description);
    this.setTo(to);
  }
}
