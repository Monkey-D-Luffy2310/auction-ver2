package vn.acgroup.controllers.lightwallet;

public class AdditionInfo {
  private String bankName = null;
  private String accountNumber = null;
  private String accountName = null;
  private Double commission;
  private String tokenAddress = null;

  private String tag = null;
  private double feeUsed = 0.0;

  // address that send coin
  private String addressSendPayment = null;
  private String transId = null;

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
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

  public String getAddressSendPayment() {
    return addressSendPayment;
  }

  public void setAddressSendPayment(String addressSendPayment) {
    this.addressSendPayment = addressSendPayment;
  }

  public String getTokenAddress() {
    return tokenAddress;
  }

  public void setTokenAddress(String tokenAddress) {
    this.tokenAddress = tokenAddress;
  }

  public Double getCommission() {
    return commission;
  }

  public void setCommission(Double commission) {
    this.commission = commission;
  }

  public String getTransId() {
    return transId;
  }

  public void setTransId(String transId) {
    this.transId = transId;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public AdditionInfo() {}

  public AdditionInfo(
      String bankName, String accountNumber, String accountName, String walletAddress) {
    this.accountName = accountName;
    this.accountNumber = accountNumber;
    this.bankName = bankName;
    this.addressSendPayment = walletAddress;
  }

  public AdditionInfo(String walletAddress) {
    this.addressSendPayment = walletAddress;
  }

  public double getFeeUsed() {
    return feeUsed;
  }

  public void setFeeUsed(double feeUsed) {
    this.feeUsed = feeUsed;
  }
}
