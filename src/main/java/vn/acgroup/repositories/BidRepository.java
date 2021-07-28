package vn.acgroup.repositories;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;

@Repository
public interface BidRepository extends CrudRepository<Bid, Long> {
  public Iterable<Bid> findByUser(Optional<User> optional);

  public Iterable<Bid> findByAuction(Auction optional);

  public int countByAuction(Auction optional);

  public Iterable<Bid> findByAuction_id(long id);

  public Iterable<Bid> findTop30ByAuction_idOrderByCreatedDesc(long id);

  public Optional<Bid> findTop1ByAuction_idAndStatusOrderByCreatedDesc(long id, String status);

  @Query(nativeQuery = true, value = "SELECT id FROM bid WHERE auction_id = ?1")
  long find(long id);

  @Cacheable(cacheNames = "lastDashBid", key = "auctionId")
  public Bid findTop1ByAuction_idOrderByCreatedDesc(long auctionId);

  public Bid findByAuction_idOrderByCreatedDesc(long auctionId);
}
