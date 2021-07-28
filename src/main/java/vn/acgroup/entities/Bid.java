package vn.acgroup.entities;

import java.math.BigDecimal;
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

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Bid {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_generator")
  @SequenceGenerator(name = "bid_generator", sequenceName = "bid_seq", allocationSize = 500)
  private long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "auction_id")
  @JsonBackReference
  private Auction auction;

  private String status = "true";

  private LocalDateTime created = LocalDateTime.now((ZoneId.of("Asia/Ho_Chi_Minh")));

  private LocalDateTime updated = LocalDateTime.now((ZoneId.of("Asia/Ho_Chi_Minh")));

  private BigDecimal bidPrice = BigDecimal.ZERO;

  public Bid() {}

  public Bid(User user, Auction auction) {
    this.setAuction(auction);
    this.setUser(user);
    this.setStatus("true");
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(BigDecimal bidPrice) {
    this.bidPrice = bidPrice;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Auction getAuction() {
    return auction;
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
  }
}
