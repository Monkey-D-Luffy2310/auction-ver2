package vn.acgroup.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuctionRegister {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_register_generator")
  @SequenceGenerator(
      name = "auction_register_generator",
      sequenceName = "auction_register_seq",
      allocationSize = 500)
  private long id;

  private int turn;

  private BigDecimal warranty;

  private Boolean depositing;

  public Boolean getDepositing() {
    return depositing;
  }

  public void setDepositing(Boolean depositing) {
    this.depositing = depositing;
  }

  public BigDecimal getWarranty() {
    return warranty;
  }

  public void setWarranty(BigDecimal warranty) {
    this.warranty = warranty;
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonManagedReference
  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "auction_id")
  @JsonManagedReference
  private Auction auction;

  public Auction getAuction() {
    return auction;
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
  }

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  private Boolean isDeleted;

  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public AuctionRegister() {}

  public AuctionRegister(User user, Auction auction) {
    this.setAuction(auction);
    this.setUser(user);
    this.setIsDeleted(false);
    this.setDepositing(false);
    this.setTurn(1);
  }

  public void cancel() {
    this.setIsDeleted(true);
    if (this.getWarranty() != null) this.getUser().unfreeze(this.getWarranty());
  }
}
