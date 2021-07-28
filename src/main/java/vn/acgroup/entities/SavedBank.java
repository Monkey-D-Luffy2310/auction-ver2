package vn.acgroup.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import javax.persistence.Id;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.google.gson.Gson;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class SavedBank {
  @Id private String accountNumber;
  private String accountName;
  private String bankCode;
  private String bankName;

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

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public SavedBank() {};

  public SavedBank(String accountNumber, String accountName, String bankCode, String bankName) {
    this.accountNumber = accountNumber;
    this.accountName = accountName;
    this.bankCode = bankCode;
    this.bankName = bankName;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
