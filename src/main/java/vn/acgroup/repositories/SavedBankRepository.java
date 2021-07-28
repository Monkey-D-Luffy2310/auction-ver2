package vn.acgroup.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.SavedBank;

@Repository
public interface SavedBankRepository extends CrudRepository<SavedBank, String> {}
