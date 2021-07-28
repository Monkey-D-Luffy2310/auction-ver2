package vn.acgroup.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class NoticeRegister {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_register_generator")
  @SequenceGenerator(
      name = "notice_register_generator",
      sequenceName = "notice_register_seq",
      allocationSize = 500)
  private long id;

  private String email;
  private String verifyCode;
  private boolean isVerify;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getVerifycode() {
    return verifyCode;
  }

  public void setVerifycode(String verifycode) {
    this.verifyCode = verifycode;
  }

  public boolean isVerify() {
    return isVerify;
  }

  public void setVerify(boolean isVerify) {
    this.isVerify = isVerify;
  }
}
