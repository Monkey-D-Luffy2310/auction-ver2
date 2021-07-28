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
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_generator")
  @SequenceGenerator(name = "address_generator", sequenceName = "address_seq", allocationSize = 500)
  private long id;

  private String name;
  private String mobile = "";
  private String address = "";
  private boolean isDefault;
  private long user;
  private boolean isDelete = false;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public long getUser() {
    return user;
  }

  public void setUser(long user) {
    this.user = user;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public boolean isDelete() {
    return isDelete;
  }

  public void setDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }
}
