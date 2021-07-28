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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.LikedAuction;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.LikedAuctionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.mail.MailService;

@Controller
@CrossOrigin(origins = "*")
public class LikedAuctionController {

  @Autowired UserRepository userRepository;
  @Autowired AssetRepository assetRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired LikedAuctionRepository likedAuctionRepository;
  @Autowired MailService mailService;

  @GetMapping(value = "/user/liked_auction/add/{id}")
  @ResponseBody
  @ApiOperation(
      value = "addLikedAuction",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> likedAuction(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
      Auction auction = auctionRepository.findById(id).get();
      Boolean isExist = likedAuctionRepository.findByUserAndAuction(user, auction).isPresent();
      if (!isExist) {
        LikedAuction like = new LikedAuction();
        like.setAuction(auction);
        like.setUser(user);
        like.setDelete(false);
        likedAuctionRepository.save(like);
        return new ResponseEntity<>("OK", HttpStatus.OK);

      } else {
        try {
          LikedAuction like =
              likedAuctionRepository.findByUserAndAuctionAndIsDelete(user, auction, true).get();
          like.setDelete(false);
          likedAuctionRepository.save(like);
          return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
          return new ResponseEntity<>("Existed", HttpStatus.OK);
        }
      }
    } catch (Exception e) {
      return new ResponseEntity<>("Error : " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/user/liked_auction")
  @ResponseBody
  @ApiOperation(
      value = "allLikedAuctionOfUser",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity getAllLikedAuction() throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      return new ResponseEntity<>(
          likedAuctionRepository.findByUserAndIsDelete(optional.get(), false), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping(value = "/user/liked_auction/auction/{id}")
  @ResponseBody
  @ApiOperation(
      value = "deleteLikedAuctionByAuctionId",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity deleteLikedAuction(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    User user = optional.get();
    Auction auction = auctionRepository.findById(id).get();
    LikedAuction like =
        likedAuctionRepository.findByUserAndAuctionAndIsDelete(user, auction, false).get();
    like.setDelete(true);
    likedAuctionRepository.save(like);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @DeleteMapping(value = "/user/liked_auction")
  @ResponseBody
  @ApiOperation(
      value = "deleteAllLikedAuctionOfUser",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity deleteLike(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      User user = optional.get();
      Iterable<LikedAuction> likes =
          likedAuctionRepository.findByUserAndIsDelete(user, false).get();
      for (LikedAuction like : likes) {
        like.setDelete(true);
      }
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/user/like/user/{id}")
  @ResponseBody
  @ApiOperation(value = "get all auction which liked by user_id")
  public ResponseEntity getAllByUserId(@PathVariable Long id)
      throws InterruptedException, ExecutionException {
    try {
      User user = userRepository.findById(id).get();
      List<Auction> list = new ArrayList<>();
      likedAuctionRepository
          .findByUserAndIsDelete(user, false)
          .get()
          .forEach(
              like -> {
                list.add(like.getAuction());
              });
      return new ResponseEntity<>(list, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/user/like/auction/{id}")
  @ResponseBody
  @ApiOperation(value = "get all user liked auction by auction_id")
  public ResponseEntity getAllByAuctionId(@PathVariable Long id)
      throws InterruptedException, ExecutionException {
    try {
      Auction auction = auctionRepository.findById(id).get();
      List<User> list = new ArrayList<>();
      likedAuctionRepository
          .findByAuctionAndIsDelete(auction, false)
          .get()
          .forEach(
              like -> {
                list.add(like.getUser());
              });
      return new ResponseEntity<>(list, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }
}
