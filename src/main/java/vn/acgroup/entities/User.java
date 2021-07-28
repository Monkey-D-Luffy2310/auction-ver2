package vn.acgroup.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import vn.acgroup.controllers.api.EditUserInformation;
import vn.acgroup.controllers.api.RegisterRequest;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
  @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 500)
  private long id;

  private String email;
  private String avatar =
      "https://firebasestorage.googleapis.com/v0/b/achauauction.appspot.com/o/no-avatar.jpg?alt=media&token=5f0f91f1-b5f1-4222-9160-a8f8a51a1e02";

  private String password;
  private String name;
  private String fullname;
  private String lastname;
  private String mobile;
  private String gender;
  private LocalDate dateofbirth;
  private String province;
  private String resetToken;
  public Boolean isActive = false;
  private long sponsor;
  private BigDecimal TRXBalance;
  private String walletAddress;
  private String bonusAddress;
  private BigDecimal USDFBalance;

  // số tiền user nạp - cọc
  private BigDecimal VNDTBalance;

  // số tiền đặt cọc
  private BigDecimal VNDTFreeze;

  // số tiền được thưởng : Giftcode
  private BigDecimal BonusBalance = BigDecimal.ZERO;

  public BigDecimal getBonusBalance() {
    return BonusBalance;
  }

  public void setBonusBalance(BigDecimal bonusBalance) {
    BonusBalance = bonusBalance;
  }

  // code xác minh
  private String code;

  private long bidTurn = 0;

  public long getBidTurn() {
    return bidTurn;
  }

  public void setBidTurn(long bidTurn) {
    this.bidTurn = bidTurn;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

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

  private String group = "User";

  public String getWalletAddress() {
    return walletAddress;
  }

  public void setWalletAddress(String walletAddress) {
    this.walletAddress = walletAddress;
  }

  public BigDecimal getUSDFBalance() {
    return USDFBalance;
  }

  public void setUSDFBalance(BigDecimal uSDFBalance) {
    USDFBalance = uSDFBalance;
  }

  public BigDecimal getVNDTBalance() {
    return VNDTBalance;
  }

  public void setVNDTBalance(BigDecimal vNDTBalance) {
    VNDTBalance = vNDTBalance;
  }

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Asset> assets;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Auction> auctions;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Bid> bids;

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

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  // @JsonIgnore
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

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public String getResetToken() {
    return resetToken;
  }

  public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public List<Asset> getAssets() {
    return assets;
  }

  public void setAssets(List<Asset> assets) {
    this.assets = assets;
  }

  public List<Bid> getBids() {
    return bids;
  }

  public void setBids(List<Bid> bids) {
    this.bids = bids;
  }

  public List<Auction> getAuctions() {
    return auctions;
  }

  public void setAuctions(List<Auction> auctions) {
    this.auctions = auctions;
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

  public BigDecimal getTRXBalance() {
    return TRXBalance;
  }

  public void setTRXBalance(BigDecimal tRXBalance) {
    TRXBalance = tRXBalance;
  }

  public BigDecimal getVNDTFreeze() {
    return VNDTFreeze;
  }

  public void setVNDTFreeze(BigDecimal vNDTFreeze) {
    VNDTFreeze = vNDTFreeze;
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

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonBackReference
  private List<AuctionRegister> auction_register;

  public List<AuctionRegister> getAuction_register() {
    return auction_register;
  }

  public void setAuction_register(List<AuctionRegister> auction_register) {
    this.auction_register = auction_register;
  }

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonBackReference
  private List<LikedAuction> liked_auction;

  public List<LikedAuction> getLiked_auction() {
    return liked_auction;
  }

  public void setLiked_auction(List<LikedAuction> liked_auction) {
    this.liked_auction = liked_auction;
  }

  public User() {}

  public User(RegisterRequest registerRequest) {
    this.setEmail(registerRequest.getEmail());
    this.setPassword(registerRequest.getPassword());
    this.setName(registerRequest.getName());
    this.setIsActive(false);
    this.setVNDTBalance(BigDecimal.valueOf(0));
    this.setVNDTFreeze(BigDecimal.valueOf(0));
    this.setTRXBalance(BigDecimal.valueOf(0));
    this.setUSDFBalance(BigDecimal.valueOf(0));
    this.setBidTurn(0);
    this.setBonusBalance(BigDecimal.valueOf(0));
  }

  public void edit(EditUserInformation editUserInformation) {
    this.setAvatar(editUserInformation.getAvatar());
    this.setFullname(editUserInformation.getFullname());
    this.setLastname(editUserInformation.getLastname());
    this.setGender(editUserInformation.getGender());
    this.setDateofbirth(editUserInformation.getDateofbirth());
    this.setMobile(editUserInformation.getMobile());
    this.setProvince(editUserInformation.getProvince());
    this.setGroup(editUserInformation.getGroup());
  }

  public void freeze(BigDecimal amount) {
    this.VNDTBalance = this.VNDTBalance.subtract(amount);
    this.VNDTFreeze = this.VNDTFreeze.add(amount);
  }

  public void unfreeze(BigDecimal amount) {
    this.VNDTBalance = this.VNDTBalance.add(amount);
    this.VNDTFreeze = this.VNDTFreeze.subtract(amount);
  }

  public String getBonusAddress() {
    return bonusAddress;
  }

  public void setBonusAddress(String bonusAddress) {
    this.bonusAddress = bonusAddress;
  }

  public long getSponsor() {
    return sponsor;
  }

  public void setSponsor(long sponsor) {
    this.sponsor = sponsor;
  }

  @Override
  public String toString() {
    return "User [id="
        + id
        + ", email="
        + email
        + ", avatar="
        + avatar
        + ", password="
        + password
        + ", name="
        + name
        + ", fullname="
        + fullname
        + ", lastname="
        + lastname
        + ", mobile="
        + mobile
        + ", gender="
        + gender
        + ", dateofbirth="
        + dateofbirth
        + ", province="
        + province
        + ", resetToken="
        + resetToken
        + ", isActive="
        + isActive
        + ", TRXBalance="
        + TRXBalance
        + ", walletAddress="
        + walletAddress
        + ", bonusAddress="
        + bonusAddress
        + ", USDFBalance="
        + USDFBalance
        + ", VNDTBalance="
        + VNDTBalance
        + ", VNDTFreeze="
        + VNDTFreeze
        + ", BonusBalance="
        + BonusBalance
        + ", code="
        + code
        + ", bidTurn="
        + bidTurn
        + ", created="
        + created
        + ", updated="
        + updated
        + ", group="
        + group
        + ", assets="
        + assets
        + ", auctions="
        + auctions
        + ", bids="
        + bids
        + ", auction_register="
        + auction_register
        + ", liked_auction="
        + liked_auction
        + "]";
  }
}
