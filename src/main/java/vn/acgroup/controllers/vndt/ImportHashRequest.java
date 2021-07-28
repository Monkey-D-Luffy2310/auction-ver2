package vn.acgroup.controllers.vndt;

import com.google.gson.Gson;

public class ImportHashRequest {
  private String hash;
  private String id;
  private String token;
  private BankInfomation info;

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public BankInfomation getInfo() {
    return info;
  }

  public void setInfo(BankInfomation info) {
    this.info = info;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
