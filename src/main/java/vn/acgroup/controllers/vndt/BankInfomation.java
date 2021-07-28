package vn.acgroup.controllers.vndt;

public class BankInfomation {
  private String orderId;
  private String bankCode;
  private String accountNumber;
  private String accountName;
  private String note;

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getBankCode() {
    return bankCode;
  }

  public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public BankInfomation() {};

  public BankInfomation(
      String orderId, String bankCode, String accountNumber, String accountName, String note) {
    super();
    this.orderId = orderId;
    this.bankCode = bankCode;
    this.accountNumber = accountNumber;
    this.accountName = accountName;
    this.note = note;
  }
}
