package vn.acgroup.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

  Optional<Transaction> findByHash(String hash);

  Iterable<Transaction> findByToUserOrderByCreatedDesc(long id);

  Iterable<Transaction> findByFromUserOrderByCreatedDesc(long id);

  Iterable<Transaction> findByFromUserOrToUserOrderByCreatedDesc(long id1, long id2);

  @Query(
      nativeQuery = true,
      value =
          "select a.* from transaction a "
              + " where "
              + " ((?1 = '') OR DATE(a.created) >= STR_TO_DATE( ?1 ,'%Y-%m-%d')) and "
              + " ((?2 = '') OR DATE(a.created) <= STR_TO_DATE( ?2 ,'%Y-%m-%d')) and "
              + " (a.from_user = ?3 OR a.to_user = ?3)  order by a.created desc")
  List<Map<String, Object>> findByCreatedAndUser(String startAt, String endAt, long id);
}
