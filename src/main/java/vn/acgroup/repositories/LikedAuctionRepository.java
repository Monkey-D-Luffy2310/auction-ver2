package vn.acgroup.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.LikedAuction;
import vn.acgroup.entities.User;

@Repository
public interface LikedAuctionRepository extends CrudRepository<LikedAuction, Long> {

  Optional<Iterable<LikedAuction>> findByUser(User user);

  Optional<Iterable<LikedAuction>> findTop3ByUserAndIsDelete(User user, boolean b);

  Optional<Iterable<LikedAuction>> findByAuctionAndIsDelete(Auction auction, boolean b);

  Optional<Iterable<LikedAuction>> findByUserAndAuction(User user, Auction auction);

  Optional<LikedAuction> findByUserAndAuctionAndIsDelete(
      User user, Auction auction, boolean isDelete);

  Optional<Iterable<LikedAuction>> findByUserAndIsDelete(User user, boolean b);

  Iterable<LikedAuction> findByIsDelete(boolean b);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "update liked_auction set is_delete = 1 where auction_id = ?1")
  public void deleteLikeAuction(long auctionId);
}
