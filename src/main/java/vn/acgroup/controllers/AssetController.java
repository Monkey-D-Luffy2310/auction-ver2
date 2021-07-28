package vn.acgroup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.AddAssetRequest;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.CategoryRepository;
import vn.acgroup.repositories.UserRepository;

@Controller
@CrossOrigin(origins = "*")
public class AssetController {

  @Autowired AssetRepository assetRepository;
  @Autowired UserRepository userRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired CategoryRepository categoryRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @PostMapping(value = "/asset")
  @ResponseBody
  @ApiOperation(
      value = "add",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> add(@RequestBody AddAssetRequest addAssetRequest)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> owner = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!owner.isPresent()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    Asset asset = new Asset();
    asset.mapAsset(addAssetRequest);
    asset.setUser(owner.get());
    assetRepository.save(asset);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @GetMapping(value = "/asset")
  @ResponseBody
  public Iterable<Asset> getAll() throws InterruptedException, ExecutionException {

    return assetRepository.findAll();
  }

  @GetMapping(value = "/asset/id/{id}")
  @ResponseBody
  public ResponseEntity getAsset(@PathVariable Long id)
      throws InterruptedException, ExecutionException {
    try {
      Asset asset = assetRepository.findById(id).get();
      User user = asset.getUser();
      List<Asset> assets = new ArrayList<>();
      assets.add(asset);
      user.setAssets(assets);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/asset/byCategory/{category}")
  @ResponseBody
  public Iterable<Asset> getAssetByCategory(@PathVariable String category)
      throws InterruptedException, ExecutionException {
    return assetRepository.findByCategory(category);
  }

  @PostMapping(value = "/asset/updateStatus/{id}")
  @ApiOperation(
      value = "status",
      authorizations = {@Authorization(value = "JWT")})
  @ResponseBody
  public ResponseEntity<String> setStatus(@PathVariable long id, String status)
      throws InterruptedException, ExecutionException {
    try {
      Asset asset = assetRepository.findById(id).get();
      asset.setStatus(status);
      assetRepository.save(asset);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/asset/status/{status}")
  @ResponseBody
  @ApiOperation(
      value = "assetStatus",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Asset> getAssetByStatus(@PathVariable String status)
      throws InterruptedException, ExecutionException {
    return assetRepository.findByStatus(status);
  }

  @GetMapping(value = "/asset/auction_status/{id}") // id Of Auction
  @ResponseBody
  @ApiOperation(
      value = "find Assets which in new auction",
      authorizations = {@Authorization(value = "JWT")})
  public Asset findAssetbyAuctionStatus(@PathVariable Long id)
      throws InterruptedException, ExecutionException {
    return assetRepository.findAssetByAuctions_Id(id);
  }

  @PutMapping(value = "/asset/update/{id}")
  @ResponseBody
  @ApiOperation(
      value = "update",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> update(
      @PathVariable Long id, @RequestBody AddAssetRequest addAssetRequest)
      throws InterruptedException, ExecutionException {
    try {
      Asset asset = assetRepository.findById(id).get();
      asset.mapAsset(addAssetRequest);
      assetRepository.save(asset);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
