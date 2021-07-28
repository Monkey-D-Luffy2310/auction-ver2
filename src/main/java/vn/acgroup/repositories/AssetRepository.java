package vn.acgroup.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vn.acgroup.entities.Asset;
import vn.acgroup.entities.User;

@Repository
public interface AssetRepository extends CrudRepository<Asset, Long> {

  public Iterable<Asset> findByUserId(Long id);

  public Iterable<Asset> findTop3ByUserId(Long id);

  public Iterable<Asset> findByCategory(String category);

  public Iterable<Asset> findByStatus(String status);

  public Asset findAssetByAuctions_Id(Long id);

  public Asset findByUser_id(Long id);

  public Iterable<Asset> findTop3ByUserOrderByCreatedDesc(User user);
}
