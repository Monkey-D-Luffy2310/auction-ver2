package vn.acgroup.controllers.vndt;

public class Deposit {
  private String token;
  private String type;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public VndtData getData() {
    return data;
  }

  public void setData(VndtData data) {
    this.data = data;
  }

  private VndtData data;
}
