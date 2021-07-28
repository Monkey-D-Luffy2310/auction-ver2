package vn.acgroup.controllers.api;

import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.User;

public class FindNewAuctionResponse {
  private Auction auction;
  private Asset asset;
  private User user;

  public Auction getAuction() {
    return auction;
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
  }

  public Asset getAsset() {
    return asset;
  }

  public void setAsset(Asset asset) {
    this.asset = asset;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
