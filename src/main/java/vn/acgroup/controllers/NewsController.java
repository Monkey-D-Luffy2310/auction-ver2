package vn.acgroup.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.AddNewsRequest;
import vn.acgroup.entities.News;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.NewsRepository;

@RestController
@CrossOrigin(origins = "*")
public class NewsController {

  @Autowired AssetRepository assetRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired UserRepository userRepository;
  @Autowired NewsRepository newsRepository;
  @Autowired AddressRepository addressRepository;

  @PostMapping(value = "/news/add")
  @ResponseBody
  @ApiOperation(
      value = "add news",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity edit(@RequestBody AddNewsRequest addNewsRequest)
      throws InterruptedException, ExecutionException {
    try {
      News entity = new News();
      entity.setTittle(addNewsRequest.getTittle());
      entity.setSummary(addNewsRequest.getSummary());
      entity.setContent(addNewsRequest.getContent());
      entity.setImage(addNewsRequest.getImage());
      entity.setType(addNewsRequest.getType());
      entity.setAlias(addNewsRequest.getAlias());
      newsRepository.save(entity);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error :" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/news")
  @ResponseBody
  @ApiOperation(
      value = "get all news",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity getAllNews() throws InterruptedException, ExecutionException {
    try {
      return new ResponseEntity<>(newsRepository.findAll(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error :" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping(value = "/news/edit/{id}")
  @ResponseBody
  @ApiOperation(
      value = "edit",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity edit(@PathVariable long id, @RequestBody AddNewsRequest addNewsRequest)
      throws InterruptedException, ExecutionException {
    try {
      News entity = newsRepository.findById(id).get();
      entity.setTittle(addNewsRequest.getTittle());
      entity.setSummary(addNewsRequest.getSummary());
      entity.setContent(addNewsRequest.getContent());
      entity.setImage(addNewsRequest.getImage());
      entity.setType(addNewsRequest.getType());
      entity.setAlias(addNewsRequest.getAlias());
      newsRepository.save(entity);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error :" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/new/{id}")
  @ResponseBody
  @ApiOperation(
      value = "get new by id",
      authorizations = {@Authorization(value = "JWT")})
  public News getNewById(@PathVariable long id) throws InterruptedException, ExecutionException {
    return newsRepository.findById(id).get();
  }

  @DeleteMapping(value = "/new/{id}")
  @ResponseBody
  @ApiOperation(
      value = "delete",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> delete(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    if (!newsRepository.findById(id).isPresent())
      throw new CustomException("Không tìm thấy tin tức.", HttpStatus.FORBIDDEN.value());
    else {
      newsRepository.deleteById(id);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }
  }

  @GetMapping(value = "/findNewByAlias/{alias}")
  @ResponseBody
  @ApiOperation(
      value = "find news by alias",
      authorizations = {@Authorization(value = "JWT")})
  public News getNewByAlias(@PathVariable String alias)
      throws InterruptedException, ExecutionException {
    return newsRepository.findByAlias(alias);
  }

  @GetMapping(value = "/findNewByType/{type}")
  @ResponseBody
  @ApiOperation(
      value = "find news by type",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity getNewByType(@PathVariable String type)
      throws InterruptedException, ExecutionException {
    try {
      return new ResponseEntity<>(newsRepository.findByType(type), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error :" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
