package vn.acgroup.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.SavedWithdrawInfo;

@Repository
public interface SavedWithdrawInfoRepository extends CrudRepository<SavedWithdrawInfo, Long> {

	Optional<SavedWithdrawInfo> findByUserAndType(long user, String type);

}