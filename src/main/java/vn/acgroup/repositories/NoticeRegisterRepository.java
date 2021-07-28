package vn.acgroup.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.NoticeRegister;

@Repository
public interface NoticeRegisterRepository extends CrudRepository<NoticeRegister, Long> {

  Optional<NoticeRegister> findByEmail(String email);

  Optional<NoticeRegister> findByEmailAndIsVerify(String email, boolean b);
}
