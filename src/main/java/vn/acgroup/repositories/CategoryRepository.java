package vn.acgroup.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

  public Iterable<Category> findTop6ByOrderByCreatedDesc();

  public Iterable<Category> findTop4ByOrderByCreatedDesc();

  public Category findByAlias(String alias);
}
