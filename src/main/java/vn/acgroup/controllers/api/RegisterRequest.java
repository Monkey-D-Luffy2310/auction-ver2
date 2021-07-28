package vn.acgroup.controllers.api;

public class RegisterRequest {

  private String email;
  private String password;
  private String name;
  private String sponsor;
  private String mobile;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSponsor() {
    return sponsor;
  }

  public void setSponsor(String sponsor) {
    this.sponsor = sponsor;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
