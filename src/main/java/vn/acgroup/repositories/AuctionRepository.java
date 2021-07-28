package vn.acgroup.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.acgroup.entities.Auction;

@Repository
public interface AuctionRepository extends CrudRepository<Auction, Long> {

  public Iterable<Auction> findByStatus(String status);

  public Iterable<Auction> findTop2ByStatusAndShowInBanerOrOrStatusAndShowInBanerOrderByStartAtAsc(
      String status1, char c1, String status2, char c2);

  public Iterable<Auction> findTop6ByStatusAndShowInBanerOrderByStartAtAsc(String status, char c);

  public Iterable<Auction> findTop2ByStatusOrderByStartAtAsc(String status);

  public Iterable<Auction> findByStatusOrderByCreatedAsc(String status);

  public Iterable<Auction> findTop6ByStatusOrderByEndAtDesc(String status);

  public Iterable<Auction> findByStatusOrderByStartAtAsc(String status);

  public Iterable<Auction> findByStatusOrderByBuyPriceAsc(String status);

  public Iterable<Auction> findByCategory(String category);

  public Iterable<Auction> findByCategoryOrderByStartAtDesc(String category);

  public Iterable<Auction> findByCategoryOrderByBuyPriceAsc(String category);

  public Iterable<Auction> findByUser_Id(Long id);

  public Iterable<Auction> findByAssest_id(Long id);

  public Iterable<Auction> findByWinner(Long id);

  public Iterable<Auction> findByWinnerOrderByEndAtDesc(Long id);

  public Iterable<Auction> findByWinnerAndStatusOrderByEndAtDesc(Long id, String status);

  public int countByWinnerAndStatus(Long id, String status);

  public Iterable<Auction> findByType(String string);

  public Iterable<Auction> findByTypeAndStatus(String type, String status);

  public Iterable<Auction> findByWinnerAndStatusNotAndDashEndInIsNotNull(
      Long winner, String status);

  public Iterable<Auction> findByStatusAndEndAtLessThan(String status, LocalDateTime endAt);

  public Iterable<Auction> findByStatusIn(ArrayList<String> status);

  public Iterable<Auction> findTop6ByStatusOrderByCreatedAsc(String string);

  public Iterable<Auction> findTop3ByStatusOrderByCreatedAsc(String string);

  public Iterable<Auction> findByStatusOrStatus(String string, String string2);

  public Iterable<Auction> findTop6ByStatusOrStatus(String string, String string2);

  public Object findTop3ByUserId(long id);

  public Iterable<Auction> findByIdIn(List<Long> ids);

  @Query(nativeQuery = true, value = "SELECT * FROM auction WHERE start_at LIKE ?1 AND winner > 0")
  Iterable<Auction> statistical(String startAt);

  @Query(
      nativeQuery = true,
      value =
          "SELECT a.id,a.win_price AS winPrice,a.status,c.name as asset,IFNULL(CONCAT( u.fullname ,' ',u.lastname),'null') AS name,u.name AS username,u.mobile,u.email,u.province AS address ,b.warranty AS deposit FROM auction a"
              + "	left join user u on a.winner = u.id"
              + " left join auction_register b on (a.winner = b.user_id and a.id = b.auction_id)"
              + "	left join asset c on c.id = a.assest_id"
              + " WHERE DATE(a.start_at) = STR_TO_DATE( ?1 ,'%Y-%m-%d') and a.winner > 0")
  List<Map<String, Object>> reportAuctionActive(String startAt);

  @Query(
      nativeQuery = true,
      value =
          "select a.*,b.name,b.images,(select count(id) from auction_register where auction_id = a.id) as attendees from auction a"
              + "	left join asset b on b.id = a.assest_id"
              + "	left join auction_register ar on ar.auction_id = a.id"
              + " 	left join user u on u.id = ar.user_id"
              + "	 where "
              + " 	((?1 = '') OR DATE(a.created) >= STR_TO_DATE( ?1 ,'%Y-%m-%d')) and"
              + " 	((?2 = '') OR DATE(a.created) <= STR_TO_DATE( ?2 ,'%Y-%m-%d')) and"
              + "  	u.id = ?3   order by a.created desc")
  List<Map<String, Object>> findByCreatedAndUser(String startAt, String endAt, long id);

  @Query(
      nativeQuery = true,
      value =
          "select a.*,(select count(id) from auction_register where auction_id = a.id) as attendees,"
              + "	b.images as assetImg,b.name as assetName,IFNULL(c.name,'') as winName from auction a"
              + " 	left join asset b on b.id = a.assest_id "
              + "	left join user c on c.id = a.winner "
              + " 	where    a.id = ?1")
  List<Map<String, Object>> findByAuctionId(long id);

  @Query(
      nativeQuery = true,
      value =
          "select a.*,(select count(id) from liked_auction where auction_id = a.id and is_delete = 0) as favorites, "
              + " (SELECT  COUNT(id) FROM auction_register WHERE  auction_id = a.id AND is_deleted = 0) AS participants,"
              + " b.name as asset_name,b.images as asset_image "
              + " from auction a left join asset b on b.id = a.assest_id where a.status = ?1 order by created limit ?2")
  List<Map<String, Object>> findAuctionByStatus(String status, long limit);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "update auction set status = ?1 where id in ?2")
  public void updateAuctionPayList(String status, List<Long> auctionId);

  @Query(
      nativeQuery = true,
      value =
          "select a.*, b.name, b.images from auction a left join asset b on b.id = a.assest_id"
              + " where a.status = \"Upcoming\" or a.status = \"Active\" order by rand() limit 6")
  List<Map<String, Object>> findLimit6RandomAuction();
}
