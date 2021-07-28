package vn.acgroup.entities;

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
public class LikedAuction {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "liked_auction_generator")
  @SequenceGenerator(
      name = "liked_auction_generator",
      sequenceName = "liked_auction_seq",
      allocationSize = 500)
  private long id;

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

  public Auction getAuction() {
    return auction;
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
  }

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonManagedReference
  private User user;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "auction_id")
  @JsonManagedReference
  private Auction auction;

  private boolean isDelete;

  public boolean isDelete() {
    return isDelete;
  }

  public void setDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }
}
