package vn.acgroup.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Address;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

  Optional<Iterable<Address>> findByUser(Long user_id);

  Optional<Address> findByUserAndIsDefault(Long id, boolean isdefault);

  Optional<Address> findByUserAndAddress(Long id, String string);
}
