package vn.acgroup.controllers.api;

import java.time.LocalDate;

public class EditUserInformation {

  private String avatar;
  private String fullname;

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  private String lastname;
  private String gender;
  private LocalDate dateofbirth;
  private String mobile;
  private String province;
  private String group;

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public LocalDate getDateofbirth() {
    return dateofbirth;
  }

  public void setDateofbirth(LocalDate dateofbirth) {
    this.dateofbirth = dateofbirth;
  }
}
