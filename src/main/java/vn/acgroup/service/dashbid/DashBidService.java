package vn.acgroup.service.dashbid;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.acgroup.dto.AutoBidDto;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.service.autobid.AutoBiddingService;
import vn.acgroup.service.scheduler.SchedulerService;

@Service
public class DashBidService {
  private final Logger log = LoggerFactory.getLogger(DashBidService.class);

  @Autowired SchedulerService scheduler;

  @Autowired AutoBiddingService autobidService;

  @Autowired LastBidCacheManager lastBidHandler;

  @Autowired AuctionRepository auctionRepo;

  @PostConstruct
  public void init() {
    Iterable<Auction> auctions =
        auctionRepo.findByWinnerAndStatusNotAndDashEndInIsNotNull(0L, "Ending");

    for (Auction auction : auctions) {
      try {
        createNewTask(
            auction, LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), auction.getDashEndIn());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * create new dash bid
   *
   * @param auction
   * @param bid
   */
  public void createBid(Auction auction, Bid bid) {
    if (lastBidHandler.bidIsValid(auction, bid)) {
      lastBidHandler.updateLastBid(auction, bid);
      try {
        createNewTask(
            auction, LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), auction.getDashEndIn());

      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * create dash bid auto bid
   *
   * @param bid
   * @param biddedUser
   */
  public void createAutoBid(AutoBidDto bid, User biddedUser) {
    autobidService.addAutoBid(bid, biddedUser);
  }

  /**
   * remove dash bid auto bid
   *
   * @param bid
   * @param biddedUser
   */
  public void removeAutoBidDto(AutoBidDto bid, User biddedUser) {
    autobidService.removeAutoBid(bid);
  }

  protected void createNewTask(Auction auction, LocalDateTime now, int execIn) {
    String id = String.valueOf(auction.getId());
    scheduler.cancelTask(id);

    Instant execTime = now.plusSeconds(execIn).atZone(ZoneId.systemDefault()).toInstant();

    scheduler.createTask(id, endBidTask(auction), execTime);
  }

  /**
   * Task to execute when bidding time is over
   *
   * <p>If there are available autobidder than update last bid as winning autobidder. Otherwise end
   * the auction with last bid as the winner
   *
   * @param auction
   * @return
   */
  protected Runnable endBidTask(Auction auction) {
    return new Runnable() {
      @Override
      public void run() {
        Collection<AutoBidDto> autoBids = autobidService.getAutoBid(auction.getId());

        AutoBidDto winningBid = autobidService.getWinningAutoBid(autoBids);

        if (winningBid == null) {
          // TODO Implement endiong bid logic here

        } else {
          autobidService.updateBiddingAutoBid(autoBids, auction.getId());
          try {
            createNewTask(
                auction, LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), auction.getDashEndIn());
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        }
      }
    };
  }
}
