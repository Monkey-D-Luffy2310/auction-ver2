package vn.acgroup.service.autobid;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import vn.acgroup.dto.AutoBidDto;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AutoBidRepository;

@Service
public class AutoBiddingService {
  @Autowired AutoBidRepository autoBidRepository;
  /**
   * @param bids - collections of autobid
   * @return null if there are no winning bid else return the winning bid
   */
  public AutoBidDto getWinningAutoBid(Collection<AutoBidDto> bids) {

    return null;
  }

  // get autobid information
  // TODO: get list of auto bid dto
  @Cacheable(cacheNames = "autoBid", key = "#auctionId")
  public Collection<AutoBidDto> getAutoBid(Long auctionId) {
    return null;
  }

  // update bidding after applied
  // TODO: update autobids and return list of dto
  @CachePut(cacheNames = "autoBid", key = "#auctionId")
  public Collection<AutoBidDto> updateBiddingAutoBid(
      Collection<AutoBidDto> autoBids, Long auctionId) {
    return null;
  }

  @CacheEvict(cacheNames = "autoBid", key = "#bid.auction")
  public void addAutoBid(AutoBidDto bid, User biddedUser) {
    // TODO: add auto bid to list, you have to return list of autobid for auction after
  }

  @CacheEvict(cacheNames = "autoBid", key = "#autoBid.getId")
  public void removeAutoBid(AutoBidDto autoBid) {
    // TODO: delete autobid
  }
}
