package vn.acgroup.entities;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class AutoBid {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autoBid_generator")
  @SequenceGenerator(name = "autoBid_generator", sequenceName = "autoBid_seq", allocationSize = 500)
  private long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonManagedReference
  private User user;

  private long auctionId;

  public long getAuctionId() {
    return auctionId;
  }

  public void setAuctionId(long auctionId) {
    this.auctionId = auctionId;
  }

  private long maxBid;
  private boolean isActive;
  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public long getMaxBid() {
    return maxBid;
  }

  public void setMaxBid(long maxBid) {
    this.maxBid = maxBid;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}
