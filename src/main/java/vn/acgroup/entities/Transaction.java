package vn.acgroup.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import vn.acgroup.controllers.lightwallet.Data;
import vn.acgroup.controllers.lightwallet.WithdrawResponse;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_generator")
  @SequenceGenerator(
      name = "transaction_generator",
      sequenceName = "transaction_seq",
      allocationSize = 500)
  private long id;

  @Column(nullable = true)
  private long fromUser;

  private String fromAddress;

  @Column(nullable = true)
  private long toUser;

  private String note;
  private String toAddress;
  private String status;
  private String currency;
  private BigDecimal amount;
  private String hash;

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getFromUser() {
    return fromUser;
  }

  public void setFromUser(long fromUser) {
    this.fromUser = fromUser;
  }

  public long getToUser() {
    return toUser;
  }

  public void setToUser(long toUser) {
    this.toUser = toUser;
  }

  public String getToAddress() {
    return toAddress;
  }

  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public Transaction() {}

  public Transaction(long user_id, WithdrawResponse withdrawResponse) {
    this.setAmount(withdrawResponse.getAmount());
    this.setFromUser(user_id);
    this.setToAddress(withdrawResponse.getTo());
    this.setNote(withdrawResponse.getDescription());
    this.setCurrency(withdrawResponse.getType());
    this.setHash(withdrawResponse.getHash());
    this.setStatus("Pending");
  }

  public Transaction(User user, WithdrawResponse withdrawResponse) {
    this(user.getId(), withdrawResponse);
  }

  public Transaction(long toUser, Data data) {
    this.setHash(data.getHash());
    this.setCurrency("VNDT");
    this.setAmount(new BigDecimal(data.getAmount()));
    this.setStatus(data.getStatus());
    this.setFromAddress(data.getFromAddress());
    this.setToAddress(data.getToAddress());
    this.setToUser(toUser);
    this.setNote("Nạp tiền");
  }
}
