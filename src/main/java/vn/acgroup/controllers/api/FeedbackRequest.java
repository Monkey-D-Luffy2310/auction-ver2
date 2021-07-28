package vn.acgroup.controllers.api;

public class FeedbackRequest {

  private long auction;
  private int star;
  private String comment;
  private String status;

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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
