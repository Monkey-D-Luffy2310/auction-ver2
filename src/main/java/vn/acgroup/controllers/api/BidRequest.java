package vn.acgroup.controllers.api;

import java.math.BigDecimal;

public class BidRequest {

  private BigDecimal bidPrice;
  private long auction;

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(BigDecimal bidPrice) {
    this.bidPrice = bidPrice;
  }

  public long getAuction() {
    return auction;
  }

  public void setAuction(long auction) {
    this.auction = auction;
  }
}
