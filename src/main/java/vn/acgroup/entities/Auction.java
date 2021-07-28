package vn.acgroup.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import vn.acgroup.controllers.api.AddAuctionRequest;

import vn.acgroup.utils.Utils;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Auction {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_generator")
  @SequenceGenerator(name = "auction_generator", sequenceName = "auction_seq", allocationSize = 500)
  private long id;

  private Integer dashEndIn;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  private long winner;

  private String status = "New";

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  private LocalDateTime startAt;

  private LocalDateTime endAt;

  // giá khởi điểm đấu giá
  private BigDecimal bidPrice;

  private BigDecimal currentPrice;

  // giá thắng
  private BigDecimal winPrice;

  private double sellOffPercent = 80.0; // % giá trị cty sẽ mua lại sp

  public BigDecimal getWinPrice() {
    return winPrice;
  }

  public void setWinPrice(BigDecimal winPrice) {
    this.winPrice = winPrice;
  }

  private String type;
  private Float timeLeft;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "assest_id")
  @JsonBackReference
  private Asset assest;

  @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Bid> bids;

  private String category;

  private BigDecimal warranty;
  private String note = "";
  private BigDecimal buyPrice;
  private char showInBaner = '0';
  private Boolean liveStream = false;
  private BigDecimal stepPrice;
  private long seller = 0;
  private String area;
  private String regulation;
  private int percent;
  private Integer attendingUser;
  private long addressId;

  private LocalDateTime attendanceDeadline;

  private BigDecimal registrationFee;
  private long currentWinner;

  public BigDecimal getRegistrationFee() {
    return registrationFee;
  }

  public void setRegistrationFee(BigDecimal registrationFee) {
    this.registrationFee = registrationFee;
  }

  @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY)
  @JsonBackReference
  private List<AuctionRegister> auction_register;

  @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY)
  @JsonBackReference
  private List<LikedAuction> liked_auction;

  public Integer getAttendingUser() {
    return attendingUser;
  }

  public void setAttendingUser(Integer attendingUser) {
    this.attendingUser = attendingUser;
  }

  public int getPercent() {
    return percent;
  }

  public void setPercent(int percent) {
    this.percent = percent;
  }

  public long getWinner() {
    return winner;
  }

  public void setWinner(long winner) {
    this.winner = winner;
  }

  public Float getTimeLeft() {
    return timeLeft;
  }

  public void setTimeLeft(Float timeLeft) {
    this.timeLeft = timeLeft;
  }

  public long getCurrentWinner() {
    return currentWinner;
  }

  public void setCurrentWinner(long currentWinner) {
    this.currentWinner = currentWinner;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public LocalDateTime getAttendanceDeadline() {
    return attendanceDeadline;
  }

  public void setAttendanceDeadline(LocalDateTime attendanceDeadline) {
    this.attendanceDeadline = attendanceDeadline;
  }

  public long getSeller() {
    return seller;
  }

  public void setSeller(long seller) {
    this.seller = seller;
  }

  public String getRegulation() {
    return regulation;
  }

  public void setRegulation(String regulation) {
    this.regulation = regulation;
  }

  public Boolean getLiveStream() {
    return liveStream;
  }

  public void setLiveStream(Boolean liveStream) {
    this.liveStream = liveStream;
  }

  public BigDecimal getStepPrice() {
    return stepPrice;
  }

  public void setStepPrice(BigDecimal stepPrice) {
    this.stepPrice = stepPrice;
  }

  public char getShowInBaner() {
    return showInBaner;
  }

  public void setShowInBaner(char showInBaner) {
    this.showInBaner = showInBaner;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public List<Bid> getBids() {
    return bids;
  }

  public void setBids(List<Bid> bids) {
    this.bids = bids;
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

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public LocalDateTime getEndAt() {
    return endAt;
  }

  public void setEndAt(LocalDateTime endAt) {
    this.endAt = endAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Asset getAssest() {
    return assest;
  }

  public void setAssest(Asset assest) {
    this.assest = assest;
  }

  public BigDecimal getWarranty() {
    return warranty;
  }

  public void setWarranty(BigDecimal warranty) {
    this.warranty = warranty;
  }

  public List<AuctionRegister> getAuction_register() {
    return auction_register;
  }

  public void setAuction_register(List<AuctionRegister> auction_register) {
    this.auction_register = auction_register;
  }

  public BigDecimal getBuyPrice() {
    return buyPrice;
  }

  public void setBuyPrice(BigDecimal buyPrice) {
    this.buyPrice = buyPrice;
  }

  public List<LikedAuction> getLiked_auction() {
    return liked_auction;
  }

  public void setLiked_auction(List<LikedAuction> liked_auction) {
    this.liked_auction = liked_auction;
  }

  public Integer getDashEndIn() {
    return dashEndIn;
  }

  public void setDashEndIn(Integer dashEndIn) {
    this.dashEndIn = dashEndIn;
  }

  public double getSellOffPercent() {
    return sellOffPercent;
  }

  public void setSellOffPercent(double sellOffPercent) {
    this.sellOffPercent = sellOffPercent;
  }

  public Auction() {
    super();
  }

  public Auction(User user, AddAuctionRequest addAuctionRequest) {
    this.setUser(user);
    this.setStartAt(Utils.setTimeZone(addAuctionRequest.getStartAt()));
    this.setEndAt(Utils.setTimeZone(addAuctionRequest.getEndAt()));
    this.setBuyPrice(addAuctionRequest.getBuyPrice());
    this.setBidPrice(addAuctionRequest.getBidPrice());
    this.setWarranty(addAuctionRequest.getWarranty());
    this.setStepPrice(addAuctionRequest.getStepPrice());
    this.setArea(addAuctionRequest.getArea());
    this.setAttendanceDeadline(Utils.setTimeZone(addAuctionRequest.getAttendanceDeadline()));
    this.setSeller(addAuctionRequest.getSeller());
    this.setType(addAuctionRequest.getType());
    this.setPercent(addAuctionRequest.getPercent());
    this.setRegistrationFee(addAuctionRequest.getRegistrationFee());
    this.setAttendingUser(addAuctionRequest.getAttendingUser());
    this.setSellOffPercent(addAuctionRequest.getSellOffPercent());
  }

  public long getAddressId() {
    return addressId;
  }

  public void setAddressId(long addressId) {
    this.addressId = addressId;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
  }
}
