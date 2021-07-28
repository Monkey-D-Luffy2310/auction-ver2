package vn.acgroup.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.acgroup.entities.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  public Optional<User> findByEmail(String email);

  public Optional<User> findByWalletAddress(String address);

  public Optional<User> findByBonusAddress(String address);

  public User findUserByAuctions_Id(long id);

  public User findByAssets_Id(Long id);

  public List<User> findBySponsorOrderByCreatedDesc(long id);

  public Optional<User> findByEmailAndIsActive(String email, boolean isActive);

  List<User> findByIsActive(boolean isActive);

  @Query("select u from User u where u.email = ?1")
  User findByEmailAddress(String email);
}
