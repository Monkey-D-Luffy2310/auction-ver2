package vn.acgroup.controllers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.BidRequest;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.bid.BidService;
import vn.acgroup.service.wallet.WalletService;
import vn.acgroup.service.auction.AuctionService;

import vn.acgroup.service.googleSheet.GoogleService;

@Controller
@CrossOrigin(origins = "*")
public class BidController {

  Logger logger = LoggerFactory.getLogger(BidController.class);

  @Autowired UserRepository userRepository;

  @Autowired AssetRepository assetRepository;

  @Autowired BidRepository bidRepository;

  @Autowired AuctionRepository auctionRepository;

  @Autowired RestTemplate emqxClient;

  @Autowired BidService bidservice;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired WalletService walletService;

  @Autowired AuctionService auctionService;

  @Autowired GoogleService googleService;

  @PostMapping("/bid") // bid request
  @ResponseBody
  @ApiOperation(
      value = "/bid",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> bidSubmit(@RequestBody BidRequest bidRequest)
      throws InterruptedException, ExecutionException, CustomException, JsonProcessingException {

    long start = System.currentTimeMillis();
    if (isBidIdExist(bidRequest.getAuction())) {
      throw new CustomException(
          "Hệ thống đang xử lý thao tác đấu giá của khách hàng. Vui lòng thục hiện lại thao tác!",
          HttpStatus.FORBIDDEN.value());
    }

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
    logger.info(
        "user "
            + user.getEmail()
            + " biding aution: "
            + bidRequest.getAuction()
            + " price: "
            + bidRequest.getBidPrice());

    Optional<Auction> auctionOptional = auctionRepository.findById(bidRequest.getAuction());
    Auction auction = auctionOptional.get();
    Asset asset = assetRepository.findById(auction.getAssest().getId()).get();

    switch (auction.getType()) {
      case "Reverse":
        bidservice.bidReverse(user, asset, auctionOptional, bidRequest);
        break;
      case "Normal":
        if (bidservice.isValidNormalBid(user, asset, auctionOptional, bidRequest))
          bidservice.bid(user, asset, auction, bidRequest);
        break;
      default:
        break;
    }

    logger.info("all time: " + (System.currentTimeMillis() - start));
    return new ResponseEntity<String>("OK", HttpStatus.OK);
  }

  // @PostMapping("/bid/reverse") // bid request
  // @ResponseBody
  // @ApiOperation(value = "/bid/reverse", authorizations = { @Authorization(value
  // = "JWT") })
  // public ResponseEntity bidReverse(@RequestBody BidRequest bidRequest) {
  // Object principal =
  // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  // User user = userRepository.findByEmailAndIsActive((String) principal,
  // true).get();
  // if (!userRepository.existsById(user.getId()))
  // return new ResponseEntity<>("Owner not found", HttpStatus.FORBIDDEN);
  // Long auction_id = bidRequest.getAuction();
  // Optional<Auction> auctionOptional = auctionRepository.findById(auction_id);
  // if (!auctionOptional.isPresent() || auctionOptional.get().getStatus() == "đã
  // xoá")
  // return new ResponseEntity<>("Không tìm thấy đấu giá", HttpStatus.NOT_FOUND);
  // if (!auctionOptional.get().getStatus().equals("Active"))
  // return new ResponseEntity<>("Đấu giá chưa diễn ra", HttpStatus.FORBIDDEN);
  // if (!(auctionRegisterRepository.findByUserAndAuctionAndIsDeleted(user,
  // auctionOptional.get(),
  // false))
  // .isPresent())
  // return new ResponseEntity<>("Chưa đăng kí đấu giá", HttpStatus.FORBIDDEN);
  //
  // return new ResponseEntity<>("OK", HttpStatus.OK);
  // }

  @GetMapping("/bid/user/{id}") // find bid by owner_id
  @ResponseBody
  @ApiOperation(
      value = "getBidByUser",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Bid> byUser(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    return bidRepository.findByUser(userRepository.findById(id));
  }

  @GetMapping("/bid/auction/{id}") // find bid by auction
  @ResponseBody
  @ApiOperation(
      value = "getBidByAuction",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Bid> byAuction(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    return bidRepository.findByAuction(auctionRepository.findById(id).get());
  }

  @GetMapping("/bid") // find all bid
  @ResponseBody
  @ApiOperation(
      value = "/bid",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Bid> getAll() throws InterruptedException, ExecutionException {
    return bidRepository.findAll();
  }

  @PostMapping("/bid/buy")
  @ResponseBody
  @ApiOperation(
      value = "buyBidNumber",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> buy(long bidNumber)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
    user.setBidTurn(user.getBidTurn() + bidNumber);
    BigDecimal pricePerBid = BigDecimal.valueOf(1000);
    BigDecimal amount = pricePerBid.multiply(new BigDecimal(bidNumber));
    if (amount.compareTo(user.getVNDTBalance()) <= 0) {
      WithdrawRequest withdrawRequest =
          new WithdrawRequest(LightwalletConfig.ticketAddress, amount, "Mua lượt đấu giá");
      walletService.sendVNDT(user, withdrawRequest);
    }
    userRepository.save(user);
    return new ResponseEntity<String>("OK", HttpStatus.OK);
  }

  public static ConcurrentHashMap<Long, Long> bidIds = new ConcurrentHashMap<Long, Long>();

  public static boolean isBidIdExist(long id) {
    long systemTime = System.nanoTime();
    System.out.println("system time:" + systemTime);
    if (bidIds.containsKey(id)) { // lock user for 30s
      System.out.println("account time: " + bidIds.get(id));
      if (systemTime - bidIds.get(id) < 500000000l) {
        return true;
      } else {
        bidIds.put(id, systemTime);
        return false;
      }
    } else {
      bidIds.put(id, systemTime);
      return false;
    }
  }

  public static void removeAccountId(long id) {
    System.out.println("remove " + id);
    bidIds.remove(id);
  }
}
