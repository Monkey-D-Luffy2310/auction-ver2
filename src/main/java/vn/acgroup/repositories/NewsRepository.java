package vn.acgroup.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.News;

@Repository
public interface NewsRepository extends CrudRepository<News, Long> {
  public Iterable<News> findAllByOrderByCreatedDesc();

  public News findByAlias(String alias);

  public News findTop1ByOrderByCreatedDesc();

  public Iterable<News> findByType(String type);

  public Iterable<News> findTop8ByOrderByCreatedDesc();

  public Iterable<News> findTop3ByOrderByCreatedDesc();

  public Iterable<News> findTop3ByOrderByViewDesc();

  public Iterable<News> findTop6ByOrderByCreatedDesc();

  //  public Iterable<News> findTop5OrderByRandom();

  @Query(nativeQuery = true, value = "SELECT * FROM news ORDER BY RAND() LIMIT 8")
  Iterable<News> findRandomNewsTop8();

  @Query(nativeQuery = true, value = "SELECT * FROM news ORDER BY RAND() LIMIT 2")
  Iterable<News> findRandomNewsTop2();
}
