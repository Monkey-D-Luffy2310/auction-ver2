package vn.acgroup.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.AutoBid;

@Repository
public interface AutoBidRepository extends CrudRepository<AutoBid, Long> {

  public AutoBid findTop1ByAuctionIdAndIsActiveOrderByCreatedDesc(long id, boolean b);
}
