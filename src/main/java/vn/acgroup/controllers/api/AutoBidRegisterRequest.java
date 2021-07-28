package vn.acgroup.controllers.api;

public class AutoBidRegisterRequest {

  private long auction_id;
  private long maxBid;

  public long getAuction_id() {
    return auction_id;
  }

  public void setAuction_id(long auction_id) {
    this.auction_id = auction_id;
  }

  public long getMaxBid() {
    return maxBid;
  }

  public void setMaxBid(long maxBid) {
    this.maxBid = maxBid;
  }
}
