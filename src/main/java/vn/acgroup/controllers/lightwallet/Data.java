package vn.acgroup.controllers.lightwallet;

public class Data {
  private String address;
  private String fromAddress;
  private String toAddress;
  private String hash;
  private String amount;
  private String fee;
  private String status;
  private boolean invalid;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getToAddress() {
    return toAddress;
  }

  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getFee() {
    return fee;
  }

  public void setFee(String fee) {
    this.fee = fee;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isInvalid() {
    return invalid;
  }

  public void setInvalid(boolean invalid) {
    this.invalid = invalid;
  }

  public Data() {};

  public Data(
      String address,
      String fromAddress,
      String toAddress,
      String hash,
      String amount,
      String fee,
      String status) {
    this.address = address;
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.hash = hash;
    this.amount = amount;
    this.fee = fee;
    this.status = status;
  }

  public Data(
      String address,
      String fromAddress,
      String toAddress,
      String hash,
      String amount,
      String fee,
      boolean invalid,
      String status) {
    this.address = address;
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.hash = hash;
    this.amount = amount;
    this.fee = fee;
    this.invalid = invalid;
    this.status = status;
  }
}
