package vn.acgroup.service.dashbid;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Bid;

@Component
public class LastBidCacheManager {

  /**
   * get last bid from auction
   *
   * @param auctionId
   * @return last bid
   */
  @Cacheable(cacheNames = "lastBid", key = "#auctionId")
  public Bid getLastBid(String auctionId) {
    return null;
  }

  /**
   * update auction
   *
   * @param auction
   * @param bid
   * @return bid after updated
   */
  @CachePut(cacheNames = "lastBid", key = "#auction.getId")
  public Bid updateLastBid(Auction auction, Bid bid) {
    return bid;
  }

  /**
   * @param auction
   * @param bid
   * @return if the bid is valid to be winner bid
   */
  public boolean bidIsValid(Auction auction, Bid bid) {
    return false;
  }
}
