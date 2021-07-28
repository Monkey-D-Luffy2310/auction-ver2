package vn.acgroup.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
  @SequenceGenerator(
      name = "category_generator",
      sequenceName = "category_seq",
      allocationSize = 500)
  private long id;

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  private String name;
  private String alias;
  private String avatar;

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getUpdated() {
    return updated;
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
  }
}
