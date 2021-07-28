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

import vn.acgroup.controllers.api.FeedbackRequest;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_generator")
  @SequenceGenerator(
      name = "feedback_generator",
      sequenceName = "feedback_seq",
      allocationSize = 500)
  private long id;

  private long user;
  private long auction;
  private int star;
  private String status = "pending";

  private String comment;

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUser() {
    return user;
  }

  public void setUser(long user) {
    this.user = user;
  }

  public long getAuction() {
    return auction;
  }

  public void setAuction(long auction) {
    this.auction = auction;
  }

  public int getStar() {
    return star;
  }

  public void setStar(int star) {
    this.star = star;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Feedback() {};

  public Feedback(FeedbackRequest request, long user) {
    this.user = user;
    this.auction = request.getAuction();
    this.comment = request.getComment();
    this.star = request.getStar();
  };
}
