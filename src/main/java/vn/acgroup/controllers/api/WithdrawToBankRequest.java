package vn.acgroup.controllers.api;

import java.math.BigDecimal;

public class WithdrawToBankRequest {

  private String accountNumber;
  private String accountName;
  private String bankCode;
  private BigDecimal amount;
  private String description;
  private boolean saveInfo = false;
  private String information = "";

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

  public String getBankCode() {
    return bankCode;
  }

  public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
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

  public boolean isSaveInfo() {
    return saveInfo;
  }

  public void setSaveInfo(boolean saveInfo) {
    this.saveInfo = saveInfo;
  }

  public String getInformation() {
    return information;
  }

  public void setInformation(String information) {
    this.information = information;
  }
}
