package vn.acgroup.controllers.lightwallet;

public class EnterGiftcodeRequest {

  private String code;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  private String address;
  private String pincode;
  private String senderId;
  private String recipientId;
  private String note;

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getPincode() {
    return pincode;
  }

  public void setPincode(String pincode) {
    this.pincode = pincode;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public EnterGiftcodeRequest() {};

  public EnterGiftcodeRequest(String address, String code) {
    this.setAddress(address);
    this.setCode(code);
    this.setPincode("");
    this.setRecipientId("");
    this.setSenderId("");
    this.setNote("");
  }
}
