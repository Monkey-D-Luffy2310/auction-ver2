package vn.acgroup.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.User;

@Repository
public interface AuctionRegisterRepository extends CrudRepository<AuctionRegister, Long> {

  Optional<Iterable<AuctionRegister>> findByUser(User user);

  Optional<Iterable<AuctionRegister>> findByUserAndIsDeleted(User user, boolean b);

  Optional<Iterable<AuctionRegister>> findTop3ByUserAndIsDeletedAndAuction_statusOrderByUpdatedDesc(
      User user, boolean b, String a);

  Optional<Iterable<AuctionRegister>> findByUserAndIsDeletedAndAuction_status(
      User user, boolean b, String a);

  Optional<Iterable<AuctionRegister>> findTop3ByUserAndIsDeletedOrderByUpdatedDesc(
      User user, boolean b);

  Optional<Iterable<AuctionRegister>> findByAuction(Auction auction);

  Optional<Iterable<AuctionRegister>> findByAuction_IdAndIsDeleted(long id, boolean b);

  Optional<Iterable<AuctionRegister>> findByAuctionAndIsDeleted(Auction auction, boolean b);

  Optional<AuctionRegister> findByUserAndAuction(User user, Auction auction);

  Optional<AuctionRegister> findByUserAndAuctionAndIsDeleted(User user, Auction auction, boolean b);

  Iterable<AuctionRegister> findByIsDeleted(boolean b);

  Iterable<AuctionRegister> findByUser_IdOrderByUpdatedDesc(Long id);

  Optional<AuctionRegister> findByUser_IdAndAuction_Id(long idu, long ida);
  
  Iterable<AuctionRegister> findByUserIdAndIsDeletedAndAuctionIdIn(long userId,boolean isDeleted,List<Long> ids);

  @Query(nativeQuery = true, value = "SELECT SUM(turn) FROM auction_register WHERE auction_id = ?1")
  int findTurnByAuction_Id(long id);

  @Query(
      nativeQuery = true,
      value =
          "SELECT * FROM auction_register WHERE user_id = ?1 AND warranty > 0 ORDER BY updated DESC")
  Iterable<AuctionRegister> findBiddingAuction(long id);

  @Query(
      nativeQuery = true,
      value =
          "SELECT COUNT(user_id) FROM auction_register WHERE auction_id = ?1 AND is_deleted = false")
  int findAttendingUser(long auctionId);
  
  
  @Modifying
  @Transactional
  @Query(
      nativeQuery = true,
      value =
          "update auction_register set depositing = 1 where user_id = ?1  and auction_id in ?2")
  public void updateAuctionRegisterPayList(long userId,List<Long> auctionId);
  
}
