package vn.acgroup.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Feedback;
import vn.acgroup.entities.User;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

  Optional<Feedback> findByUserOrderByCreatedDesc(long user);

  Optional<Feedback> findByAuctionOrderByCreatedDesc(long auction);

  Optional<Feedback> findByUserAndAuctionOrderByCreatedDesc(long user, long auction);
  
  @Query(nativeQuery = true, value = "SELECT * FROM feedback order by created desc limit ?1")
  List<Feedback> findAllOrderByCreatedLimit(long limit);
  
  @Query(nativeQuery = true, value = "SELECT * FROM feedback where user = ?1 and auction = ?2")
  List<Feedback> findFeedbackByUser(long user, long auction);

}
